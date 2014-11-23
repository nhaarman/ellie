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

package com.nhaarman.ellie.query;

import android.database.Cursor;

import com.nhaarman.ellie.Ellie;
import com.nhaarman.ellie.Model;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

import static rx.Observable.OnSubscribe;

@SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
public abstract class ResultQueryBase extends ExecutableQueryBase implements ResultQuery {

    private Ellie mEllie;

    protected ResultQueryBase(final Query parent, final Class<? extends Model> table) {
        super(parent, table);
        mEllie = parent.getEllie();
    }

    public static <T extends Model> List<T> rawQuery(final Ellie ellie, final Class<T> cls, final String sql, final String[] selectionArgs) {
        return ellie.processAndCloseCursor(cls, ellie.getDatabase().rawQuery(sql, selectionArgs));
    }

    public ResultQueryBase with(final Ellie ellie) {
        mEllie = ellie;
        return this;
    }

    @Override
    public <T extends Model> List<T> fetch() {
        return (List<T>) rawQuery(mEllie, mTable, getSql(), getArgs());
    }

    @Override
    public <T extends Model> T fetchSingle() {
        List<T> results = (List<T>) rawQuery(mEllie, mTable, getSql(), getArgs());
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public <T> T fetchValue(final Class<T> type) {
        final Cursor cursor = mEllie.getDatabase().rawQuery(getSql(), getArgs());
        if (!cursor.moveToFirst()) {
            return null;
        }

        if (type.equals(Byte[].class) || type.equals(byte[].class)) {
            return (T) cursor.getBlob(0);
        }
        if (type.equals(double.class) || type.equals(Double.class)) {
            return (T) Double.valueOf(cursor.getDouble(0));
        }
        if (type.equals(float.class) || type.equals(Float.class)) {
            return (T) Float.valueOf(cursor.getFloat(0));
        }
        if (type.equals(int.class) || type.equals(Integer.class)) {
            return (T) Integer.valueOf(cursor.getInt(0));
        }
        if (type.equals(long.class) || type.equals(Long.class)) {
            return (T) Long.valueOf(cursor.getLong(0));
        }
        if (type.equals(short.class) || type.equals(Short.class)) {
            return (T) Short.valueOf(cursor.getShort(0));
        }
        if (type.equals(String.class)) {
            return (T) cursor.getString(0);
        }

        return null;
    }

    @Override
    public <T extends Model> Observable<List<T>> observable() {
        return Observable.create(new ListOnSubscribe<T>());
    }

    @Override
    public <T extends Model> Observable<T> observableSingle() {
        return Observable.create(new ModelOnSubscribe<T>());
    }

    @Override
    public <T> Observable<T> observableValue(final Class<T> type) {
        return Observable.create(new ValueOnSubscribe<>(type));
    }

    private class ListOnSubscribe<T extends Model> implements OnSubscribe<List<T>> {

        @Override
        public void call(final Subscriber<? super List<T>> subscriber) {
            final List<T> result = fetch();
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        }
    }

    private class ValueOnSubscribe<T> implements OnSubscribe<T> {

        private final Class<T> mType;

        private ValueOnSubscribe(final Class<T> type) {
            mType = type;
        }

        @Override
        public void call(final Subscriber<? super T> subscriber) {
            final T result = fetchValue(mType);
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        }
    }

    private class ModelOnSubscribe<T extends Model> implements OnSubscribe<T> {

        @Override
        public void call(final Subscriber<? super T> subscriber) {
            final T result = fetchSingle();
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        }
    }
}
