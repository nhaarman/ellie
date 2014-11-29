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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.SparseArray;

import com.nhaarman.ellie.Ellie.LogLevel;
import com.nhaarman.ellie.internal.ModelAdapter;

/**
 * <p>
 * Default implementation of a <a href="https://developer.android.com/reference/android/content/ContentProvider.html">
 * ContentProvider</a>. As with any content provider, this class must be
 * <a href="https://developer.android.com/guide/topics/manifest/provider-element.html">declared in the manifest.</a>.
 * When using this content provider, manual initialization is not required as the content provider does this for you.
 * This is not a feature of the provider, but rather a requirement due to the behavior of content providers.
 * </p>
 * Content Uris are built in a restful manner using your package name and tables names. For example:
 * <ul>
 * <li>content://com.example.notes/notes</li>
 * <li>content://com.example.notes/notes/1</li>
 * </ul>
 */
@SuppressWarnings({
        "StringToUpperCaseOrToLowerCaseWithoutLocale",
        "AssignmentToStaticFieldFromInstanceMethod",
        "HardCodedStringLiteral"
        , "rawtypes"
        , "ParameterNameDiffersFromOverriddenParameter"
})
public abstract class EllieProvider extends ContentProvider {

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private static final SparseArray<Class<? extends Model>> TYPE_CODES = new SparseArray<>();

    private static final SparseArray<String> MIME_TYPE_CACHE = new SparseArray<>();

    private static boolean sIsImplemented;

    private static String sAuthority;

    private final Ellie mEllie;

    protected EllieProvider() {
        this(Ellie.getInstance());
    }

    protected EllieProvider(final Ellie ellie) {
        mEllie = ellie;
    }

    /**
     * Returns whether the provider has been implemented.
     *
     * @return Whether the provider has been implemented.
     */
    public static boolean isImplemented() {
        return sIsImplemented;
    }

    /**
     * Create a Uri for a model table.
     *
     * @param type The model type.
     *
     * @return The Uri for the model table.
     */
    public static Uri createUri(final Class<? extends Model> type, final Ellie ellie) {
        return createUri(type, null, ellie);
    }

    /**
     * Create a Uri for a model row.
     *
     * @param type The model type.
     * @param id   The row Id.
     *
     * @return The Uri for the model row.
     */
    public static Uri createUri(final Class<? extends Model> type, final Long id, final Ellie ellie) {
        final StringBuilder uri = new StringBuilder();
        uri.append("content://");
        uri.append(sAuthority);
        uri.append("/");
        uri.append(ellie.getTableName(type).toLowerCase());

        if (id != null) {
            uri.append("/");
            uri.append(id);
        }

        return Uri.parse(uri.toString());
    }

    @Override
    public boolean onCreate() {
        mEllie.init(getContext(), getDatabaseName(), getDatabaseVersion(), getCacheSize(), getLogLevel());
        sAuthority = getAuthority();
        sIsImplemented = true;

        int i = 0;
        for (ModelAdapter modelAdapter : mEllie.getModelAdapters()) {
            final int tableKey = i * 2 + 1;
            final int itemKey = i * 2 + 2;

            // content://<authority>/<table>
            URI_MATCHER.addURI(sAuthority, modelAdapter.getTableName().toLowerCase(), tableKey);
            TYPE_CODES.put(tableKey, modelAdapter.getModelType());

            // content://<authority>/<table>/<id>
            URI_MATCHER.addURI(sAuthority, modelAdapter.getTableName().toLowerCase() + "/#", itemKey);
            TYPE_CODES.put(itemKey, modelAdapter.getModelType());

            i++;
        }

        return true;
    }

    @Override
    public String getType(final Uri uri) {
        final int match = URI_MATCHER.match(uri);

        String cachedMimeType = MIME_TYPE_CACHE.get(match);
        if (cachedMimeType != null) {
            return cachedMimeType;
        }

        final Class<? extends Model> type = getModelType(uri);
        final boolean single = match % 2 == 0;

        StringBuilder mimeType = new StringBuilder();
        mimeType.append("vnd");
        mimeType.append(".");
        mimeType.append(sAuthority);
        mimeType.append(".");
        mimeType.append(single ? "item" : "dir");
        mimeType.append("/");
        mimeType.append("vnd");
        mimeType.append(".");
        mimeType.append(sAuthority);
        mimeType.append(".");
        mimeType.append(mEllie.getTableName(type));

        MIME_TYPE_CACHE.append(match, mimeType.toString());

        return mimeType.toString();
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues contentValues) {
        final Class<? extends Model> type = getModelType(uri);
        final Long id = mEllie.getDatabase().insert(mEllie.getTableName(type), null, contentValues);

        if (id > 0) {
            Uri retUri = createUri(type, id, mEllie);
            getContext().getContentResolver().notifyChange(uri, null);
            return retUri;
        }

        return null;
    }

    @Override
    public int update(final Uri uri, final ContentValues contentValues, final String selection, final String[] selectionArgs) {
        final int count = mEllie.getDatabase().update(
                mEllie.getTableName(getModelType(uri)),
                contentValues,
                selection,
                selectionArgs
        );

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
        final int count = mEllie.getDatabase().delete(
                mEllie.getTableName(getModelType(uri)),
                selection,
                selectionArgs
        );

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
        final Cursor cursor = mEllie.getDatabase().query(
                mEllie.getTableName(getModelType(uri)),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Returns the database name.
     *
     * @return The database name.
     */
    protected abstract String getDatabaseName();

    /**
     * Returns the database version.
     *
     * @return The database version.
     */
    protected abstract int getDatabaseVersion();

    /**
     * Returns the package name as the default Uri authority. Override to provide your own Uri authority.
     *
     * @return The Uri authority.
     */
    protected String getAuthority() {
        return getContext().getPackageName();
    }

    /**
     * Returns the default cache size. Override to provide your own cache size.
     *
     * @return The cache size.
     */
    protected int getCacheSize() {
        return Ellie.DEFAULT_CACHE_SIZE;
    }

    /**
     * Returns the default log level of NONE. Override to provide your own log level.
     *
     * @return The log level.
     */
    protected LogLevel getLogLevel() {
        return LogLevel.NONE;
    }

    private Class<? extends Model> getModelType(final Uri uri) {
        final int code = URI_MATCHER.match(uri);
        if (code != UriMatcher.NO_MATCH) {
            return TYPE_CODES.get(code);
        }
        return null;
    }
}
