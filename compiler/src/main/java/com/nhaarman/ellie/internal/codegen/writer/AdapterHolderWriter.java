/*
 * Copyright (C) 2014 Michael Pardo
 * Copyright (C) 2014 Niek Haarman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nhaarman.ellie.internal.codegen.writer;

import android.util.SparseArray;

import com.google.common.collect.Sets;
import com.nhaarman.ellie.internal.AdapterHolder;
import com.nhaarman.ellie.BaseMigration;
import com.nhaarman.ellie.internal.ModelAdapter;
import com.nhaarman.ellie.internal.codegen.Registry;
import com.nhaarman.ellie.internal.codegen.element.MigrationElement;
import com.nhaarman.ellie.internal.codegen.element.ModelAdapterElement;
import com.nhaarman.ellie.internal.codegen.element.TypeAdapterElement;
import com.squareup.javawriter.JavaWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.nhaarman.ellie.internal.Package.PACKAGE_NAME;

public class AdapterHolderWriter implements SourceWriter<TypeElement> {

    private static final Set<Modifier> PUBLIC = EnumSet.of(Modifier.PUBLIC);
    private static final Set<Modifier> PRIVATE = EnumSet.of(Modifier.PRIVATE);
    private static final Set<Modifier> PUBLIC_FINAL = EnumSet.of(Modifier.PUBLIC, Modifier.FINAL);
    private static final Set<Modifier> PRIVATE_FINAL = EnumSet.of(Modifier.PRIVATE, Modifier.FINAL);

    private final Registry mRegistry;

    public AdapterHolderWriter(final Registry registry) {
        mRegistry = registry;
    }

    @Override
    public String createSourceName(final TypeElement element) {
        return AdapterHolder.IMPL_CLASS_FQCN;
    }

    @Override
    public void writeSource(final Writer writer, final TypeElement element) throws IOException {
        JavaWriter javaWriter = new JavaWriter(writer);
        javaWriter.setCompressingTypes(true);
        javaWriter.setIndent("    ");

        javaWriter.emitSingleLineComment("Generated by Ellie. Do not modify!");
        javaWriter.emitPackage(PACKAGE_NAME);

        writeImports(javaWriter);

        javaWriter.beginType(AdapterHolder.IMPL_CLASS_NAME, "class", PUBLIC_FINAL, null, "AdapterHolder");
        javaWriter.emitEmptyLine();

        writeFields(javaWriter);
        writeConstructor(javaWriter);
        writeCreateMigrations(javaWriter);
        writeCreateModelAdapters(javaWriter);
        writeCreateTypeAdapters(javaWriter);
        writeGetMigrations(javaWriter);
        writeGetModelAdapter(javaWriter);
        writeGetModelAdapters(javaWriter);
        writeGetTypeAdapter(javaWriter);

        javaWriter.endType();
    }

    private void writeImports(final JavaWriter writer) throws IOException {
        Set<String> imports = Sets.newHashSet(
                ArrayList.class.getName(),
                HashMap.class.getName(),
                List.class.getName(),
                Map.class.getName(),
                SparseArray.class.getName(),
                Collections.class.getName(),
                AdapterHolder.class.getName(),
                ModelAdapter.class.getName(),
                BaseMigration.class.getName()
        );

        List<TypeAdapterElement> typeAdapters = mRegistry.getTypeAdapterElements();
        for (TypeAdapterElement typeAdapter : typeAdapters) {
            imports.add(typeAdapter.getQualifiedName());
        }

        Map<Integer, MigrationElement> migrationElements = mRegistry.getMigrationElements();
        for (MigrationElement migrationElement : migrationElements.values()) {
            imports.add(migrationElement.getQualifiedName());
        }

        List<ModelAdapterElement> modelAdapterElements = mRegistry.getModelAdapterElements();
        for (ModelAdapterElement modelAdapterElement : modelAdapterElements) {
            imports.add(modelAdapterElement.getModelQualifiedName());
            imports.add(modelAdapterElement.getQualifiedName());
        }

        writer.emitImports(imports);
        writer.emitEmptyLine();
    }

    private void writeFields(final JavaWriter writer) throws IOException {
        writer.emitField(
                String.format("SparseArray<%s>", BaseMigration.class.getSimpleName()),
                "mMigrations",
                PRIVATE_FINAL,
                String.format("new SparseArray<>(%d)", mRegistry.getMigrationElements().size())
        );

        writer.emitField(
                "Map<Class<? extends Model>, ModelAdapter<?>>",
                "mModelAdapters",
                PRIVATE_FINAL,
                "new HashMap<>()"
        );

        writer.emitField(
                "Map<Class<?>, TypeAdapter<?, ?>>",
                "mTypeAdapters",
                PRIVATE_FINAL,
                "new HashMap<Class<?>, TypeAdapter<?, ?>>()"
        );

        writer.emitEmptyLine();
    }

    private void writeConstructor(final JavaWriter writer) throws IOException {
        writer.beginConstructor(EnumSet.of(Modifier.PUBLIC));

        writer.emitStatement("createMigrations()");
        writer.emitStatement("createModelAdapters()");
        writer.emitStatement("createTypeAdapters()");

        writer.endConstructor();
        writer.emitEmptyLine();
    }

    private void writeCreateMigrations(final JavaWriter writer) throws IOException {
        writer.beginMethod(void.class.getSimpleName(), "createMigrations", PRIVATE);

        for (MigrationElement migration : mRegistry.getMigrationElements().values()) {
            writer.emitStatement("mMigrations.put(%d, new %s())", migration.getVersion(), migration.getSimpleName());
        }

        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void writeCreateModelAdapters(final JavaWriter writer) throws IOException {
        writer.beginMethod(void.class.getSimpleName(), "createModelAdapters", PRIVATE);

        for (ModelAdapterElement modelAdapter : mRegistry.getModelAdapterElements()) {
            writer.emitStatement(
                    "mModelAdapters.put(%s.class, new %s())",
                    modelAdapter.getModelSimpleName(),
                    modelAdapter.getSimpleName()
            );
        }

        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void writeCreateTypeAdapters(final JavaWriter writer) throws IOException {
        writer.beginMethod(void.class.getSimpleName(), "createTypeAdapters", PRIVATE);

        for (TypeAdapterElement typeAdapter : mRegistry.getTypeAdapterElements()) {
            writer.emitStatement(
                    "mTypeAdapters.put(%s.class, new %s())",
                    typeAdapter.getDeserializedQualifiedName(),
                    typeAdapter.getSimpleName()
            );
        }

        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void writeGetMigrations(final JavaWriter writer) throws IOException {
        writer.emitAnnotation(Override.class);

        writer.beginMethod(
                String.format(
                        "SparseArray<? extends %s>",
                        BaseMigration.class.getSimpleName()
                ),
                "getMigrations",
                PUBLIC
        );

        writer.emitStatement("return mMigrations");
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void writeGetModelAdapter(final JavaWriter writer) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod(
                "<T extends Model> ModelAdapter<T>",
                "getModelAdapter",
                PUBLIC,
                "final Class<? extends Model>", "cls"
        );
        writer.emitStatement("return (ModelAdapter<T>) mModelAdapters.get(cls)");
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void writeGetModelAdapters(final JavaWriter writer) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod("List<? extends ModelAdapter>", "getModelAdapters", PUBLIC);
        writer.emitStatement("return Collections.unmodifiableList(new ArrayList<>(mModelAdapters.values()))");
        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void writeGetTypeAdapter(final JavaWriter writer) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod("<D, S> TypeAdapter<D, S>", "getTypeAdapter", PUBLIC, "final Class<D>", "cls");
        writer.emitStatement("return (TypeAdapter<D, S>) mTypeAdapters.get(cls)");
        writer.endMethod();
    }
}
