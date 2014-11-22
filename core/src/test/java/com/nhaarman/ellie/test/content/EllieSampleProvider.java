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

package com.nhaarman.ellie.test.content;

        import com.nhaarman.ellie.Ellie;
        import com.nhaarman.ellie.EllieProvider;


public class EllieSampleProvider extends EllieProvider {

    @Override
    protected String getDatabaseName() {
        return "OllieSample.db";
    }

    @Override
    protected int getDatabaseVersion() {
        return 1;
    }

    @Override
    protected Ellie.LogLevel getLogLevel() {
        return Ellie.LogLevel.FULL;
    }
}