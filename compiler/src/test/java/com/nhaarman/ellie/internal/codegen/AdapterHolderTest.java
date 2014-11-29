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

package com.nhaarman.ellie.internal.codegen;

import com.google.common.collect.Lists;
import com.google.testing.compile.JavaFileObjects;
import com.nhaarman.ellie.internal.sources.Migration;
import com.nhaarman.ellie.internal.sources.Note;
import com.nhaarman.ellie.internal.sources.Tag;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static com.nhaarman.ellie.internal.ProcessorTestUtilities.ellieProcessors;
import static org.truth0.Truth.ASSERT;

@SuppressWarnings({"HardCodedStringLiteral", "JUnitTestMethodWithNoAssertions"})
public class AdapterHolderTest {


    @Test
    public void noModelsNoMigrations() {
        JavaFileObject source = JavaFileObjects.forSourceLines(
                "com.nhaarman.ellie.test.Dummy",
                "package com.nhaarman.ellie.test;",
                "public class Dummy {",
                "}"
        );

        ASSERT.about(javaSource())
              .that(source)
              .processedWith(ellieProcessors())
              .compilesWithoutError();
    }

    @Test
    public void singleModel() {
        JavaFileObject noteModelSource = Note.fullSource();

        JavaFileObject adapterHolderSource = JavaFileObjects.forSourceLines(
                "com.nhaarman.ellie.AdapterHolderImpl",
                "package com.nhaarman.ellie;",
                "",
                "import android.util.SparseArray;",
                "import com.nhaarman.ellie.BaseMigration;",
                "import com.nhaarman.ellie.Note;",
                "import com.nhaarman.ellie.Note$$ModelAdapter;",
                "import com.nhaarman.ellie.adapter.BooleanAdapter;",
                "import com.nhaarman.ellie.adapter.CalendarAdapter;",
                "import com.nhaarman.ellie.adapter.SqlDateAdapter;",
                "import com.nhaarman.ellie.adapter.UtilDateAdapter;",
                "import com.nhaarman.ellie.internal.AdapterHolder;",
                "import com.nhaarman.ellie.internal.ModelAdapter;",
                "import java.util.ArrayList;",
                "import java.util.Collections;",
                "import java.util.HashMap;",
                "import java.util.List;",
                "import java.util.Map;",
                "",
                "public final class AdapterHolderImpl",
                "    implements AdapterHolder {",
                "",
                "    private final SparseArray<BaseMigration> mMigrations = new SparseArray<>(0);",
                "    private final Map<Class<? extends Model>, ModelAdapter<?>> mModelAdapters = new HashMap<>();",
                "    private final Map<Class<?>, TypeAdapter<?, ?>> mTypeAdapters = new HashMap<Class<?>, TypeAdapter<?, ?>>();",
                "",
                "    public AdapterHolderImpl() {",
                "        createMigrations();",
                "        createModelAdapters();",
                "        createTypeAdapters();",
                "    }",
                "",
                "    private void createMigrations() {",
                "    }",
                "",
                "    private void createModelAdapters() {",
                "        mModelAdapters.put(Note.class, new Note$$ModelAdapter());",
                "    }",
                "",
                "    private void createTypeAdapters() {",
                "        mTypeAdapters.put(java.lang.Boolean.class, new BooleanAdapter());",
                "        mTypeAdapters.put(java.util.Calendar.class, new CalendarAdapter());",
                "        mTypeAdapters.put(java.sql.Date.class, new SqlDateAdapter());",
                "        mTypeAdapters.put(java.util.Date.class, new UtilDateAdapter());",
                "    }",
                "",
                "    @Override",
                "    public SparseArray<? extends BaseMigration> getMigrations() {",
                "        return mMigrations;",
                "    }",
                "",
                "    @Override",
                "    public <T extends Model> ModelAdapter<T> getModelAdapter(final Class<? extends Model> cls) {",
                "        return (ModelAdapter<T>) mModelAdapters.get(cls);",
                "    }",
                "",
                "    @Override",
                "    public List<? extends ModelAdapter> getModelAdapters() {",
                "        return Collections.unmodifiableList(new ArrayList<>(mModelAdapters.values()));",
                "    }",
                "",
                "    @Override",
                "    public <D, S> TypeAdapter<D, S> getTypeAdapter(final Class<D> cls) {",
                "        return (TypeAdapter<D, S>) mTypeAdapters.get(cls);",
                "    }",
                "}"
        );

        ASSERT.about(javaSource())
              .that(noteModelSource)
              .processedWith(ellieProcessors())
              .compilesWithoutError()
              .and()
              .generatesSources(adapterHolderSource);
    }

