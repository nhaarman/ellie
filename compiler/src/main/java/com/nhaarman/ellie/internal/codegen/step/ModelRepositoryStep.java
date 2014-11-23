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

import com.nhaarman.ellie.annotation.Table;
import com.nhaarman.ellie.internal.codegen.Registry;
import com.nhaarman.ellie.internal.codegen.element.ModelRepositoryElement;
import com.nhaarman.ellie.internal.codegen.validator.ColumnValidator;
import com.nhaarman.ellie.internal.codegen.validator.ModelRepositoryValidator;
import com.nhaarman.ellie.internal.codegen.writer.ModelRepositoryWriter;
import com.nhaarman.ellie.internal.codegen.writer.SourceWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

public class ModelRepositoryStep implements ProcessingStep {

    private final Registry mRegistry;

    private final Elements mElements;

    private final Filer mFiler;

    private final ModelRepositoryValidator mModelRepositoryValidator;

    private final ColumnValidator mColumnvalidator;

    private final SourceWriter mSourceWriter;

    public ModelRepositoryStep(final Registry registry) {
        mRegistry = registry;
        mElements = registry.getElements();
        mFiler = registry.getFiler();
        mModelRepositoryValidator = new ModelRepositoryValidator(registry);
        mColumnvalidator = new ColumnValidator(registry);
        mSourceWriter = new ModelRepositoryWriter(registry);
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        Set<? extends Element> tableElements = roundEnv.getElementsAnnotatedWith(Table.class);
        for (Element tableElement : tableElements) {
            if (mModelRepositoryValidator.validate(tableElement.getEnclosingElement(), tableElement)) {
                mRegistry.addModelRepositoryElement(new ModelRepositoryElement((TypeElement) tableElement));

                try {
                    String name = mSourceWriter.createSourceName(tableElement);
                    JavaFileObject object = mFiler.createSourceFile(name, tableElement);
                    Writer writer = object.openWriter();
                    mSourceWriter.writeSource(writer, tableElement);
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
