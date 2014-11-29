/*
 * Copyright (C) 2014 Niek Haarman
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

package com.nhaarman.ellie.internal.sources;

import com.google.testing.compile.JavaFileObjects;

import javax.tools.JavaFileObject;

public class Tag {

    public static JavaFileObject fullSource() {

        return JavaFileObjects.forSourceLines(
                "com.nhaarman.ellie.Tag",
                "package com.nhaarman.ellie;",
                "import java.util.Date;",
                "import com.nhaarman.ellie.Model;",
                "import com.nhaarman.ellie.annotation.Column;",
                "import com.nhaarman.ellie.annotation.GetterFor;",
                "import com.nhaarman.ellie.annotation.SetterFor;",
                "import com.nhaarman.ellie.annotation.NotNull;",
                "import com.nhaarman.ellie.annotation.Table;",
                "@Table(\"tags\")",
                "public class Tag extends Model {",
                "	public static final String TITLE = \"title\";",
                "	public static final String BODY = \"body\";",
                "	public static final String DATE = \"date\";",
                "	@Column(TITLE) public String title;",
                "	@Column(BODY) @NotNull public String body;",
                "	@Column(DATE) public Date date;",
                "   @GetterFor(TITLE) public String getTitle() { return title; }",
                "   @SetterFor(TITLE) public void setTitle(String title) { this.title = title; }",
                "}"
        );
    }

}
