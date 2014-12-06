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

import com.nhaarman.ellie.annotation.Column;
import com.nhaarman.ellie.annotation.Table;
import com.nhaarman.ellie.internal.codegen.Errors;
import com.nhaarman.ellie.internal.codegen.Registry;
import com.nhaarman.ellie.internal.codegen.element.ColumnElement;

import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.tools.Diagnostic.Kind.ERROR;

public class ColumnValidator implements Validator {

    private final Registry mRegistry;
    private final Messager mMessager;

    public ColumnValidator(final Registry registry) {
        mRegistry = registry;
        mMessager = registry.getMessager();
    }

    @Override
    public boolean validate(final Element enclosingElement, final Element element) {
        Table table = enclosingElement.getAnnotation(Table.class);

        boolean isAbstractClass = enclosingElement.getKind() == CLASS && enclosingElement.getModifiers().contains(Modifier.ABSTRACT);

        if (!isAbstractClass && (enclosingElement.getKind() != CLASS || table == null)) {
            mMessager.printMessage(ERROR, "@Column fields can only be enclosed by model classes.", element);
            return false;
        }

        Column column = element.getAnnotation(Column.class);
        Set<ColumnElement> existingColumns = mRegistry.getColumnElements((TypeElement) enclosingElement);
        for (ColumnElement existingColumn : existingColumns) {
            if (existingColumn.getColumnName().equals(column.value())) {
                mMessager.printMessage(ERROR, Errors.COLUMN_DUPLICATE_ERROR + column.value(), element);
                return false;
            }
        }

        return true;
    }
}
