/*
 * Copyright (C) 2014 Michael Pardo
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
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.nhaarman.ellie.internal.codegen.element.ColumnElement;
import com.nhaarman.ellie.internal.codegen.element.MigrationElement;
import com.nhaarman.ellie.internal.codegen.element.ModelAdapterElement;
import com.nhaarman.ellie.internal.codegen.element.ModelRepositoryElement;
import com.nhaarman.ellie.internal.codegen.element.TypeAdapterElement;

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

    private final Set<MigrationElement> mMigrationElements = Sets.newHashSet();

    private final Map<String, TypeAdapterElement> mTypeAdapters = Maps.newHashMap();

    private final SetMultimap<String, ColumnElement> mColumns = LinkedHashMultimap.create();

    private final Set<ModelAdapterElement> mModelAdapters = Sets.newHashSet();

    private final Set<ModelRepositoryElement> mModelRepositories = Sets.newHashSet();

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

    public Set<MigrationElement> getMigrationElements() {
        return mMigrationElements;
    }

    public void addMigrationElement(final MigrationElement element) {
        mMigrationElements.add(element);
    }

    // Type adapters

    public TypeAdapterElement getTypeAdapterElement(final TypeElement deserializedType) {
        return mTypeAdapters.get(deserializedType.getQualifiedName().toString());
    }

    public Set<TypeAdapterElement> getTypeAdapterElements() {
        return Sets.newHashSet(mTypeAdapters.values());
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

    public Set<ModelAdapterElement> getModelAdapterElements() {
        return mModelAdapters;
    }

    public void addModelAdapterElement(final ModelAdapterElement element) {
        mModelAdapters.add(element);
    }

    public Set<ModelRepositoryElement> getModelRepositories() {
        return mModelRepositories;
    }

    public void addModelRepositoryElement(final ModelRepositoryElement element) {
        mModelRepositories.add(element);
    }
}
