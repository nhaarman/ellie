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

package com.nhaarman.ellie.internal.codegen.step;

import com.nhaarman.ellie.internal.codegen.Registry;
import com.nhaarman.ellie.internal.codegen.writer.RepositoryHolderWriter;
import com.nhaarman.ellie.internal.codegen.writer.SourceWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

public class RepositoryHolderStep implements ProcessingStep {

    private final Registry mRegistry;

    private final SourceWriter mSourceWriter;

    public RepositoryHolderStep(final Registry registry) {
        mRegistry = registry;
        mSourceWriter = new RepositoryHolderWriter(registry);
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            try {
                String name = mSourceWriter.createSourceName(null);
                JavaFileObject object = mRegistry.getFiler().createSourceFile(name);
                Writer writer = object.openWriter();
                mSourceWriter.writeSource(writer, null);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
