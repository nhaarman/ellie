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

package com.nhaarman.ellie.internal.codegen.step;

import com.nhaarman.ellie.annotation.RepositoryFor;
import com.nhaarman.ellie.internal.codegen.Registry;
import com.nhaarman.ellie.internal.codegen.element.ModelElement;
import com.nhaarman.ellie.internal.codegen.validator.ModelRepositoryValidator;
import com.nhaarman.ellie.internal.codegen.writer.ModelRepositoryWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.JavaFileObject;

public class ModelRepositoryStep implements ProcessingStep {

    private final Registry mRegistry;

    private final ModelRepositoryValidator mModelRepositoryValidator;

    private final ModelRepositoryWriter mSourceWriter;

    private final Filer mFiler;

    public ModelRepositoryStep(final Registry registry) {
        mRegistry = registry;
        mModelRepositoryValidator = new ModelRepositoryValidator(registry);
        mFiler = registry.getFiler();
        mSourceWriter = new ModelRepositoryWriter(registry);
    }

    @Override
    public void process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        Set<? extends Element> repositoryElements = roundEnv.getElementsAnnotatedWith(RepositoryFor.class);
        for (Element repositoryElement : repositoryElements) {
            if (mModelRepositoryValidator.validate(repositoryElement)) {
                mRegistry.getModelElement(getValueString(repositoryElement.getAnnotation(RepositoryFor.class))).setModelRepositoryElement((TypeElement) repositoryElement);
            }
        }

        List<ModelElement> modelElements = mRegistry.getModelElements();
        for (ModelElement modelElement : modelElements) {
            writeModelRepository(modelElement);
        }
    }

    private void writeModelRepository(final ModelElement modelElement) {
        try {
            String name = mSourceWriter.createSourceName(modelElement.getTypeElement());
            JavaFileObject object = mFiler.createSourceFile(name, modelElement.getTypeElement());
            Writer writer = object.openWriter();
            mSourceWriter.writeSource(writer, modelElement.getTypeElement());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getValueString(final RepositoryFor annotation) {
        try {
            annotation.value();
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror().toString();
        }
        return null;
    }
}
