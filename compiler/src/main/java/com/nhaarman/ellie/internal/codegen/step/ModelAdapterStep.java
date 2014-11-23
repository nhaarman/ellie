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

import com.nhaarman.ellie.annotation.Column;
import com.nhaarman.ellie.annotation.GetterFor;
import com.nhaarman.ellie.annotation.SetterFor;
import com.nhaarman.ellie.annotation.Table;
import com.nhaarman.ellie.internal.codegen.Registry;
import com.nhaarman.ellie.internal.codegen.element.ColumnElement;
import com.nhaarman.ellie.internal.codegen.element.ModelAdapterElement;
import com.nhaarman.ellie.internal.codegen.validator.ColumnValidator;
import com.nhaarman.ellie.internal.codegen.validator.GetterForValidator;
import com.nhaarman.ellie.internal.codegen.validator.ModelAdapterValidator;
import com.nhaarman.ellie.internal.codegen.validator.SetterForValidator;
import com.nhaarman.ellie.internal.codegen.writer.ModelAdapterWriter;
import com.nhaarman.ellie.internal.codegen.writer.SourceWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

public class ModelAdapterStep implements ProcessingStep {
	private Registry registry;
	private Elements elements;
	private Filer filer;
	private ModelAdapterValidator validator;
	private ColumnValidator columnValidator;
	private GetterForValidator mGetterForValidator;
	private SetterForValidator mSetterForValidator;
	private SourceWriter sourceWriter;

	public ModelAdapterStep(Registry registry) {
		this.registry = registry;
		this.elements = registry.getElements();
		this.filer = registry.getFiler();
		this.validator = new ModelAdapterValidator(registry);
		mGetterForValidator = new GetterForValidator(registry);
		mSetterForValidator = new SetterForValidator(registry);
		this.columnValidator = new ColumnValidator(registry);
		this.sourceWriter = new ModelAdapterWriter(registry);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Set<? extends Element> tableElements = roundEnv.getElementsAnnotatedWith(Table.class);
		for (Element tableElement : tableElements) {
			if (validator.validate(tableElement.getEnclosingElement(), tableElement)) {
				registry.addModelAdapterElement(new ModelAdapterElement((TypeElement) tableElement));

				addColumnElements((TypeElement) tableElement);
				addAccessors((TypeElement) tableElement);

				try {
					String name = sourceWriter.createSourceName(tableElement);
					JavaFileObject object = filer.createSourceFile(name, tableElement);
					Writer writer = object.openWriter();
					sourceWriter.writeSource(writer, tableElement);
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	private void addColumnElements(TypeElement element) {
		final List<? extends Element> members = elements.getAllMembers(element);

		for (Element member : members) {
			if (member.getAnnotation(Column.class) != null && columnValidator.validate(element, member)) {
				registry.addColumnElement(new ColumnElement(registry, element, (VariableElement) member));
			}
		}
	}

	private void addAccessors(TypeElement element) {
		final List<? extends Element> members = elements.getAllMembers(element);

		for (Element member : members) {
			if (member.getAnnotation(GetterFor.class) != null && mGetterForValidator.validate(element, member)) {
				addGetter(element, member);
			} else if (member.getAnnotation(SetterFor.class) != null && mSetterForValidator.validate(element, member)) {
				addSetter(element, member);
			}
		}
	}

	private void addGetter(TypeElement element, Element member) {
		Set<ColumnElement> columnElements = registry.getColumnElements(element);
		for (ColumnElement columnElement : columnElements) {
			if (columnElement.getColumnName().equals(member.getAnnotation(GetterFor.class).value())) {
				columnElement.setGetter((ExecutableElement) member);
			}
		}
	}

	private void addSetter(TypeElement element, Element member) {
		Set<ColumnElement> columnElements = registry.getColumnElements(element);
		for (ColumnElement columnElement : columnElements) {
			if (columnElement.getColumnName().equals(member.getAnnotation(SetterFor.class).value())) {
				columnElement.setSetter((ExecutableElement) member);
			}
		}
	}
}
