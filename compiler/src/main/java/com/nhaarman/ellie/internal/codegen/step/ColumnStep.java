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
import com.nhaarman.ellie.internal.codegen.validator.ColumnValidator;
import com.nhaarman.ellie.internal.codegen.validator.Validator;

import com.nhaarman.ellie.annotation.Column;
import com.nhaarman.ellie.internal.codegen.Registry;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public class ColumnStep implements ProcessingStep {
	private Validator validator;

	public ColumnStep(Registry registry) {
		this.validator = new ColumnValidator(registry);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		final Set<Element> elements = Sets.newHashSet(roundEnv.getElementsAnnotatedWith(Column.class));
		for (Element element : elements) {
			validator.validate(element.getEnclosingElement(), element);
		}
		return false;
	}
}