    @Test
    public void twoModels() {
        JavaFileObject noteModelSource = Note.fullSource();
        JavaFileObject tagModelSource = Tag.fullSource();

        JavaFileObject expectedAdapterHolderSource = JavaFileObjects.forSourceLines(
                "com.nhaarman.ellie.AdapterHolderImpl",
                "package com.nhaarman.ellie;",
                "",
                "import android.util.SparseArray;",
                "import com.nhaarman.ellie.BaseMigration;",
                "import com.nhaarman.ellie.Note;",
                "import com.nhaarman.ellie.Note$$ModelAdapter;",
                "import com.nhaarman.ellie.Tag;",
                "import com.nhaarman.ellie.Tag$$ModelAdapter;",
                "import com.nhaarman.ellie.adapter.BooleanAdapter;",
                "import com.nhaarman.ellie.adapter.CalendarAdapter;",
                "import com.nhaarman.ellie.adapter.SqlDateAdapter;",
                "import com.nhaarman.ellie.adapter.UtilDateAdapter;",
                "import com.nhaarman.ellie.internal.AdapterHolder;",
                "import com.nhaarman.ellie.internal.ModelAdapter;",
                "import java.util.ArrayList;",
                "import java.util.Collections;",
                "import java.util.HashMap;",
                "import java.util.List;",
                "import java.util.Map;",
                "",
                "public final class AdapterHolderImpl",
                "    implements AdapterHolder {",
                "",
                "    private final SparseArray<BaseMigration> mMigrations = new SparseArray<>(0);",
                "    private final Map<Class<? extends Model>, ModelAdapter<?>> mModelAdapters = new HashMap<>();",
                "    private final Map<Class<?>, TypeAdapter<?, ?>> mTypeAdapters = new HashMap<Class<?>, TypeAdapter<?, ?>>();",
                "",
                "    public AdapterHolderImpl() {",
                "        createMigrations();",
                "        createModelAdapters();",
                "        createTypeAdapters();",
                "    }",
                "",
                "    private void createMigrations() {",
                "    }",
                "",
                "    private void createModelAdapters() {",
                "        mModelAdapters.put(Note.class, new Note$$ModelAdapter());",
                "        mModelAdapters.put(Tag.class, new Tag$$ModelAdapter());",
                "    }",
                "",
                "    private void createTypeAdapters() {",
                "        mTypeAdapters.put(java.lang.Boolean.class, new BooleanAdapter());",
                "        mTypeAdapters.put(java.util.Calendar.class, new CalendarAdapter());",
                "        mTypeAdapters.put(java.sql.Date.class, new SqlDateAdapter());",
                "        mTypeAdapters.put(java.util.Date.class, new UtilDateAdapter());",
                "    }",
                "",
                "    @Override",
                "    public SparseArray<? extends BaseMigration> getMigrations() {",
                "        return mMigrations;",
                "    }",
                "",
                "    @Override",
                "    public <T extends Model> ModelAdapter<T> getModelAdapter(final Class<? extends Model> cls) {",
                "        return (ModelAdapter<T>) mModelAdapters.get(cls);",
                "    }",
                "",
                "    @Override",
                "    public List<? extends ModelAdapter> getModelAdapters() {",
                "        return Collections.unmodifiableList(new ArrayList<>(mModelAdapters.values()));",
                "    }",
                "",
                "    @Override",
                "    public <D, S> TypeAdapter<D, S> getTypeAdapter(final Class<D> cls) {",
                "        return (TypeAdapter<D, S>) mTypeAdapters.get(cls);",
                "    }",
                "}"
        );

        ASSERT.about(javaSources())
              .that(Lists.newArrayList(noteModelSource, tagModelSource))
              .processedWith(ellieProcessors())
              .compilesWithoutError()
              .and()
              .generatesSources(expectedAdapterHolderSource);
    }

