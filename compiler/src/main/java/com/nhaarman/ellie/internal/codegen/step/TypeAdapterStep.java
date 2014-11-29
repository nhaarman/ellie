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

import com.google.common.collect.Sets;
import com.nhaarman.ellie.adapter.BooleanAdapter;
import com.nhaarman.ellie.adapter.CalendarAdapter;
import com.nhaarman.ellie.adapter.SqlDateAdapter;
import com.nhaarman.ellie.adapter.UtilDateAdapter;
import com.nhaarman.ellie.internal.TypeAdapter;
import com.nhaarman.ellie.internal.codegen.Registry;
import com.nhaarman.ellie.internal.codegen.element.TypeAdapterElement;
import com.nhaarman.ellie.internal.codegen.validator.TypeAdapterValidator;
import com.nhaarman.ellie.internal.codegen.validator.Validator;

import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class TypeAdapterStep implements ProcessingStep {

    private static final Class[] DEFAULT_TYPE_ADAPTERS = {
            BooleanAdapter.class,
            CalendarAdapter.class,
            SqlDateAdapter.class,
            UtilDateAdapter.class
    };

    private final Registry mRegistry;
    private final Validator mValidator;

    public TypeAdapterStep(final Registry registry) {
        mRegistry = registry;
        mValidator = new TypeAdapterValidator(registry);
    }

    @Override
    public void process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        final Set<Element> elements = Sets.newHashSet(roundEnv.getElementsAnnotatedWith(TypeAdapter.class));
        for (Class cls : DEFAULT_TYPE_ADAPTERS) {
            elements.add(mRegistry.getElements().getTypeElement(cls.getName()));
        }

        for (Element element : elements) {
            if (mValidator.validate(element.getEnclosingElement(), element)) {
                mRegistry.addTypeAdapterModel(
                        new TypeAdapterElement(
                                mRegistry.getTypes(),
                                mRegistry.getElements(),
                                (TypeElement) element
                        )
                );
            }
        }
    }
}
