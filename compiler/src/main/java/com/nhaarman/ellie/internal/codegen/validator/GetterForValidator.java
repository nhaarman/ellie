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

package com.nhaarman.ellie.internal.codegen.validator;

import com.nhaarman.ellie.annotation.GetterFor;
import com.nhaarman.ellie.annotation.Table;
import com.nhaarman.ellie.internal.codegen.Registry;
import com.nhaarman.ellie.internal.codegen.element.ColumnElement;

import org.jetbrains.annotations.NonNls;

import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.tools.Diagnostic.Kind.ERROR;

public class GetterForValidator implements Validator {

    private final Registry mRegistry;
    @NonNls private final Messager mMessager;

    public GetterForValidator(final Registry registry) {
        mRegistry = registry;
        mMessager = registry.getMessager();
    }

    @Override
    public boolean validate(final Element enclosingElement, final Element element) {
        Table table = enclosingElement.getAnnotation(Table.class);
        if (!enclosingElement.getKind().equals(CLASS) || table == null) {
            mMessager.printMessage(ERROR, "@GetterFor methods can only be enclosed by model classes.", element);
            return false;
        }

        GetterFor getter = element.getAnnotation(GetterFor.class);
        Set<ColumnElement> existingColumns = mRegistry.getColumnElements((TypeElement) enclosingElement);

        boolean hasField = false;
        for (ColumnElement column : existingColumns) {
            if (column.getColumnName().equals(getter.value())) {
                hasField = true;
            }
        }

        if (!hasField) {
            mMessager.printMessage(ERROR, "@GetterFor \"" + getter.value() + "\" found without a @Column annotated field for \"" + getter.value() + "\".", element);
        }

        return true;
    }
}