    @Test
    public void singleMigration() {
        JavaFileObject fullMigrationSource = Migration.fullMigration("FullMigration", 1);

        JavaFileObject adapterHolderSource = JavaFileObjects.forSourceLines(
                "com.nhaarman.ellie.AdapterHolderImpl",
                "package com.nhaarman.ellie;",
                "",
                "import android.util.SparseArray;",
                "import com.nhaarman.ellie.BaseMigration;",
                "import com.nhaarman.ellie.FullMigration;",
                "import com.nhaarman.ellie.adapter.BooleanAdapter;",
                "import com.nhaarman.ellie.adapter.CalendarAdapter;",
                "import com.nhaarman.ellie.adapter.SqlDateAdapter;",
                "import com.nhaarman.ellie.adapter.UtilDateAdapter;",
                "import com.nhaarman.ellie.internal.AdapterHolder;",
                "import com.nhaarman.ellie.internal.ModelAdapter;",
                "import java.util.ArrayList;",
                "import java.util.Collections;",
                "import java.util.HashMap;",
                "import java.util.List;",
                "import java.util.Map;",
                "",
                "public final class AdapterHolderImpl",
                "    implements AdapterHolder {",
                "",
                "    private final SparseArray<BaseMigration> mMigrations = new SparseArray<>(1);",
                "    private final Map<Class<? extends Model>, ModelAdapter<?>> mModelAdapters = new HashMap<>();",
                "    private final Map<Class<?>, TypeAdapter<?, ?>> mTypeAdapters = new HashMap<Class<?>, TypeAdapter<?, ?>>();",
                "",
                "    public AdapterHolderImpl() {",
                "        createMigrations();",
                "        createModelAdapters();",
                "        createTypeAdapters();",
                "    }",
                "",
                "    private void createMigrations() {",
                "        mMigrations.put(1, new FullMigration());",
                "    }",
                "",
                "    private void createModelAdapters() {",
                "    }",
                "",
                "    private void createTypeAdapters() {",
                "        mTypeAdapters.put(java.lang.Boolean.class, new BooleanAdapter());",
                "        mTypeAdapters.put(java.util.Calendar.class, new CalendarAdapter());",
                "        mTypeAdapters.put(java.sql.Date.class, new SqlDateAdapter());",
                "        mTypeAdapters.put(java.util.Date.class, new UtilDateAdapter());",
                "    }",
                "",
                "    @Override",
                "    public SparseArray<? extends BaseMigration> getMigrations() {",
                "        return mMigrations;",
                "    }",
                "",
                "    @Override",
                "    public <T extends Model> ModelAdapter<T> getModelAdapter(final Class<? extends Model> cls) {",
                "        return (ModelAdapter<T>) mModelAdapters.get(cls);",
                "    }",
                "",
                "    @Override",
                "    public List<? extends ModelAdapter> getModelAdapters() {",
                "        return Collections.unmodifiableList(new ArrayList<>(mModelAdapters.values()));",
                "    }",
                "",
                "    @Override",
                "    public <D, S> TypeAdapter<D, S> getTypeAdapter(final Class<D> cls) {",
                "        return (TypeAdapter<D, S>) mTypeAdapters.get(cls);",
                "    }",
                "}"
        );

        ASSERT.about(javaSource())
              .that(fullMigrationSource)
              .processedWith(ellieProcessors())
              .compilesWithoutError()
              .and()
              .generatesSources(adapterHolderSource);
    }

