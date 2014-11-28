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

package com.nhaarman.ellie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nhaarman.ellie.internal.AdapterHolder;
import com.nhaarman.ellie.internal.DatabaseHelper;
import com.nhaarman.ellie.internal.ModelAdapter;
import com.nhaarman.ellie.internal.RepositoryHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

/**
 * The main class used in Ellie.
 * This class is responsible for database creation/migrations, and instantiates the {@link ModelRepository} classes.
 * Users must call {@link #init(Context, String, int, int, LogLevel)} or any of its variants before using the class in any other way.
 */
@SuppressWarnings("rawtypes")
public final class Ellie {

    /**
     * The default cache size that is used by each {@link ModelRepository}.
     */
    public static final int DEFAULT_CACHE_SIZE = 1024;

    private static final String TAG = "Ellie";

    @NotNull
    private static Ellie sInstance = new Ellie();

    /**
     * Controls the level of logging.
     */
    public enum LogLevel {

        /**
         * No logging.
         */
        NONE,

        /**
         * Log basic events.
         */
        BASIC,

        /**
         * Log all queries.
         */
        FULL;

        /**
         * Returns whether this level is higher than the given level.
         *
         * @param logLevel The {@code LogLevel} to check against.
         *
         * @return {@code true} if this level is higher than given level.
         */
        public boolean log(final LogLevel logLevel) {
            return ordinal() >= logLevel.ordinal();
        }
    }

    /**
     * The {@link AdapterHolder} instance that is instantiated to access the {@link ModelAdapter}s, {@link TypeAdapter}s, and {@link Migration}s.
     * This value is {@code null} until {@link #init(Context, String, int, int, LogLevel)} is successfully called.
     */
    @Nullable
    private AdapterHolder mAdapterHolder;

    /**
     * The {@link RepositoryHolder} instance that is instantiated to access the {@link ModelRepository} instances.
     * This value is {@code null} until {@link #init(Context, String, int, int, LogLevel)} is successfully called.
     */
    @Nullable
    private RepositoryHolder mRepositoryHolder;

    /**
     * The writable {@link SQLiteDatabase} that is used.
     * This value is {@code null} until {@link #init(Context, String, int, int, LogLevel)} is successfully called.
     */
    @Nullable
    private SQLiteDatabase mSQLiteDatabase;

    /**
     * Indicates whether we're fully initialized.
     */
    private boolean mInitialized;

    /**
     * Returns the main instance of {@code Ellie} that is used in classes when not injected.
     * To have this method return a custom instance, call {@link #useInstance(Ellie)} with your instance.
     *
     * @return The {@code Ellie} instance.
     */
    @NotNull
    public static Ellie getInstance() {
        synchronized (Ellie.class) {
            return sInstance;
        }
    }

    /**
     * Instructs Ellie to use given {@code Ellie} instance whenever the {@code Ellie} instance isn't supplied
     * explicitely to classes that depend on {@code Ellie}.
     * After calling this method, {@link #getInstance()} will return given instance.
     *
     * @param ellie The {@code Ellie} instance to use.
     */
    public static void useInstance(@NotNull final Ellie ellie) {
        synchronized (Ellie.class) {
            sInstance = Objects.requireNonNull(ellie);
        }
    }

    /**
     * Initialize the database. Must be called before interacting with the database.
     *
     * @param context Context
     * @param name    The database name.
     * @param version The database version.
     */
    public void init(@NotNull final Context context, @NotNull final String name, final int version) {
        init(context, name, version, DEFAULT_CACHE_SIZE, LogLevel.NONE);
    }

    /**
     * Initialize the database. Must be called before interacting with the database.
     *
     * @param context   Context
     * @param name      The database name.
     * @param version   The database version.
     * @param cacheSize The cache size.
     */
    public void init(@NotNull final Context context, @NotNull final String name, final int version, final int cacheSize) {
        init(context, name, version, cacheSize, LogLevel.NONE);
    }

    /**
     * Initialize the database. Must be called before interacting with the database.
     *
     * @param context  Context
     * @param name     The database name.
     * @param version  The database version.
     * @param logLevel The logging level.
     */
    public void init(@NotNull final Context context, @NotNull final String name, final int version, @NotNull final LogLevel logLevel) {
        init(context, name, version, DEFAULT_CACHE_SIZE, logLevel);
    }

    /**
     * Initialize the database. Must be called before interacting with the database.
     *
     * @param context   Context
     * @param name      The database name.
     * @param version   The database version.
     * @param cacheSize The cache size.
     * @param logLevel  The logging level.
     */
    public void init(@NotNull final Context context, @NotNull final String name, final int version, final int cacheSize, @NotNull final LogLevel logLevel) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(name);
        Objects.requireNonNull(logLevel);

        if (mInitialized) {
            if (logLevel.log(LogLevel.BASIC)) {
                Log.d(TAG, "Already initialized.");
            }
            return;
        }

        mAdapterHolder = instantiateAdapterHolder();
        mSQLiteDatabase = createSQLiteDatabase(context, name, version, mAdapterHolder, logLevel);
        mRepositoryHolder = instantiateRepositoryHolder(mSQLiteDatabase, cacheSize);

