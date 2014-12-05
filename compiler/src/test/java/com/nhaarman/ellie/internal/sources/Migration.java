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

public class Migration {

    public static JavaFileObject fullMigration(final String name, final int version) {
        return JavaFileObjects.forSourceLines(
                "com.nhaarman.test." + name,
                "package com.nhaarman.ellie;",
                "",
                "import com.nhaarman.ellie.BaseMigration;",
                "import com.nhaarman.ellie.annotation.Migration;",
                "",
                "@Migration(version = " + version + ")",
                "public class " + name + " extends BaseMigration {",
                "",
                "    @Override",
                "    public void beforeUp() {",
                "    }",
                "",
                "    @Override",
                "    public String[] getUpStatements() {",
                "        return new String[0];",
                "    }",
                "",
                "    @Override",
                "    public void afterUp() {",
                "    }",
                "",
                "    @Override",
                "    public void beforeDown() {",
                "    }",
                "",
                "    @Override",
                "    public String[] getDownStatements() {",
                "        return new String[0];",
                "    }",
                "",
                "    @Override",
                "    public void afterDown() {",
                "    }",
                "}"
        );
    }

}
