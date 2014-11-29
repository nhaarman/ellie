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

package com.nhaarman.ellie.internal.codegen.element;

import javax.lang.model.element.TypeElement;

import static com.nhaarman.ellie.internal.Package.PACKAGE_NAME;

public class ModelElement {

    private final TypeElement mTypeElement;
    private TypeElement mModelRepositoryElement;

    public ModelElement(final TypeElement typeElement) {
        mTypeElement = typeElement;
    }

    public TypeElement getTypeElement() {
        return mTypeElement;
    }

    public String getModelAdapterQualifiedName() {
        return PACKAGE_NAME + "." + getModelSimpleName() + "$$ModelAdapter";
    }

    public String getModelQualifiedName() {
        return mTypeElement.getQualifiedName().toString();
    }

    public String getModelSimpleName() {
        return mTypeElement.getSimpleName().toString();
    }

    public String getSimpleName() {
        return getModelSimpleName() + "$$ModelAdapter";
    }

    public String getModelRepositoryQualifiedName() {
        return PACKAGE_NAME + "." + getModelSimpleName() + "$$Repository";
    }

    public void setModelRepositoryElement(final TypeElement modelRepositoryElement) {
        mModelRepositoryElement = modelRepositoryElement;
    }

    public TypeElement getModelRepositoryElement() {
        return mModelRepositoryElement;
    }
}
