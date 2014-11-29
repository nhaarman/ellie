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

import com.nhaarman.ellie.BaseMigration;
import com.nhaarman.ellie.annotation.Migration;
import com.nhaarman.ellie.internal.codegen.Registry;
import com.nhaarman.ellie.internal.codegen.element.MigrationElement;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.tools.Diagnostic.Kind.ERROR;

public class MigrationValidator {

    private final Registry mRegistry;
    private final Messager mMessager;

    public MigrationValidator(final Registry registry) {
        mRegistry = registry;
        mMessager = registry.getMessager();
    }

    public boolean validates(final Element element) {
        return elementKindValidates(element) && versionValidates(element);
    }

    private boolean elementKindValidates(final Element element) {
        if (element.getKind() != CLASS) {
            mMessager.printMessage(ERROR, "annotation type not applicable to this kind of declaration", element);
            return false;
        }

        if (!((TypeElement) element).getSuperclass().toString().equals(BaseMigration.class.getName())) {
            mMessager.printMessage(ERROR, "Classes annotated with @Migration should extend BaseMigration.");
            return false;
        }

        return true;
    }

    private boolean versionValidates(final Element element) {
        Migration migrationAnnotation = element.getAnnotation(Migration.class);

        MigrationElement existingMigrationElement = mRegistry.getMigrationElement(migrationAnnotation.version());
        if (existingMigrationElement != null) {
            mMessager.printMessage(
                    ERROR,
                    String.format(
                            "Found two Migrations with the same version: \n\t- %s;\n\t- %s\nEach of your migrations needs to have a unique version.",
                            element.getSimpleName(),
                            existingMigrationElement.getSimpleName()
                    ),
                    element
            );
        }

        return true;
    }
}
