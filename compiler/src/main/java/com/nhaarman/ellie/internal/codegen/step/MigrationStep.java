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

package com.nhaarman.ellie.internal.codegen.step;

import com.nhaarman.ellie.annotation.Migration;
import com.nhaarman.ellie.internal.codegen.Registry;
import com.nhaarman.ellie.internal.codegen.element.MigrationElement;
import com.nhaarman.ellie.internal.codegen.validator.MigrationValidator;

import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class MigrationStep implements ProcessingStep {

    private final Registry mRegistry;

    private final MigrationValidator mMigrationValidator;


    public MigrationStep(final Registry registry) {
        mRegistry = registry;
        mMigrationValidator = new MigrationValidator(registry);
    }

    @Override
    public void process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Migration.class);

        for (Element element : elements) {
            processElement(element);
        }
    }

    private void processElement(final Element element) {
        if (mMigrationValidator.validates(element)) {
            MigrationElement migrationElement = new MigrationElement((TypeElement) element);
            mRegistry.addMigrationElement(migrationElement);
        }
    }
}