    @Test
    public void twoMigrations() {
        JavaFileObject fullMigration1Source = Migration.fullMigration("FullMigration1", 1);
        JavaFileObject fullMigration2Source = Migration.fullMigration("FullMigration2", 2);

        JavaFileObject adapterHolderSource = JavaFileObjects.forSourceLines(
                "com.nhaarman.ellie.AdapterHolderImpl",
                "package com.nhaarman.ellie;",
                "",
                "import android.util.SparseArray;",
                "import com.nhaarman.ellie.BaseMigration;",
                "import com.nhaarman.ellie.FullMigration1;",
                "import com.nhaarman.ellie.FullMigration2;",
                "import com.nhaarman.ellie.adapter.BooleanAdapter;",
                "import com.nhaarman.ellie.adapter.CalendarAdapter;",
                "import com.nhaarman.ellie.adapter.SqlDateAdapter;",
                "import com.nhaarman.ellie.adapter.UtilDateAdapter;",
                "import com.nhaarman.ellie.internal.AdapterHolder;",
                "import com.nhaarman.ellie.internal.ModelAdapter;",
                "import java.util.ArrayList;",
                "import java.util.Collections;",
                "import java.util.HashMap;",
                "import java.util.List;",
                "import java.util.Map;",
                "",
                "public final class AdapterHolderImpl",
                "    implements AdapterHolder {",
                "",
                "    private final SparseArray<BaseMigration> mMigrations = new SparseArray<>(2);",
                "    private final Map<Class<? extends Model>, ModelAdapter<?>> mModelAdapters = new HashMap<>();",
                "    private final Map<Class<?>, TypeAdapter<?, ?>> mTypeAdapters = new HashMap<Class<?>, TypeAdapter<?, ?>>();",
                "",
                "    public AdapterHolderImpl() {",
                "        createMigrations();",
                "        createModelAdapters();",
                "        createTypeAdapters();",
                "    }",
                "",
                "    private void createMigrations() {",
                "        mMigrations.put(1, new FullMigration1());",
                "        mMigrations.put(2, new FullMigration2());",
                "    }",
                "",
                "    private void createModelAdapters() {",
                "    }",
                "",
                "    private void createTypeAdapters() {",
                "        mTypeAdapters.put(java.lang.Boolean.class, new BooleanAdapter());",
                "        mTypeAdapters.put(java.util.Calendar.class, new CalendarAdapter());",
                "        mTypeAdapters.put(java.sql.Date.class, new SqlDateAdapter());",
                "        mTypeAdapters.put(java.util.Date.class, new UtilDateAdapter());",
                "    }",
                "",
                "    @Override",
                "    public SparseArray<? extends BaseMigration> getMigrations() {",
                "        return mMigrations;",
                "    }",
                "",
                "    @Override",
                "    public <T extends Model> ModelAdapter<T> getModelAdapter(final Class<? extends Model> cls) {",
                "        return (ModelAdapter<T>) mModelAdapters.get(cls);",
                "    }",
                "",
                "    @Override",
                "    public List<? extends ModelAdapter> getModelAdapters() {",
                "        return Collections.unmodifiableList(new ArrayList<>(mModelAdapters.values()));",
                "    }",
                "",
                "    @Override",
                "    public <D, S> TypeAdapter<D, S> getTypeAdapter(final Class<D> cls) {",
                "        return (TypeAdapter<D, S>) mTypeAdapters.get(cls);",
                "    }",
                "}"
        );

        ASSERT.about(javaSources())
              .that(Lists.newArrayList(fullMigration1Source, fullMigration2Source))
              .processedWith(ellieProcessors())
              .compilesWithoutError()
              .and()
              .generatesSources(adapterHolderSource);
    }

