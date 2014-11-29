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

package com.nhaarman.ellie.internal;

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
import android.util.SparseArray;

import com.nhaarman.ellie.BaseMigration;
import com.nhaarman.ellie.Ellie.LogLevel;
import com.nhaarman.ellie.Model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The DatabaseHelper that is used internally.
 * <p/>
 * Manages database creation and migrations.
 */
@SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
public class DatabaseHelper extends SQLiteOpenHelper {

    @NotNull
    private final AdapterHolder mAdapterHolder;

    public DatabaseHelper(@NotNull final Context context, @NotNull final String name, final int version,
                          @NotNull final AdapterHolder adapterHolder, @NotNull final LogLevel logLevel) {
        super(context, name, logLevel.log(LogLevel.FULL) ? new LoggingCursorAdapter() : null, version);
        mAdapterHolder = adapterHolder;
    }

    @Override
    public void onOpen(@NotNull final SQLiteDatabase db) {
        executePragmas(db);
    }

    @Override
    public void onCreate(@NotNull final SQLiteDatabase sqLiteDatabase) {
        executePragmas(sqLiteDatabase);
        executeCreate(sqLiteDatabase);
        executeUpMigrations(sqLiteDatabase, -1, sqLiteDatabase.getVersion());
    }

    @Override
    public void onUpgrade(@NotNull final SQLiteDatabase sqLiteDatabase, final int oldVersion, final int newVersion) {
        executePragmas(sqLiteDatabase);
        executeCreate(sqLiteDatabase);
        executeUpMigrations(sqLiteDatabase, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(final SQLiteDatabase sqLiteDatabase, final int oldVersion, final int newVersion) {
        executePragmas(sqLiteDatabase);
        executeDownMigrations(sqLiteDatabase, oldVersion, newVersion);
    }

    /**
     * Executes PRAGMA statements, enabling foreign key constraints.
     *
     * @param db The {@link SQLiteDatabase}.
     */
    private void executePragmas(@NotNull final SQLiteDatabase db) {
        if (VERSION.SDK_INT >= VERSION_CODES.FROYO) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    /**
     * Executes the CREATE statements to create the tables for the {@link Model}s.
     *
     * @param db The {@link SQLiteDatabase}.
     */
    private void executeCreate(@NotNull final SQLiteDatabase db) {
        final List<String> tableDefinitions = new ArrayList<>();
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

    /**
     * Executes the up migrations for given versions.
     *
     * @param db         The {@link SQLiteDatabase}.
     * @param oldVersion The old version of the database.
     * @param newVersion The new version of the database.
     */
    private void executeUpMigrations(@NotNull final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        SparseArray<? extends BaseMigration> migrations = mAdapterHolder.getMigrations();

        db.beginTransaction();
        try {
            for (int version = oldVersion + 1; version <= newVersion; version++) {
                BaseMigration baseMigration = migrations.get(version);
                if (baseMigration != null) {
                    baseMigration.beforeUp();
                    for (String statement : baseMigration.getUpStatements()) {
                        db.execSQL(statement);
                    }
                    baseMigration.afterUp();
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Executes the down migrations for given versions.
     *
     * @param db         The {@link SQLiteDatabase}.
     * @param oldVersion The old version of the database.
     * @param newVersion The new version of the database.
     */
    private void executeDownMigrations(@NotNull final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        SparseArray<? extends BaseMigration> migrations = mAdapterHolder.getMigrations();

        db.beginTransaction();
        try {
            for (int version = oldVersion; version > newVersion; version--) {
                BaseMigration baseMigration = migrations.get(version);
                if (baseMigration != null) {
                    baseMigration.beforeDown();
                    for (String statement : baseMigration.getDownStatements()) {
                        db.execSQL(statement);
                    }
                    baseMigration.afterDown();
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private static class LoggingCursorAdapter implements CursorFactory {

        private static final String TAG = "Ellie.query";

        @Override
        public Cursor newCursor(@NotNull final SQLiteDatabase sqLiteDatabase, @NotNull final SQLiteCursorDriver sqLiteCursorDriver, @NotNull final String editTable, @NotNull
        final SQLiteQuery sqLiteQuery) {
            Log.v(TAG, sqLiteQuery.toString());
            return new SQLiteCursor(sqLiteCursorDriver, editTable, sqLiteQuery);
        }
    }
}