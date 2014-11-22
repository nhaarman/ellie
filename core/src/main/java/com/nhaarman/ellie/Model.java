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

import android.database.Cursor;
import android.provider.BaseColumns;

import com.nhaarman.ellie.annotation.AutoIncrement;
import com.nhaarman.ellie.annotation.Column;
import com.nhaarman.ellie.annotation.PrimaryKey;
import com.nhaarman.ellie.internal.ModelRepository;

/**
 * A Model represents a single table record and uses annotations to define the table's schema. The Model contains
 * methods for interacting with the database directly.
 */
public abstract class Model {

    public static final String COLUMN_ID = BaseColumns._ID;

    @SuppressWarnings("rawtypes")
    private final ModelRepository mRepository;

    @Column(COLUMN_ID)
    @PrimaryKey
    @AutoIncrement
    public Long id;

    protected Model() {
        mRepository = Ellie.getInstance().getModelRepository(getClass());
    }

    protected Model(final ModelRepository<? extends Model> repository) {
        mRepository = repository;
    }

    /**
     * <p>
     * Load this objects values from a cursor.
     * </p>
     *
     * @param cursor
     */
    public final void load(final Cursor cursor) {
        mRepository.load(this, cursor);
        mRepository.putEntity(this);
    }

    /**
     * <p>
     * Persist the record to the database. Inserts the record if it does not exists and updates the record if it
     * does exists.
     * </p>
     *
     * @return The record id.
     */
    public final Long save() {
        if (id == null) {
            id = mRepository.create(this);
        } else {
            mRepository.update(this);
        }
        mRepository.putEntity(this);
        notifyChange();
        return id;
    }

    /**
     * <p>
     * Delete the record from the database.
     * </p>
     */
    public final void delete() {
        mRepository.delete(this);
        mRepository.removeEntity(this);
        notifyChange();
        id = null;
    }

    /**
     * <p>
     * Notify observers that this record has changed.
     * </p>
     */
    private void notifyChange() {
//		if (EllieProvider.isImplemented()) {
//			mEllie.getContext().getContentResolver().notifyChange(EllieProvider.createUri(getClass(), id), null);
//		}
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Model && id != null) {
            final Model other = (Model) obj;
            return getClass().equals(other.getClass()) && id.equals(other.id);
        }
        return this == obj;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + getClass().getName().hashCode();
        hash = hash * 31 + (id != null ? id.intValue() : super.hashCode());
        return hash;
    }
}