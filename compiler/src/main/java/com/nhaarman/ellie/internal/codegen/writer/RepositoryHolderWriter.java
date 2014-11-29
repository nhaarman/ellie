/*
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

import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.Sets;
import com.nhaarman.ellie.Ellie;
import com.nhaarman.ellie.ModelRepository;
import com.nhaarman.ellie.internal.RepositoryHolder;
import com.nhaarman.ellie.internal.codegen.Registry;
import com.nhaarman.ellie.internal.codegen.element.ModelRepositoryElement;
import com.squareup.javawriter.JavaWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

public class RepositoryHolderWriter implements SourceWriter<TypeElement> {

    private static final EnumSet<Modifier> PRIVATE_FINAL = EnumSet.of(PRIVATE, FINAL);

    private static final Set<Modifier> PUBLIC_FINAL = EnumSet.of(PUBLIC, FINAL);

    private final Registry mRegistry;

    public RepositoryHolderWriter(final Registry registry) {
        mRegistry = registry;
    }

    @Override
    public String createSourceName(final TypeElement element) {
        return RepositoryHolder.IMPL_CLASS_FQCN;
    }

    @Override
    public void writeSource(final Writer writer, final TypeElement element) throws IOException {

        JavaWriter javaWriter = new JavaWriter(writer);
        javaWriter.setCompressingTypes(true);
        javaWriter.setIndent("    ");

        javaWriter.emitSingleLineComment("Generated by Ellie. Do not modify!");
        javaWriter.emitPackage("com.nhaarman.ellie");

        writeImports(javaWriter);

        javaWriter.beginType(RepositoryHolder.IMPL_CLASS_NAME, "class", PUBLIC_FINAL, null, RepositoryHolder.class.getSimpleName());

        writeFields(javaWriter);
        writeConstructor(javaWriter);
        writeCreateModelRepositories(javaWriter);
        writeGetModelRepository(javaWriter);
        writeGetModelRepositories(javaWriter);

        javaWriter.endType();
    }

    private void writeImports(final JavaWriter writer) throws IOException {
        Set<String> imports = Sets.newHashSet(
                SQLiteDatabase.class.getName(),
                ModelRepository.class.getName(),
                Collection.class.getName(),
                Collections.class.getName(),
                RepositoryHolder.class.getName(),
                Map.class.getName(),
                List.class.getName(),
                HashMap.class.getName(),
                ArrayList.class.getName()
        );


        writer.emitImports(imports);
        writer.emitEmptyLine();
    }

    private void writeFields(final JavaWriter writer) throws IOException {
        writer.emitField(
                "Map<Class<? extends Model>, ModelRepository<?>>",
                "mModelRepositories",
                PRIVATE_FINAL,
                "new HashMap<>()"
        );
        writer.emitEmptyLine();
    }

    private void writeConstructor(final JavaWriter writer) throws IOException {
        writer.beginConstructor(
                EnumSet.of(PUBLIC),
                "final " + Ellie.class.getSimpleName(), "ellie",
                "final " + SQLiteDatabase.class.getSimpleName(), "database",
                "final int", "cacheSize"
        );

        writer.emitStatement("createModelRepositories(ellie, database, cacheSize)");

        writer.endConstructor();
        writer.emitEmptyLine();
    }

    private void writeCreateModelRepositories(final JavaWriter writer) throws IOException {
        writer.beginMethod(
                void.class.getSimpleName(),
                "createModelRepositories",
                PRIVATE_FINAL,
                "final " + Ellie.class.getSimpleName(), "ellie",
                "final " + SQLiteDatabase.class.getSimpleName(), "database",
                "final int", "cacheSize"
        );

        for (ModelRepositoryElement modelRepositoryElement : mRegistry.getModelRepositories()) {
            writer.emitStatement(
                    "mModelRepositories.put(%s.class, new %s(ellie, database, cacheSize))",
                    modelRepositoryElement.getModelQualifiedName(),
                    modelRepositoryElement.getQualifiedName()
            );
        }

        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void writeGetModelRepository(final JavaWriter writer) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod(
                "<T extends Model> ModelRepository<T>",
                "getModelRepository",
                PUBLIC_FINAL,
                "final Class<? extends Model>", "cls"
        );

        writer.emitStatement("return (ModelRepository<T>) mModelRepositories.get(cls)");

        writer.endMethod();
        writer.emitEmptyLine();
    }

    private void writeGetModelRepositories(final JavaWriter writer) throws IOException {
        writer.emitAnnotation(Override.class);
        writer.beginMethod("List<? extends ModelRepository>", "getModelRepositories", PUBLIC_FINAL);

        writer.emitStatement("return Collections.unmodifiableList(new ArrayList<>(mModelRepositories.values()))");

        writer.endMethod();
        writer.emitEmptyLine();
    }
}
