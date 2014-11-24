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

package com.nhaarman.ellie.internal.codegen;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.nhaarman.ellie.internal.ProcessorTestUtilities.ellieProcessors;
import static org.truth0.Truth.ASSERT;

@SuppressWarnings({"HardCodedStringLiteral", "JUnitTestMethodWithNoAssertions"})
public class ModelAdapterTest {

    @Test
    public void modelAdapter() {
        JavaFileObject source = JavaFileObjects.forSourceString(
                "com.nhaarman.ellie.test.Note",
                Joiner.on('\n').join(
                        "package com.nhaarman.ellie.test;",
                        "import java.util.Date;",
                        "import com.nhaarman.ellie.Model;",
                        "import com.nhaarman.ellie.annotation.Column;",
                        "import com.nhaarman.ellie.annotation.GetterFor;",
                        "import com.nhaarman.ellie.annotation.SetterFor;",
                        "import com.nhaarman.ellie.annotation.NotNull;",
                        "import com.nhaarman.ellie.annotation.Table;",
                        "@Table(\"notes\")",
                        "public class Note extends Model {",
                        "	public static final String TITLE = \"title\";",
                        "	public static final String BODY = \"body\";",
                        "	public static final String DATE = \"date\";",
                        "	@Column(TITLE) public String title;",
                        "	@Column(BODY) @NotNull public String body;",
                        "	@Column(DATE) public Date date;",
                        "   @GetterFor(TITLE) public String getTitle() { return title; }",
                        "   @SetterFor(TITLE) public void setTitle(String title) { this.title = title; }",
                        "}"
                )
        );

        JavaFileObject expectedSource = JavaFileObjects.forSourceString(
                "com.nhaarman.ellie/Note$$ModelAdapter",
                Joiner.on('\n').join(
                        "package com.nhaarman.ellie;",
                        "import com.nhaarman.ellie.internal.ModelAdapter;",
                        "import com.nhaarman.ellie.test.Note;",
                        "public final class Note$$ModelAdapter implements ModelAdapter<Note> {",
                        "   @Override",
                        "	public Class<Note> getModelType() {",
                        "		return Note.class;",
                        "	}",
                        "   @Override",
                        "	public String getTableName() {",
                        "		return \"notes\";",
                        "	}",
                        "   @Override",
                        "	public String getSchema() {",
                        "		return \"CREATE TABLE IF NOT EXISTS notes (\" +",
                        "			\"_id INTEGER PRIMARY KEY AUTOINCREMENT, \" +",
                        "			\"title TEXT, \" +",
                        "			\"body TEXT NOT NULL, \" +",
                        "			\"date INTEGER)\";",
                        "	}",
                        "}"
                )
        );

        ASSERT.about(javaSource()).that(source)
              .processedWith(ellieProcessors())
              .compilesWithoutError()
              .and()
              .generatesSources(expectedSource);
    }


    @Test
    public void tablesAreClasses() {
        JavaFileObject source = JavaFileObjects.forSourceString(
                "com.nhaarman.ellie.test.Note",
                Joiner.on('\n').join(
                        "package com.nhaarman.ellie.test;",
                        "import java.util.Date;",
                        "import com.nhaarman.ellie.Model;",
                        "import com.nhaarman.ellie.annotation.Column;",
                        "import com.nhaarman.ellie.annotation.NotNull;",
                        "import com.nhaarman.ellie.annotation.Table;",
                        "@Table(\"notes\")",
                        "public class Note extends Model {",
                        "	public static final String TITLE = \"title\";",
                        "	public static final String BODY = \"body\";",
                        "	public static final String DATE = \"date\";",
                        "	@Table(TITLE) public String title;",
                        "	@Table(BODY) @NotNull public String body;",
                        "	@Table(DATE) public Date date;",
                        "}"
                )
        );

        ASSERT.about(javaSource()).that(source)
              .processedWith(ellieProcessors())
              .failsToCompile();
    }

