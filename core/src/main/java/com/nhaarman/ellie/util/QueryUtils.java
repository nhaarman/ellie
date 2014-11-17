package com.nhaarman.ellie.util;

import android.os.CancellationSignal;

import com.nhaarman.ellie.Model;
import com.nhaarman.ellie.Ellie;

import java.util.List;

import static android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * Utility class for interacting with the database using SQLiteDatabase methods.
 */
public class QueryUtils {
	// TODO: Duplicate methods using provided Ellie instance

	public static void execSQL(String sql) {
		Ellie.getInstance().getDatabase().execSQL(sql);
	}

	public static void execSQL(String sql, String[] selectionArgs) {
		Ellie.getInstance().getDatabase().execSQL(sql, selectionArgs);
	}

	public static <T extends Model> List<T> query(Class<T> cls, boolean distinct, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {

		return Ellie.getInstance().processAndCloseCursor(cls, Ellie.getInstance().getDatabase().query(distinct, Ellie.getInstance().getTableName(cls), columns,
				selection, selectionArgs, groupBy, having, orderBy, limit));
	}

	public static <T extends Model> List<T> query(Class<T> cls, boolean distinct, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy, String limit,
			CancellationSignal cancellationSignal) {

		return Ellie.getInstance().processAndCloseCursor(cls, Ellie.getInstance().getDatabase().query(distinct, Ellie.getInstance().getTableName(cls), columns,
				selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal));
	}

	public static <T extends Model> List<T> queryWithFactory(Class<T> cls, CursorFactory cursorFactory,
			boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy, String limit) {

		return Ellie.getInstance().processAndCloseCursor(cls, Ellie.getInstance().getDatabase().queryWithFactory(cursorFactory, distinct,
				Ellie.getInstance().getTableName(cls), columns, selection, selectionArgs, groupBy, having, orderBy, limit));
	}

	public static <T extends Model> List<T> queryWithFactory(Class<T> cls, CursorFactory cursorFactory,
			boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy, String limit, CancellationSignal cancellationSignal) {

		return Ellie.getInstance().processAndCloseCursor(cls, Ellie.getInstance().getDatabase().queryWithFactory(cursorFactory, distinct,
				Ellie.getInstance().getTableName(cls), columns, selection, selectionArgs, groupBy, having, orderBy, limit,
				cancellationSignal));
	}

	public static <T extends Model> List<T> query(Class<T> cls, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy) {

		return Ellie.getInstance().processAndCloseCursor(cls, Ellie.getInstance().getDatabase().query(
																 Ellie.getInstance().getTableName(cls), columns, selection,
				selectionArgs, groupBy, having, orderBy));
	}

	public static <T extends Model> List<T> query(Class<T> cls, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {

		return Ellie.getInstance().processAndCloseCursor(cls, Ellie.getInstance().getDatabase().query(
																 Ellie.getInstance().getTableName(cls), columns, selection,
				selectionArgs, groupBy, having, orderBy, limit));
	}

	public static <T extends Model> List<T> rawQuery(Class<T> cls, String sql, String[] selectionArgs) {
		return Ellie.getInstance().processAndCloseCursor(cls, Ellie.getInstance().getDatabase().rawQuery(sql, selectionArgs));
	}

	public static <T extends Model> List<T> rawQuery(Class<T> cls, String sql, String[] selectionArgs,
			CancellationSignal cancellationSignal) {

		return Ellie.getInstance().processAndCloseCursor(cls, Ellie.getInstance().getDatabase().rawQuery(sql, selectionArgs, cancellationSignal));
	}

	public static <T extends Model> List<T> rawQueryWithFactory(Class<T> cls, CursorFactory cursorFactory, String sql,
			String[] selectionArgs, String editTable) {

		return Ellie.getInstance().processAndCloseCursor(cls, Ellie.getInstance().getDatabase().rawQueryWithFactory(cursorFactory, sql,
				selectionArgs, editTable));
	}

	public static <T extends Model> List<T> rawQueryWithFactory(Class<T> cls, CursorFactory cursorFactory, String sql,
			String[] selectionArgs, String editTable, CancellationSignal cancellationSignal) {

		return Ellie.getInstance().processAndCloseCursor(cls, Ellie.getInstance().getDatabase().rawQueryWithFactory(cursorFactory, sql,
				selectionArgs, editTable, cancellationSignal));
	}
}
