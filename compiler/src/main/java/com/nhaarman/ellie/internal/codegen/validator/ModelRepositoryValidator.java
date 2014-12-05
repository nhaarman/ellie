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

package com.nhaarman.ellie.internal.codegen.validator;

import com.nhaarman.ellie.ModelRepository;
import com.nhaarman.ellie.annotation.RepositoryFor;
import com.nhaarman.ellie.internal.codegen.Registry;

import java.util.List;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.tools.Diagnostic.Kind.ERROR;

public class ModelRepositoryValidator {

    private final Messager mMessager;

    public ModelRepositoryValidator(final Registry registry) {
        mMessager = registry.getMessager();
    }

    public boolean validate(final Element element) {
        return kindValidates(element) && abstractValidates(element) && implementsValidates(element);
    }

    private boolean kindValidates(final Element element) {
        if (element.getKind() != CLASS) {
            mMessager.printMessage(ERROR, "annotation type not applicable to this kind of declaration", element);
            return false;
        }
        return true;
    }

    private boolean abstractValidates(final Element element) {
        if (!element.getModifiers().contains(Modifier.ABSTRACT)) {
            mMessager.printMessage(ERROR, "Classes annotated with @RepositoryFor should be abstract", element);
            return false;
        }
        return true;
    }

    private boolean implementsValidates(final Element element) {
        RepositoryFor repositoryForAnnotation = element.getAnnotation(RepositoryFor.class);
        TypeMirror value = getValue(repositoryForAnnotation);

        List<? extends TypeMirror> interfaces = ((TypeElement) element).getInterfaces();
        boolean implementsModelRepository = false;
        for (TypeMirror anInterface : interfaces) {
            if (anInterface.toString().equals(String.format("%s<%s>", ModelRepository.class.getName(), value.toString()))) {
                implementsModelRepository = true;
            }
        }

        if (!implementsModelRepository) {
            mMessager.printMessage(ERROR, "Classes annotated with @RepositoryFor should implement ModelRepository", element);
            return false;
        }
        return true;
    }

    private static TypeMirror getValue(final RepositoryFor annotation) {
        try {
            annotation.value();
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }
        return null;
    }

}
