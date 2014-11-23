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

import android.database.Cursor;
import android.provider.BaseColumns;

import com.nhaarman.ellie.annotation.AutoIncrement;
import com.nhaarman.ellie.annotation.Column;
import com.nhaarman.ellie.annotation.GetterFor;
import com.nhaarman.ellie.annotation.PrimaryKey;

/**
 * A Model represents a single table record and uses annotations to define the table's schema. The Model contains
 * methods for interacting with the database directly.
 */
public abstract class Model {

    public static final String COLUMN_ID = BaseColumns._ID;

    @SuppressWarnings("rawtypes")
    private final ModelRepository mRepository;

    @Column(COLUMN_ID) @PrimaryKey @AutoIncrement
    protected Long mId;

    protected Model() {
        mRepository = Ellie.getInstance().getModelRepository(getClass());
    }

    protected Model(final ModelRepository<? extends Model> repository) {
        mRepository = repository;
    }

    @GetterFor(COLUMN_ID)
    public Long getId() {
        return mId;
    }

    public void setId(final Long id) {
        mId = id;
    }

    /**
     * <p>
     * Load this objects values from a cursor.
     * </p>
     *
     * @param cursor The Cursor to load values from.
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
        if (mId == null) {
            mId = mRepository.create(this);
        } else {
            mRepository.update(this);
        }
        mRepository.putEntity(this);
        notifyChange();
        return mId;
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
        mId = null;
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
        if (obj instanceof Model && mId != null) {
            final Model other = (Model) obj;
            return getClass().equals(other.getClass()) && mId.equals(other.getId());
        }
        return this == obj;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + getClass().getName().hashCode();
        hash = hash * 31 + (mId != null ? mId.intValue() : super.hashCode());
        return hash;
    }
}