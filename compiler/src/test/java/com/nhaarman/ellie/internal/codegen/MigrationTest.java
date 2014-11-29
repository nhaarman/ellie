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

import com.google.common.collect.Lists;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static com.nhaarman.ellie.internal.ProcessorTestUtilities.ellieProcessors;
import static org.truth0.Truth.ASSERT;

@SuppressWarnings("HardCodedStringLiteral")
public class MigrationTest {

    @Test
    public void emptyMigration() {
        JavaFileObject source = JavaFileObjects.forSourceLines(
                "com.nhaarman.ellie.test.AddNameColumnToNotes",
                "package com.nhaarman.ellie.test;",
                "import com.nhaarman.ellie.annotation.Migration;",
                "import com.nhaarman.ellie.BaseMigration;",
                "@Migration(version = 1)",
                "public class AddNameColumnToNotes extends BaseMigration {",
                "}"
        );

        ASSERT.about(javaSource())
              .that(source)
              .processedWith(ellieProcessors())
              .compilesWithoutError();
    }

    @Test
    public void invalidMigrationType() {
        JavaFileObject source = JavaFileObjects.forSourceLines(
                "com.nhaarman.ellie.test.AddNameColumnToNotes",
                "package com.nhaarman.ellie.test;",
                "import com.nhaarman.ellie.annotation.Migration;",
                "public class AddNameColumnToNotes {",
                "    @Migration(version = 1) String mString;",
                "}"
        );

        ASSERT.about(javaSource())
              .that(source)
              .processedWith(ellieProcessors())
              .failsToCompile()
              .withErrorContaining("annotation type not applicable to this kind of declaration");
    }

    @Test
    public void invalidMigrationParent() {
        JavaFileObject source = JavaFileObjects.forSourceLines(
                "com.nhaarman.ellie.test.AddNameColumnToNotes",
                "package com.nhaarman.ellie.test;",
                "import com.nhaarman.ellie.annotation.Migration;",
                "@Migration(version = 1)",
                "public class AddNameColumnToNotes {",
                "}"
        );

        ASSERT.about(javaSource())
              .that(source)
              .processedWith(ellieProcessors())
              .failsToCompile()
              .withErrorContaining("Classes annotated with @Migration should extend BaseMigration.");
    }

    @Test
    public void duplicateMigrationVersions() {
        JavaFileObject firstSource = JavaFileObjects.forSourceLines(
                "com.nhaarman.ellie.test.AddNameColumnToNotes",
                "package com.nhaarman.ellie.test;",
                "import com.nhaarman.ellie.annotation.Migration;",
                "import com.nhaarman.ellie.BaseMigration;",
                "@Migration(version = 1)",
                "public class AddNameColumnToNotes extends BaseMigration {",
                "}"
        );

        JavaFileObject secondSource = JavaFileObjects.forSourceLines(
                "com.nhaarman.ellie.test.AddNameColumnToTags",
                "package com.nhaarman.ellie.test;",
                "import com.nhaarman.ellie.annotation.Migration;",
                "import com.nhaarman.ellie.BaseMigration;",
                "@Migration(version = 1)",
                "public class AddNameColumnToTags extends BaseMigration {",
                "}"
        );

        ASSERT.about(javaSources())
              .that(Lists.newArrayList(firstSource, secondSource))
              .processedWith(ellieProcessors())
              .failsToCompile()
              .withErrorContaining("Found two Migrations with the same version");
    }
}
