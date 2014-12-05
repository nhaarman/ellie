/*
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
import com.nhaarman.ellie.internal.sources.Note;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static com.nhaarman.ellie.internal.ProcessorTestUtilities.ellieProcessors;
import static org.truth0.Truth.ASSERT;

public class ModelRepositoryTest {

    @Test
    public void modelRepository() {
        JavaFileObject source = Note.fullSource();

        JavaFileObject expectedSource = JavaFileObjects.forSourceLines(
                "com.nhaarman.ellie/Note$$Repository",
                "package com.nhaarman.ellie;",
                "",
                "import android.content.ContentValues;",
                "import android.database.Cursor;",
                "import android.database.sqlite.SQLiteDatabase;",
                "import com.nhaarman.ellie.Ellie;",
                "import com.nhaarman.ellie.ModelRepository;",
                "import com.nhaarman.ellie.Note;",
                "import com.nhaarman.ellie.query.Select;",
                "import com.nhaarman.ellie.util.LruCache;",
                "import java.lang.Long;",
                "import java.util.Date;",
                "",
                "public final class Note$$Repository",
                "    implements ModelRepository<Note> {",
                "",
                "    private final Ellie mEllie;",
                "    private final SQLiteDatabase mDatabase;",
                "    private final LruCache<String, Note> mCache;",
                "",
                "    public Note$$Repository(final Ellie ellie, final SQLiteDatabase database, final int cacheSize) {",
                "        mEllie = ellie;",
                "        mDatabase = database;",
                "        mCache = new LruCache<>(cacheSize);",
                "    }",
                "",
                "    @Override",
                "    public final Note find(final long id) {",
                "        return new Select().from(Note.class).where(\"_id=?\", id).fetchSingle();",
                "    }",
                "",
                "    @Override",
                "    public final Long create(final Note entity) {",
                "        ContentValues values = createContentValues(entity);",
                "        entity.setId(mDatabase.insert(\"notes\", null, values));",
                "        return entity.getId();",
                "    }",
                "",
                "    @Override",
                "    public final Long update(final Note entity) {",
                "        ContentValues values = createContentValues(entity);",
                "        mDatabase.update(\"notes\", values, Model.COLUMN_ID + \"=?\", new String[]{entity.getId().toString()});",
                "        return entity.getId();",
                "    }",
                "",
                "    @Override",
                "    public final void load(final Note entity, final Cursor cursor) {",
                "        entity.setId(cursor.getLong(cursor.getColumnIndex(\"_id\")));",
                "        entity.setTitle(cursor.getString(cursor.getColumnIndex(\"title\")));",
                "        entity.body = cursor.getString(cursor.getColumnIndex(\"body\"));",
                "        entity.date = mEllie.getTypeAdapter(Date.class).deserialize(cursor.getLong(cursor.getColumnIndex(\"date\")));",
                "    }",
                "",
                "    @Override",
                "    public final Long createOrUpdate(final Note entity) {",
                "        if (entity.getId() == null) {",
                "            return create(entity);",
                "        } else {",
                "            return update(entity);",
                "        }",
                "    }",
                "",
                "    @Override",
                "    public final void delete(final Note entity) {",
                "        mDatabase.delete(\"notes\", \"_id=?\", new String[]{entity.getId().toString()});;",
                "    }",
                "",
                "    @Override",
                "    public final void putEntity(final Note entity) {",
                "        if (entity.getId() != null) {",
                "            mCache.put(getEntityIdentifier(entity.getId()), entity);",
                "        }",
                "    }",
                "",
                "    @Override",
                "    public final Note getEntity(final long id) {",
                "        return mCache.get(getEntityIdentifier(id));",
                "    }",
                "",
                "    @Override",
                "    public final void removeEntity(final Note entity) {",
                "        if (entity.getId() != null) {",
                "            mCache.remove(getEntityIdentifier(entity.getId()));",
                "        }",
                "    }",
                "",
                "    @Override",
                "    public final Note getOrFindEntity(final long id) {",
                "        Note entity = getEntity(id);",
                "        if (entity == null) {",
                "            entity = find(id);",
                "        }",
                "        return entity;",
                "    }",
                "",
                "    public final ContentValues createContentValues(final Note entity) {",
                "        ContentValues values = new ContentValues();",
                "        values.put(\"_id\", entity.getId());",
                "        values.put(\"title\", entity.getTitle());",
                "        values.put(\"body\", entity.body);",
                "        values.put(\"date\", (Long) mEllie.getTypeAdapter(Date.class).serialize(entity.date));",
                "        return values;",
                "    }",
                "",
                "    private static String getEntityIdentifier(final long id) {",
                "        return \"Note@\" + id;",
                "    }",
                "",
                "}"
        );

        ASSERT.about(javaSource()).that(source)
              .processedWith(ellieProcessors())
              .compilesWithoutError()
              .and()
              .generatesSources(expectedSource);
    }

    @Test
    public void extendedModelRepository() {
        JavaFileObject noteModelSource = JavaFileObjects.forSourceLines(
                "com.nhaarman.ellie.Note",
                "package com.nhaarman.ellie;",
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
        );

        JavaFileObject noteRepositorySource = JavaFileObjects.forSourceLines(
                "com.nhaarman.ellie.NoteRepository",
                "package com.nhaarman.ellie;",
                "import com.nhaarman.ellie.annotation.RepositoryFor;",
                "@RepositoryFor(Note.class)",
                "public abstract class NoteRepository implements ModelRepository<Note> {",
                "}"
        );

        JavaFileObject expectedSource = JavaFileObjects.forSourceLines(
                "com.nhaarman.ellie/Note$$Repository",
                "package com.nhaarman.ellie;",
                "",
                "import android.content.ContentValues;",
                "import android.database.Cursor;",
                "import android.database.sqlite.SQLiteDatabase;",
                "import com.nhaarman.ellie.Ellie;",
                "import com.nhaarman.ellie.ModelRepository;",
                "import com.nhaarman.ellie.Note;",
                "import com.nhaarman.ellie.NoteRepository;",
                "import com.nhaarman.ellie.query.Select;",
                "import com.nhaarman.ellie.util.LruCache;",
                "import java.lang.Long;",
                "import java.util.Date;",
                "",
                "public final class Note$$Repository extends NoteRepository",
                "    implements ModelRepository<Note> {",
                "",
                "    private final Ellie mEllie;",
                "    private final SQLiteDatabase mDatabase;",
                "    private final LruCache<String, Note> mCache;",
                "",
                "    public Note$$Repository(final Ellie ellie, final SQLiteDatabase database, final int cacheSize) {",
                "        mEllie = ellie;",
                "        mDatabase = database;",
                "        mCache = new LruCache<>(cacheSize);",
                "    }",
                "",
                "    @Override",
                "    public final Note find(final long id) {",
                "        return new Select().from(Note.class).where(\"_id=?\", id).fetchSingle();",
                "    }",
                "",
                "    @Override",
                "    public final Long create(final Note entity) {",
                "        ContentValues values = createContentValues(entity);",
                "        entity.setId(mDatabase.insert(\"notes\", null, values));",
                "        return entity.getId();",
                "    }",
                "",
                "    @Override",
                "    public final Long update(final Note entity) {",
                "        ContentValues values = createContentValues(entity);",
                "        mDatabase.update(\"notes\", values, Model.COLUMN_ID + \"=?\", new String[]{entity.getId().toString()});",
                "        return entity.getId();",
                "    }",
                "",
                "    @Override",
                "    public final void load(final Note entity, final Cursor cursor) {",
                "        entity.setId(cursor.getLong(cursor.getColumnIndex(\"_id\")));",
                "        entity.setTitle(cursor.getString(cursor.getColumnIndex(\"title\")));",
                "        entity.body = cursor.getString(cursor.getColumnIndex(\"body\"));",
                "        entity.date = mEllie.getTypeAdapter(Date.class).deserialize(cursor.getLong(cursor.getColumnIndex(\"date\")));",
                "    }",
                "",
                "    @Override",
                "    public final Long createOrUpdate(final Note entity) {",
                "        if (entity.getId() == null) {",
                "            return create(entity);",
                "        } else {",
                "            return update(entity);",
                "        }",
                "    }",
                "",
                "    @Override",
                "    public final void delete(final Note entity) {",
                "        mDatabase.delete(\"notes\", \"_id=?\", new String[]{entity.getId().toString()});;",
                "    }",
                "",
                "    @Override",
                "    public final void putEntity(final Note entity) {",
                "        if (entity.getId() != null) {",
                "            mCache.put(getEntityIdentifier(entity.getId()), entity);",
                "        }",
                "    }",
                "",
                "    @Override",
                "    public final Note getEntity(final long id) {",
                "        return mCache.get(getEntityIdentifier(id));",
                "    }",
                "",
                "    @Override",
                "    public final void removeEntity(final Note entity) {",
                "        if (entity.getId() != null) {",
                "            mCache.remove(getEntityIdentifier(entity.getId()));",
                "        }",
                "    }",
                "",
                "    @Override",
                "    public final Note getOrFindEntity(final long id) {",
                "        Note entity = getEntity(id);",
                "        if (entity == null) {",
                "            entity = find(id);",
                "        }",
                "        return entity;",
                "    }",
                "",
                "    public final ContentValues createContentValues(final Note entity) {",
                "        ContentValues values = new ContentValues();",
                "        values.put(\"_id\", entity.getId());",
                "        values.put(\"title\", entity.getTitle());",
                "        values.put(\"body\", entity.body);",
                "        values.put(\"date\", (Long) mEllie.getTypeAdapter(Date.class).serialize(entity.date));",
                "        return values;",
                "    }",
                "",
                "    private static String getEntityIdentifier(final long id) {",
                "        return \"Note@\" + id;",
                "    }",
                "",
                "}"
        );

        ASSERT.about(javaSources())
              .that(Lists.newArrayList(noteModelSource, noteRepositorySource))
              .processedWith(ellieProcessors())
              .compilesWithoutError()
              .and()
              .generatesSources(expectedSource);
    }

        @Test
        public void repositoryShouldImplementModelRepository() {
                JavaFileObject noteModelSource = Note.fullSource();

                JavaFileObject noteRepositorySource = JavaFileObjects.forSourceLines(
                        "com.nhaarman.ellie.NoteRepository",
                        "package com.nhaarman.ellie;",
                        "import com.nhaarman.ellie.annotation.RepositoryFor;",
                        "@RepositoryFor(Note.class)",
                        "public abstract class NoteRepository {",
                        "}"
                );

                ASSERT.about(javaSources())
                      .that(Lists.newArrayList(noteModelSource, noteRepositorySource))
                      .processedWith(ellieProcessors())
                      .failsToCompile()
                      .withErrorContaining("Classes annotated with @RepositoryFor should implement ModelRepository");
        }


        @Test
        public void repositoryShouldBeAbstract() {
                JavaFileObject noteModelSource = Note.fullSource();
                JavaFileObject noteRepositorySource = JavaFileObjects.forSourceLines(
                        "com.nhaarman.ellie.NoteRepository",
                        "package com.nhaarman.ellie;",
                        "import com.nhaarman.ellie.annotation.RepositoryFor;",
                        "@RepositoryFor(Note.class)",
                        "public class NoteRepository implements ModelRepository<Note> {",
                        "}"
                );

                ASSERT.about(javaSources())
                      .that(Lists.newArrayList(noteModelSource, noteRepositorySource))
                      .processedWith(ellieProcessors())
                      .failsToCompile()
                      .withErrorContaining("Classes annotated with @RepositoryFor should be abstract");
        }

}
