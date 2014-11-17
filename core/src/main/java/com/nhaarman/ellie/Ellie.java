/*
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

package com.nhaarman.ellie;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Build;
import android.provider.BaseColumns;
import android.util.Log;

import com.nhaarman.ellie.internal.AdapterHolder;
import com.nhaarman.ellie.internal.ModelAdapter;
import com.nhaarman.ellie.util.LruCache;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public final class Ellie {
	public final int DEFAULT_CACHE_SIZE = 1024;

	private static final String TAG = "Ellie";

	private  Context mContext;
	private AdapterHolder mAdapterHolder;
	private  DatabaseHelper mDatabaseHelper;
	private  SQLiteDatabase mSQLiteDatabase;
	private  LruCache<String, Model> mStringModelLruCache;
	private  LogLevel mLogLevel = LogLevel.NONE;
	private  boolean mInitialized = false;

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

		public boolean log(LogLevel logLevel) {
			return this.ordinal() >= logLevel.ordinal();
		}
	}

	public Ellie() {
	}

	public static synchronized Ellie getInstance() {
		if(sInstance == null){
			sInstance = new Ellie();
		}

		return sInstance;
	}

	// Public methods

	/**
	 * Initialize the database. Must be called before interacting with the database.
	 *
	 * @param context Context
	 * @param name    The database name.
	 * @param version The database version.
	 */
	public void init(Context context, String name, int version) {
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
	public void init(Context context, String name, int version, int cacheSize) {
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
	public void init(Context context, String name, int version, LogLevel logLevel) {
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
	public void init(Context context, String name, int version, int cacheSize, LogLevel logLevel) {
		mLogLevel = logLevel;

		if (mInitialized) {
			if (mLogLevel.log(LogLevel.BASIC)) {
				Log.d(TAG, "Already initialized.");
			}
			return;
		}

		try {
			Class adapterClass = Class.forName(AdapterHolder.IMPL_CLASS_FQCN);
			mAdapterHolder = (AdapterHolder) adapterClass.newInstance();
		} catch (Exception e) {
			if (mLogLevel.log(LogLevel.BASIC)) {
				Log.e(TAG, "Failed to initialize.", e);
			}
		}

		mContext = context.getApplicationContext();
		mDatabaseHelper = new DatabaseHelper(mContext, name, version);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
		mStringModelLruCache = new LruCache<String, Model>(cacheSize);

		mInitialized = true;
	}

	public Context getContext() {
		return mContext;
	}

	public SQLiteDatabase getDatabase() {
		return mSQLiteDatabase;
	}

	public <T extends Model> String getTableName(Class<T> cls) {
		return mAdapterHolder.getModelAdapter(cls).getTableName();
	}

	// Convenience methods

	/**
	 * Iterate over a cursor and load entities.
	 *
	 * @param cls    The model class.
	 * @param cursor The result cursor.
	 * @return The list of entities.
	 */
	public <T extends Model> List<T> processCursor(Class<T> cls, Cursor cursor) {
		final List<T> entities = new ArrayList<T>();
		try {
			Constructor<T> entityConstructor = cls.getConstructor();
			if (cursor.moveToFirst()) {
				do {
					T entity = getEntity(cls, cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
					if (entity == null) {
						entity = entityConstructor.newInstance();
					}

					entity.load(cursor);
					entities.add(entity);
				}
				while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e(TAG, "Failed to process cursor.", e);
		}

		return entities;
	}

	/**
	 * Iterate over a cursor and load entities. Closes the cursor when finished.
	 *
	 * @param cls    The model class.
	 * @param cursor The result cursor.
	 * @return The list of entities.
	 */
	public <T extends Model> List<T> processAndCloseCursor(Class<T> cls, Cursor cursor) {
		List<T> entities = processCursor(cls, cursor);
		cursor.close();
		return entities;
	}

	// Finder methods

	<D, S> TypeAdapter<D, S> getTypeAdapter(Class<D> cls) {
		return (TypeAdapter<D, S>) mAdapterHolder.getTypeAdapter(cls);
	}

	List<? extends ModelAdapter> getModelAdapters() {
		return mAdapterHolder.getModelAdapters();
	}

	// Cache methods

	synchronized <T extends Model> void putEntity(T entity) {
		if (entity.id != null) {
			mStringModelLruCache.put(getEntityIdentifier(entity.getClass(), entity.id), entity);
		}
	}

	synchronized <T extends Model> T getEntity(Class<T> cls, long id) {
		return (T) mStringModelLruCache.get(getEntityIdentifier(cls, id));
	}

	synchronized <T extends Model> void removeEntity(T entity) {
		mStringModelLruCache.remove(getEntityIdentifier(entity.getClass(), entity.id));
	}

	synchronized <T extends Model> T getOrFindEntity(Class<T> cls, long id) {
		T entity = getEntity(cls, id);
		if (entity == null) {
			entity = Model.find(cls, id);
		}
		return entity;
	}

	// Model adapter methods

	synchronized <T extends Model> void load(T entity, Cursor cursor) {
		mAdapterHolder.getModelAdapter(entity.getClass()).load(entity, cursor);
	}

	synchronized <T extends Model> Long save(T entity) {
		return mAdapterHolder.getModelAdapter(entity.getClass()).save(entity, mSQLiteDatabase);
	}

	synchronized <T extends Model> void delete(T entity) {
		mAdapterHolder.getModelAdapter(entity.getClass()).delete(entity, mSQLiteDatabase);
	}

	// Private methods

	private static String getEntityIdentifier(Class<? extends Model> cls, long id) {
		return cls.getName() + "@" + id;
	}

	// Private classes

	private final class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context, String name, int version) {
			super(context, name, mLogLevel.log(LogLevel.FULL) ? new LoggingCursorAdapter() : null, version);
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			executePragmas(db);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			executePragmas(db);
			executeCreate(db);
			executeMigrations(db, -1, db.getVersion());
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			executePragmas(db);
			executeCreate(db);
			executeMigrations(db, oldVersion, newVersion);
		}

		private void executePragmas(SQLiteDatabase db) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				db.execSQL("PRAGMA foreign_keys=ON;");
			}
		}

		private void executeCreate(SQLiteDatabase db) {
			final List<String> tableDefinitions = new ArrayList<String>();
			for (ModelAdapter modelAdapter : mAdapterHolder.getModelAdapters()) {
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

		private boolean executeMigrations(SQLiteDatabase db, int oldVersion, int newVersion) {
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

	private static final class LoggingCursorAdapter implements CursorFactory {
		@Override
		public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver driver, String editTable, SQLiteQuery query) {
			Log.v(TAG, query.toString());
			return new SQLiteCursor(db, driver, editTable, query);
		}
	}
}