    @Test
    public void columnsAreFields() {
        JavaFileObject source = JavaFileObjects.forSourceString(
                "com.nhaarman.ellie.test.Note",
                Joiner.on('\n').join(
                        "package com.nhaarman.ellie.test;",
                        "import java.util.Date;",
                        "import com.nhaarman.ellie.Model;",
                        "import com.nhaarman.ellie.annotation.Column;",
                        "import com.nhaarman.ellie.annotation.NotNull;",
                        "import com.nhaarman.ellie.annotation.Table;",
                        "@Column(\"notes\")",
                        "public class Note extends Model {",
                        "	public static final String TITLE = \"title\";",
                        "	public static final String BODY = \"body\";",
                        "	public static final String DATE = \"date\";",
                        "	@Column(TITLE) public String title;",
                        "	@Column(BODY) @NotNull public String body;",
                        "	@Column(DATE) public Date date;",
                        "}"
                )
        );

        ASSERT.about(javaSource()).that(source)
              .processedWith(ellieProcessors())
              .failsToCompile()
              .withErrorContaining("annotation type not applicable to this kind of declaration");
    }

    @Test
    public void columnsAreUnique() {
        JavaFileObject source = JavaFileObjects.forSourceString(
                "com.nhaarman.ellie.test.Note",
                Joiner.on('\n').join(
                        "package com.nhaarman.ellie.test;",
                        "import java.util.Date;",
                        "import com.nhaarman.ellie.Model;",
                        "import com.nhaarman.ellie.annotation.Column;",
                        "import com.nhaarman.ellie.annotation.NotNull;",
                        "import com.nhaarman.ellie.annotation.Table;",
                        "@Table(\"notes\")",
                        "public class Note extends Model {",
                        "	public static final String TITLE = \"title\";",
                        "	public static final String DATE = \"date\";",
                        "	@Column(TITLE) public String title;",
                        "	@Column(TITLE) @NotNull public String body;",
                        "	@Column(DATE) public Date date;",
                        "}"
                )
        );

        ASSERT.about(javaSource()).that(source)
              .processedWith(ellieProcessors())
              .failsToCompile()
              .withErrorContaining(Errors.COLUMN_DUPLICATE_ERROR);
    }

    @Test
    public void getterForHasColumn() {
        JavaFileObject source = JavaFileObjects.forSourceString(
                "com.nhaarman.ellie.test.Note",
                Joiner.on('\n').join(
                        "package com.nhaarman.ellie.test;",
                        "import java.util.Date;",
                        "import com.nhaarman.ellie.Model;",
                        "import com.nhaarman.ellie.annotation.Column;",
                        "import com.nhaarman.ellie.annotation.GetterFor;",
                        "import com.nhaarman.ellie.annotation.NotNull;",
                        "import com.nhaarman.ellie.annotation.Table;",
                        "@Table(\"notes\")",
                        "public class Note extends Model {",
                        "	public static final String TITLE = \"title\";",
                        "	public static final String BODY = \"body\";",
                        "	public static final String DATE = \"date\";",
                        "	public String title;",
                        "	@Column(BODY) @NotNull public String body;",
                        "	@Column(DATE) public Date date;",
                        "   @GetterFor(TITLE) public String getTitle() { return title; }",
                        "}"
                )
        );

        ASSERT.about(javaSource()).that(source)
              .processedWith(ellieProcessors())
              .failsToCompile()
              .withErrorContaining("@GetterFor \"title\" found without a @Column annotated field for \"title\"");
    }

    @Test
    public void setterForHasColumn() {
        JavaFileObject source = JavaFileObjects.forSourceString(
                "com.nhaarman.ellie.test.Note",
                Joiner.on('\n').join(
                        "package com.nhaarman.ellie.test;",
                        "import java.util.Date;",
                        "import com.nhaarman.ellie.Model;",
                        "import com.nhaarman.ellie.annotation.Column;",
                        "import com.nhaarman.ellie.annotation.SetterFor;",
                        "import com.nhaarman.ellie.annotation.NotNull;",
                        "import com.nhaarman.ellie.annotation.Table;",
                        "@Table(\"notes\")",
                        "public class Note extends Model {",
                        "	public static final String TITLE = \"title\";",
                        "	public static final String BODY = \"body\";",
                        "	public static final String DATE = \"date\";",
                        "	public String title;",
                        "	@Column(BODY) @NotNull public String body;",
                        "	@Column(DATE) public Date date;",
                        "   @SetterFor(TITLE) public void setTitle(String title) { this.title = title; }",
                        "}"
                )
        );

        ASSERT.about(javaSource()).that(source)
              .processedWith(ellieProcessors())
              .failsToCompile()
              .withErrorContaining("@SetterFor \"title\" found without a @Column annotated field for \"title\"");
    }
}