        mInitialized = true;
    }

    /**
     * Uses reflection to instantiate the {@link AdapterHolder} class generated by the compiler module.
     *
     * @throws IllegalStateException if the class could not be instantiated.
     */
    @NotNull
    private AdapterHolder instantiateAdapterHolder() {
        try {
            Class<? extends AdapterHolder> adapterHolderClass = (Class<? extends AdapterHolder>) Class.forName(AdapterHolder.IMPL_CLASS_FQCN);
            Constructor<? extends AdapterHolder> constructor = adapterHolderClass.getConstructor(AdapterHolder.CONSTRUCTOR_CLASSES);
            return constructor.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("Could not instantiate AdapterHolder class. Make sure code generation has completed", e);
        }
    }

    /**
     * Creates the {@link SQLiteDatabase} that is used.
     *
     * @param context       Context
     * @param name          The database name.
     * @param version       The database version.
     * @param adapterHolder The {@link AdapterHolder}.
     * @param logLevel      The logging level.
     *
     * @return the {@code SQLiteDatabase}.
     */
    @NotNull
    private SQLiteDatabase createSQLiteDatabase(@NotNull final Context context, @NotNull final String name, final int version,
                                                @NotNull final AdapterHolder adapterHolder, @NotNull final LogLevel logLevel) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context.getApplicationContext(), name, version, adapterHolder, logLevel);
        return databaseHelper.getWritableDatabase();
    }

    /**
     * Uses reflection to instantiate the {@link RepositoryHolder} class generated by the compiler module.
     *
     * @param database  The {@link SQLiteDatabase} to pass to the {@code RepositoryHolder}.
     * @param cacheSize The size of the cache to use for each {@code RepositoryHolder}.
     *
     * @throws IllegalStateException if the class could not be instantiated.
     */
    @NotNull
    private RepositoryHolder instantiateRepositoryHolder(final SQLiteDatabase database, final int cacheSize) {
        try {
            Class<? extends RepositoryHolder> adapterHolderClass = (Class<? extends RepositoryHolder>) Class.forName(RepositoryHolder.IMPL_CLASS_FQCN);
            Constructor<? extends RepositoryHolder> constructor = adapterHolderClass.getConstructor(RepositoryHolder.CONSTRUCTOR_CLASSES);
            return constructor.newInstance(this, database, cacheSize);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("Could not instantiate RepositoryHolder class. Make sure code generation has completed.", e);
        }
    }

    /**
     * Returns the {@link SQLiteDatabase} instance that is used.
     *
     * @return The {@code SQLiteDatabase} instance.
     */
    @NotNull
    public SQLiteDatabase getDatabase() {
        if (mSQLiteDatabase == null) {
            throw new IllegalStateException("Ellie hasn't been initialized yet. Did you call Ellie#init(...)?");
        }

        return mSQLiteDatabase;
    }

    /**
     * Returns the table name for the model represented by given type class.
     *
     * @param cls The type class.
     * @param <T> The Model extension to get the table name for.
     *
     * @return The table name.
     */
    @NotNull
    public <T extends Model> String getTableName(final Class<T> cls) {
        if (mAdapterHolder == null) {
            throw new IllegalStateException("Ellie hasn't been initialized yet. Did you call Ellie#init(...)?");
        }

        ModelAdapter<Model> modelAdapter = Objects.requireNonNull(
                mAdapterHolder.getModelAdapter(cls),
                cls.getSimpleName() + " has no declared table name! Did you add the @Table annotation?"
        );

        return modelAdapter.getTableName();
    }

    /**
     * Returns the {@link ModelRepository} instance for given type class, if it exists.
     *
     * @param cls The type class.
     * @param <T> The type of the class extending {@link Model}.
     *
     * @return The {@code ModelRepository} instance, or {@code null} if it doesn't exist.
     */
    @Nullable
    public <T extends Model> ModelRepository<T> getModelRepository(final Class<T> cls) {
        if (mRepositoryHolder == null) {
            throw new IllegalStateException("Ellie hasn't been initialized yet. Did you call Ellie#init(...)?");
        }

        return mRepositoryHolder.getModelRepository(cls);
    }

    /**
     * Returns all instantiated {@link ModelAdapter}s.
     *
     * @return The list of {@code ModelAdapter}s.
     */
    @NotNull
    List<? extends ModelAdapter> getModelAdapters() {
        if (mAdapterHolder == null) {
            throw new IllegalStateException("Ellie hasn't been initialized yet. Did you call Ellie#init(...)?");
        }

        return mAdapterHolder.getModelAdapters();
    }


    /**
     * Returns the {@link TypeAdapter} to use for (de)serializing classes with given type.
     *
     * @param cls The deserialized type class.
     * @param <D> Deserialized type, i.e. the Java type.
     * @param <S> Serialized type, i.e. the SQLite type.
     *
     * @return The {@code TypeAdapter}, or {@code null} if there is none for given class.
     */
    @Nullable
    <D, S> TypeAdapter<D, S> getTypeAdapter(final Class<D> cls) {
        if (mAdapterHolder == null) {
            throw new IllegalStateException("Ellie hasn't been initialized yet. Did you call Ellie#init(...)?");
        }

        return (TypeAdapter<D, S>) mAdapterHolder.getTypeAdapter(cls);
    }
}