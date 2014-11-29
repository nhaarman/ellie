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

package com.nhaarman.ellie.internal.codegen.element;


import com.nhaarman.ellie.annotation.Migration;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public class MigrationElement {

    private final TypeElement mElement;

    private ExecutableElement mBeforeUpElement;
    private ExecutableElement mAfterUpElement;
    private ExecutableElement mUpElement;
    
    private ExecutableElement mBeforeDownElement;
    private ExecutableElement mDownElement;
    private ExecutableElement mAfterDownElement;

    public MigrationElement(final TypeElement element) {
        mElement = element;
    }

    public String getQualifiedName() {
        return mElement.getQualifiedName().toString();
    }

    public String getSimpleName() {
        return mElement.getSimpleName().toString();
    }

    public int getVersion() {
        return mElement.getAnnotation(Migration.class).version();
    }

    public ExecutableElement getBeforeUpElement() {
        return mBeforeUpElement;
    }

    public void setBeforeUpElement(final ExecutableElement beforeUpElement) {
        mBeforeUpElement = beforeUpElement;
    }

    public ExecutableElement getAfterUpElement() {
        return mAfterUpElement;
    }

    public void setAfterUpElement(final ExecutableElement afterUpElement) {
        mAfterUpElement = afterUpElement;
    }

    public ExecutableElement getUpElement() {
        return mUpElement;
    }

    public void setUpElement(final ExecutableElement upElement) {
        mUpElement = upElement;
    }

    public ExecutableElement getAfterDownElement() {
        return mAfterDownElement;
    }

    public void setAfterDownElement(final ExecutableElement afterDownElement) {
        mAfterDownElement = afterDownElement;
    }

    public ExecutableElement getBeforeDownElement() {
        return mBeforeDownElement;
    }

    public void setBeforeDownElement(final ExecutableElement beforeDownElement) {
        mBeforeDownElement = beforeDownElement;
    }

    public ExecutableElement getDownElement() {
        return mDownElement;
    }

    public void setDownElement(final ExecutableElement downElement) {
        mDownElement = downElement;
    }
}
