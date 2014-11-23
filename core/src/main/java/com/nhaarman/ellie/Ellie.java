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
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;

import com.nhaarman.ellie.internal.AdapterHolder;
import com.nhaarman.ellie.internal.ModelAdapter;
import com.nhaarman.ellie.internal.RepositoryHolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public final class Ellie {

    public static final int DEFAULT_CACHE_SIZE = 1024;

    private static final String TAG = "Ellie";

    private static Ellie sInstance;

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

        public boolean log(final LogLevel logLevel) {
            return ordinal() >= logLevel.ordinal();
        }
    }

    private Context mContext;

    private AdapterHolder mAdapterHolder;

    private RepositoryHolder mRepositoryHolder;

    private SQLiteDatabase mSQLiteDatabase;

    private LogLevel mLogLevel = LogLevel.NONE;

    private boolean mInitialized;

    public Ellie() {
    }

    public static Ellie getInstance() {
        synchronized (Ellie.class) {
            if (sInstance == null) {
                sInstance = new Ellie();
            }

            return sInstance;
        }
    }

    // Public methods

    /**
     * Initialize the database. Must be called before interacting with the database.
     *
     * @param context Context
     * @param name    The database name.
     * @param version The database version.
     */
    public void init(final Context context, final String name, final int version) {
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
    public void init(final Context context, final String name, final int version, final int cacheSize) {
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
    public void init(final Context context, final String name, final int version, final LogLevel logLevel) {
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
    public void init(final Context context, final String name, final int version, final int cacheSize, final LogLevel logLevel) {
        if (mInitialized) {
            if (logLevel.log(LogLevel.BASIC)) {
                Log.d(TAG, "Already initialized.");
            }
            return;
        }

        mContext = context.getApplicationContext();
        mLogLevel = logLevel;

        try {
            Class adapterHolderClass = Class.forName(AdapterHolder.IMPL_CLASS_FQCN);
            mAdapterHolder = (AdapterHolder) adapterHolderClass.newInstance();
        } catch (Exception e) {
            if (mLogLevel.log(LogLevel.BASIC)) {
                Log.e(TAG, "Failed to initialize.", e);
            }
            throw new RuntimeException(e);
        }

        DatabaseHelper databaseHelper = new DatabaseHelper(mContext, name, version);
        mSQLiteDatabase = databaseHelper.getWritableDatabase();

        try {
            Class adapterHolderClass = Class.forName(RepositoryHolder.IMPL_CLASS_FQCN);
            mRepositoryHolder = (RepositoryHolder) adapterHolderClass.getConstructor(Ellie.class, SQLiteDatabase.class, int.class).newInstance(this, mSQLiteDatabase, cacheSize);
        } catch (Exception e) {
            if (mLogLevel.log(LogLevel.BASIC)) {
                Log.e(TAG, "Failed to initialize.", e);
            }
            throw new RuntimeException(e);
        }

        mInitialized = true;
    }

    public Context getContext() {
        return mContext;
    }

    public SQLiteDatabase getDatabase() {
        return mSQLiteDatabase;
    }

    public <T extends Model> String getTableName(final Class<T> cls) {
        return mAdapterHolder.getModelAdapter(cls).getTableName();
    }

    // Convenience methods

    /**
     * Iterate over a cursor and load entities.
     *
     * @param <T>    The Model type
     * @param cls    The Model class.
     * @param cursor The result cursor.
     *
     * @return The list of entities.
     */
    public <T extends Model> List<T> processCursor(final Class<T> cls, final Cursor cursor) {
        final List<T> entities = new ArrayList<T>();
        try {
            Constructor<T> entityConstructor = cls.getConstructor();
            if (cursor.moveToFirst()) {
                do {
                    T entity = getModelRepository(cls).getEntity(cursor.getLong(cursor.getColumnIndex(Model.COLUMN_ID)));
                    if (entity == null) {
                        entity = entityConstructor.newInstance();
                    }

                    entity.load(cursor);
                    entities.add(entity);
                }
                while (cursor.moveToNext());
            }
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

        return entities;
    }

    /**
     * Iterate over a cursor and load entities. Closes the cursor when finished.
     *
     * @param <T>    The Model type
     * @param cls    The model class.
     * @param cursor The result cursor.
     *
     * @return The list of entities.
     */
    public <T extends Model> List<T> processAndCloseCursor(final Class<T> cls, final Cursor cursor) {
        List<T> entities = processCursor(cls, cursor);
        cursor.close();
        return entities;
    }

    // Finder methods

    List<? extends ModelAdapter> getModelAdapters() {
        return mAdapterHolder.getModelAdapters();
    }

    public <T extends Model> ModelRepository<T> getModelRepository(final Class<T> cls) {
        return mRepositoryHolder.getModelRepository(cls);
    }

    <D, S> TypeAdapter<D, S> getTypeAdapter(final Class<D> cls) {
        return (TypeAdapter<D, S>) mAdapterHolder.getTypeAdapter(cls);
    }

    // Private classes

    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(final Context context, final String name, final int version) {
            super(context, name, mLogLevel.log(LogLevel.FULL) ? new LoggingCursorAdapter() : null, version);
        }

        @Override
        public void onOpen(final SQLiteDatabase db) {
            executePragmas(db);
        }

        @Override
        public void onCreate(final SQLiteDatabase sqLiteDatabase) {
            executePragmas(sqLiteDatabase);
            executeCreate(sqLiteDatabase);
            executeMigrations(sqLiteDatabase, -1, sqLiteDatabase.getVersion());
        }

        @Override
        public void onUpgrade(final SQLiteDatabase sqLiteDatabase, final int oldVersion, final int newVersion) {
            executePragmas(sqLiteDatabase);
            executeCreate(sqLiteDatabase);
            executeMigrations(sqLiteDatabase, oldVersion, newVersion);
        }

        private void executePragmas(final SQLiteDatabase db) {
            if (VERSION.SDK_INT >= VERSION_CODES.FROYO) {
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        }

        private void executeCreate(final SQLiteDatabase db) {
            final List<String> tableDefinitions = new ArrayList<String>();
            for (ModelAdapter<?> modelAdapter : mAdapterHolder.getModelAdapters()) {
                tableDefinitions.add(modelAdapter.getSchema());
            }

            db.beginTransaction();
            try {
                for (String tableDefinition : tableDefinitions) {
                    db.execSQL(tableDefinition);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        private boolean executeMigrations(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            boolean migrationExecuted = false;
            final List<? extends Migration> migrations = mAdapterHolder.getMigrations();

            db.beginTransaction();
            try {
                for (Migration migration : migrations) {
                    if (migration.getVersion() > oldVersion && migration.getVersion() <= newVersion) {
                        for (String statement : migration.getStatements()) {
                            db.execSQL(statement);
                        }
                        migrationExecuted = true;
                    }
                }
            } finally {
                db.setTransactionSuccessful();
            }
            db.endTransaction();

            return migrationExecuted;
        }
    }

    private static class LoggingCursorAdapter implements CursorFactory {

        @Override
        public Cursor newCursor(final SQLiteDatabase sqLiteDatabase, final SQLiteCursorDriver sqLiteCursorDriver, final String editTable, final SQLiteQuery sqLiteQuery) {
            Log.v(TAG, sqLiteQuery.toString());
            return new SQLiteCursor(sqLiteDatabase, sqLiteCursorDriver, editTable, sqLiteQuery);
        }
    }
}