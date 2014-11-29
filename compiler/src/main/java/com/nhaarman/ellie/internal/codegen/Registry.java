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

package com.nhaarman.ellie.internal.codegen;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.nhaarman.ellie.internal.codegen.element.ColumnElement;
import com.nhaarman.ellie.internal.codegen.element.MigrationElement;
import com.nhaarman.ellie.internal.codegen.element.ModelElement;
import com.nhaarman.ellie.internal.codegen.element.TypeAdapterElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class Registry {

    private final Messager mMessager;

    private final Types mTypes;

    private final Elements mElements;

    private final Filer mFiler;

    private final Map<String, TypeAdapterElement> mTypeAdapters = Maps.newHashMap();

    private final SetMultimap<String, ColumnElement> mColumns = LinkedHashMultimap.create();

    private final Set<ModelElement> mModelElements = Sets.newHashSet();

    private final Map<Integer, MigrationElement> mMigrationElements = Maps.newHashMap();

    public Registry(final Messager messager, final Types types, final Elements elements, final Filer filer) {
        mMessager = messager;
        mTypes = types;
        mElements = elements;
        mFiler = filer;
    }

    public Messager getMessager() {
        return mMessager;
    }

    public Types getTypes() {
        return mTypes;
    }

    public Elements getElements() {
        return mElements;
    }

    public Filer getFiler() {
        return mFiler;
    }

    // Migrations

    public Map<Integer, MigrationElement> getMigrationElements() {
        return mMigrationElements;
    }

    public MigrationElement getMigrationElement(final int version) {
        return mMigrationElements.get(version);
    }

    public void addMigrationElement(final MigrationElement element) {
        mMigrationElements.put(element.getVersion(), element);
    }

    // Type adapters

    public TypeAdapterElement getTypeAdapterElement(final TypeElement deserializedType) {
        return mTypeAdapters.get(deserializedType.getQualifiedName().toString());
    }

    public List<TypeAdapterElement> getTypeAdapterElements() {
        Collection<TypeAdapterElement> values = mTypeAdapters.values();
        List<TypeAdapterElement> result = Lists.newArrayList(values);
        Collections.sort(
                result, new Comparator<TypeAdapterElement>() {
                    @Override
                    public int compare(final TypeAdapterElement o1, final TypeAdapterElement o2) {
                        return o1.getSimpleName().compareTo(o2.getSimpleName());
                    }
                }
        );
        return result;
    }

    public void addTypeAdapterModel(final TypeAdapterElement element) {
        mTypeAdapters.put(element.getDeserializedQualifiedName(), element);
    }

    // Columns

    public Set<ColumnElement> getColumnElements(final TypeElement enclosingType) {
        return Sets.newLinkedHashSet(mColumns.get(enclosingType.getQualifiedName().toString()));
    }

    public void addColumnElement(final ColumnElement element) {
        mColumns.put(element.getEnclosingQualifiedName(), element);
    }

    // Model adapters

    public List<ModelElement> getModelElements() {
        List<ModelElement> result = new ArrayList<>(mModelElements);

        Collections.sort(
                result, new Comparator<ModelElement>() {
                    @Override
                    public int compare(final ModelElement o1, final ModelElement o2) {
                        return o1.getModelAdapterQualifiedName().compareTo(o2.getModelAdapterQualifiedName());
                    }
                }
        );

        return result;
    }

    public ModelElement getModelElement(String fullyQualifiedModelName) {
        for (ModelElement modelElement : mModelElements) {
            if (modelElement.getModelQualifiedName().equals(fullyQualifiedModelName)) {
                return modelElement;
            }
        }
        return null;
    }

    public void addModelElement(final ModelElement element) {
        mModelElements.add(element);
    }
}