    @Test
    public void fullAdapterHolder() {
        JavaFileObject fullMigration1Source = Migration.fullMigration("FullMigration1", 1);
        JavaFileObject fullMigration2Source = Migration.fullMigration("FullMigration2", 2);

        JavaFileObject noteModelSource = Note.fullSource();
        JavaFileObject tagModelSource = Tag.fullSource();

        JavaFileObject adapterHolderSource = JavaFileObjects.forSourceLines(
                "com.nhaarman.ellie.AdapterHolderImpl",
                "package com.nhaarman.ellie;",
                "",
                "import android.util.SparseArray;",
                "import com.nhaarman.ellie.BaseMigration;",
                "import com.nhaarman.ellie.FullMigration1;",
                "import com.nhaarman.ellie.FullMigration2;",
                "import com.nhaarman.ellie.Note;",
                "import com.nhaarman.ellie.Note$$ModelAdapter;",
                "import com.nhaarman.ellie.Tag;",
                "import com.nhaarman.ellie.Tag$$ModelAdapter;",
                "import com.nhaarman.ellie.adapter.BooleanAdapter;",
                "import com.nhaarman.ellie.adapter.CalendarAdapter;",
                "import com.nhaarman.ellie.adapter.SqlDateAdapter;",
                "import com.nhaarman.ellie.adapter.UtilDateAdapter;",
                "import com.nhaarman.ellie.internal.AdapterHolder;",
                "import com.nhaarman.ellie.internal.ModelAdapter;",
                "import java.util.ArrayList;",
                "import java.util.Collections;",
                "import java.util.HashMap;",
                "import java.util.List;",
                "import java.util.Map;",
                "",
                "public final class AdapterHolderImpl",
                "    implements AdapterHolder {",
                "",
                "    private final SparseArray<BaseMigration> mMigrations = new SparseArray<>(2);",
                "    private final Map<Class<? extends Model>, ModelAdapter<?>> mModelAdapters = new HashMap<>();",
                "    private final Map<Class<?>, TypeAdapter<?, ?>> mTypeAdapters = new HashMap<Class<?>, TypeAdapter<?, ?>>();",
                "",
                "    public AdapterHolderImpl() {",
                "        createMigrations();",
                "        createModelAdapters();",
                "        createTypeAdapters();",
                "    }",
                "",
                "    private void createMigrations() {",
                "        mMigrations.put(1, new FullMigration1());",
                "        mMigrations.put(2, new FullMigration2());",
                "    }",
                "",
                "    private void createModelAdapters() {",
                "        mModelAdapters.put(Note.class, new Note$$ModelAdapter());",
                "        mModelAdapters.put(Tag.class, new Tag$$ModelAdapter());",
                "    }",
                "",
                "    private void createTypeAdapters() {",
                "        mTypeAdapters.put(java.lang.Boolean.class, new BooleanAdapter());",
                "        mTypeAdapters.put(java.util.Calendar.class, new CalendarAdapter());",
                "        mTypeAdapters.put(java.sql.Date.class, new SqlDateAdapter());",
                "        mTypeAdapters.put(java.util.Date.class, new UtilDateAdapter());",
                "    }",
                "",
                "    @Override",
                "    public SparseArray<? extends BaseMigration> getMigrations() {",
                "        return mMigrations;",
                "    }",
                "",
                "    @Override",
                "    public <T extends Model> ModelAdapter<T> getModelAdapter(final Class<? extends Model> cls) {",
                "        return (ModelAdapter<T>) mModelAdapters.get(cls);",
                "    }",
                "",
                "    @Override",
                "    public List<? extends ModelAdapter> getModelAdapters() {",
                "        return Collections.unmodifiableList(new ArrayList<>(mModelAdapters.values()));",
                "    }",
                "",
                "    @Override",
                "    public <D, S> TypeAdapter<D, S> getTypeAdapter(final Class<D> cls) {",
                "        return (TypeAdapter<D, S>) mTypeAdapters.get(cls);",
                "    }",
                "}"
        );

        ASSERT.about(javaSources())
              .that(
                      Lists.newArrayList(
                              fullMigration1Source,
                              fullMigration2Source,
                              noteModelSource,
                              tagModelSource
                      )
              )
              .processedWith(ellieProcessors())
              .compilesWithoutError()
              .and()
              .generatesSources(adapterHolderSource);
    }

}