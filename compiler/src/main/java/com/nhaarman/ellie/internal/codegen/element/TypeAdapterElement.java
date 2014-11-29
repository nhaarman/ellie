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

import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class TypeAdapterElement {

    private final TypeElement mTypeElement;
    private TypeElement mDeserializedTypeElement;
    private TypeElement mSerializedTypeElement;

    public TypeAdapterElement(final Types types, final Elements elements, final TypeElement typeElement) {
        mTypeElement = typeElement;

        DeclaredType typeAdapterInterface = null;
        final DeclaredType typeAdapterType = types.getDeclaredType(
                elements.getTypeElement("com.nhaarman.ellie.TypeAdapter"),
                types.getWildcardType(null, null),
                types.getWildcardType(null, null)
        );


        for (TypeMirror superType : types.directSupertypes(typeElement.asType())) {
            if (types.isAssignable(superType, typeAdapterType)) {
                typeAdapterInterface = (DeclaredType) superType;
                break;
            }
        }

        if (typeAdapterInterface != null) {
            final List<? extends TypeMirror> typeArguments = typeAdapterInterface.getTypeArguments();
            mDeserializedTypeElement = elements.getTypeElement(typeArguments.get(0).toString());
            mSerializedTypeElement = elements.getTypeElement(typeArguments.get(1).toString());
        }
    }

    public String getQualifiedName() {
        return mTypeElement.getQualifiedName().toString();
    }

    public String getSimpleName() {
        return mTypeElement.getSimpleName().toString();
    }

    public TypeElement getDeserializedType() {
        return mDeserializedTypeElement;
    }

    public String getDeserializedQualifiedName() {
        return mDeserializedTypeElement.getQualifiedName().toString();
    }

    public String getDeserializedSimpleName() {
        return mDeserializedTypeElement.getSimpleName().toString();
    }

    public TypeElement getSerializedType() {
        return mSerializedTypeElement;
    }

    public String getSerializedQualifiedName() {
        return mSerializedTypeElement.getQualifiedName().toString();
    }

    public String getSerializedSimpleName() {
        return mSerializedTypeElement.getSimpleName().toString();
    }
}
