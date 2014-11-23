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

package com.nhaarman.ellie.util;

import android.os.CancellationSignal;

import com.nhaarman.ellie.Ellie;
import com.nhaarman.ellie.Model;

import java.util.List;

import static android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * Utility class for interacting with the database using SQLiteDatabase methods.
 */
public class QueryUtils {
    // TODO: Duplicate methods using provided Ellie instance

    public static void execSQL(final String sql) {
        Ellie.getInstance().getDatabase().execSQL(sql);
    }

    public static void execSQL(final String sql, final String[] selectionArgs) {
        Ellie.getInstance().getDatabase().execSQL(sql, selectionArgs);
    }

    public static <T extends Model> List<T> query(final Class<T> cls, final boolean distinct, final String[] columns, final String selection,
                                                  final String[] selectionArgs, final String groupBy, final String having, final String orderBy, final String limit) {

        return Ellie.getInstance().processAndCloseCursor(
                cls, Ellie.getInstance().getDatabase().query(
                        distinct, Ellie.getInstance().getTableName(cls), columns,
                        selection, selectionArgs, groupBy, having, orderBy, limit
                )
        );
    }

    public static <T extends Model> List<T> query(final Class<T> cls, final boolean distinct, final String[] columns, final String selection,
                                                  final String[] selectionArgs, final String groupBy, final String having, final String orderBy, final String limit,
                                                  final CancellationSignal cancellationSignal) {

        return Ellie.getInstance().processAndCloseCursor(
                cls, Ellie.getInstance().getDatabase().query(
                        distinct, Ellie.getInstance().getTableName(cls), columns,
                        selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal
                )
        );
    }

    public static <T extends Model> List<T> queryWithFactory(
            final Class<T> cls, final CursorFactory cursorFactory,
            final boolean distinct, final String[] columns, final String selection, final String[] selectionArgs,
            final String groupBy, final String having, final String orderBy, final String limit) {

        return Ellie.getInstance().processAndCloseCursor(
                cls, Ellie.getInstance().getDatabase().queryWithFactory(
                        cursorFactory, distinct,
                        Ellie.getInstance().getTableName(cls), columns, selection, selectionArgs, groupBy, having, orderBy, limit
                )
        );
    }

    public static <T extends Model> List<T> queryWithFactory(final Class<T> cls, final CursorFactory cursorFactory,
                                                             final boolean distinct, final String[] columns, final String selection, final String[] selectionArgs, final String
            groupBy,
                                                             final String having, final String orderBy, final String limit, final CancellationSignal cancellationSignal) {

        return Ellie.getInstance().processAndCloseCursor(
                cls, Ellie.getInstance().getDatabase().queryWithFactory(
                        cursorFactory, distinct,
                        Ellie.getInstance().getTableName(cls), columns, selection, selectionArgs, groupBy, having, orderBy, limit,
                        cancellationSignal
                )
        );
    }

    public static <T extends Model> List<T> query(final Class<T> cls, final String[] columns, final String selection,
                                                  final String[] selectionArgs, final String groupBy, final String having, final String orderBy) {

        return Ellie.getInstance().processAndCloseCursor(
                cls, Ellie.getInstance().getDatabase().query(
                        Ellie.getInstance().getTableName(cls), columns, selection,
                        selectionArgs, groupBy, having, orderBy
                )
        );
    }

    public static <T extends Model> List<T> query(final Class<T> cls, final String[] columns, final String selection,
                                                  final String[] selectionArgs, final String groupBy, final String having, final String orderBy, final String limit) {

        return Ellie.getInstance().processAndCloseCursor(
                cls, Ellie.getInstance().getDatabase().query(
                        Ellie.getInstance().getTableName(cls), columns, selection,
                        selectionArgs, groupBy, having, orderBy, limit
                )
        );
    }

    public static <T extends Model> List<T> rawQuery(final Class<T> cls, final String sql, final String[] selectionArgs) {
        return Ellie.getInstance().processAndCloseCursor(cls, Ellie.getInstance().getDatabase().rawQuery(sql, selectionArgs));
    }

    public static <T extends Model> List<T> rawQuery(final Class<T> cls, final String sql, final String[] selectionArgs,
                                                     final CancellationSignal cancellationSignal) {

        return Ellie.getInstance().processAndCloseCursor(cls, Ellie.getInstance().getDatabase().rawQuery(sql, selectionArgs, cancellationSignal));
    }

    public static <T extends Model> List<T> rawQueryWithFactory(final Class<T> cls, final CursorFactory cursorFactory, final String sql,
                                                                final String[] selectionArgs, final String editTable) {

        return Ellie.getInstance().processAndCloseCursor(
                cls, Ellie.getInstance().getDatabase().rawQueryWithFactory(
                        cursorFactory, sql,
                        selectionArgs, editTable
                )
        );
    }

    public static <T extends Model> List<T> rawQueryWithFactory(final Class<T> cls, final CursorFactory cursorFactory, final String sql,
                                                                final String[] selectionArgs, final String editTable, final CancellationSignal cancellationSignal) {

        return Ellie.getInstance().processAndCloseCursor(
                cls, Ellie.getInstance().getDatabase().rawQueryWithFactory(
                        cursorFactory, sql,
                        selectionArgs, editTable, cancellationSignal
                )
        );
    }
}
