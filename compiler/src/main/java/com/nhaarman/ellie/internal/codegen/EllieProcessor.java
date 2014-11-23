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

import com.google.common.collect.ImmutableSet;
import com.nhaarman.ellie.annotation.Column;
import com.nhaarman.ellie.annotation.GetterFor;
import com.nhaarman.ellie.annotation.SetterFor;
import com.nhaarman.ellie.annotation.Table;
import com.nhaarman.ellie.internal.Migration;
import com.nhaarman.ellie.internal.TypeAdapter;
import com.nhaarman.ellie.internal.codegen.step.AdapterHolderStep;
import com.nhaarman.ellie.internal.codegen.step.ColumnStep;
import com.nhaarman.ellie.internal.codegen.step.MigrationStep;
import com.nhaarman.ellie.internal.codegen.step.ModelAdapterStep;
import com.nhaarman.ellie.internal.codegen.step.ModelRepositoryStep;
import com.nhaarman.ellie.internal.codegen.step.ProcessingStep;
import com.nhaarman.ellie.internal.codegen.step.RepositoryHolderStep;
import com.nhaarman.ellie.internal.codegen.step.TypeAdapterStep;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

public class EllieProcessor extends AbstractProcessor {

    private ImmutableSet<? extends ProcessingStep> mProcessingSteps;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(
                Migration.class.getName(),
                TypeAdapter.class.getName(),
                Table.class.getName(),
                Column.class.getName(),
                GetterFor.class.getName(),
                SetterFor.class.getName()
        );
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        Registry registry = new Registry(
                processingEnv.getMessager(),
                processingEnv.getTypeUtils(),
                processingEnv.getElementUtils(),
                processingEnv.getFiler()
        );

        mProcessingSteps = ImmutableSet.of(
                new MigrationStep(registry),
                new TypeAdapterStep(registry),
                new ColumnStep(registry),
                new ModelAdapterStep(registry),
                new AdapterHolderStep(registry),
                new ModelRepositoryStep(registry),
                new RepositoryHolderStep(registry)
        );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (ProcessingStep processingStep : mProcessingSteps) {
            processingStep.process(annotations, roundEnv);
        }
        return false;
    }
}
