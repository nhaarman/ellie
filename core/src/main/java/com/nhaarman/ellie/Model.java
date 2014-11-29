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
import com.nhaarman.ellie.annotation.SetterFor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A Model represents a single table record and uses annotations to define the table's schema.
 * The Model contains methods for interacting with the database directly.
 */
public abstract class Model {

    /**
     * The name of the id column.
     */
    public static final String COLUMN_ID = BaseColumns._ID;

    /**
     * The {@link ModelRepository} that is used to query the database.
     */
    @SuppressWarnings("rawtypes")
    private final ModelRepository mRepository;

    @Nullable
    @Column(COLUMN_ID)
    @PrimaryKey
    @AutoIncrement
    protected Long mId;

    /**
     * Creates a new instance of the {@code Model} using the default {@link Ellie} instance
     * to retrieve the {@link ModelRepository} to use.
     */
    protected Model() {
        mRepository = Ellie.getInstance().getModelRepository(getClass());
    }

    /**
     * Creates a new instance of the {@code Model} using given {@link ModelRepository}.
     *
     * @param repository The {@link ModelRepository} to use for querying the database.
     */
    protected Model(final ModelRepository<? extends Model> repository) {
        mRepository = repository;
    }

    /**
     * Returns the id of this {@code Model} instance.
     *
     * @return The id, or {@code null} if it doesn't exist in the database.
     */
    @Nullable
    @GetterFor(COLUMN_ID)
    public Long getId() {
        return mId;
    }

    /**
     * Sets given id to this {@code Model} instance.
     *
     * @param id The id to set.
     */
    @SetterFor(COLUMN_ID)
    public void setId(@Nullable final Long id) {
        mId = id;
    }

    /**
     * Load this objects values from a cursor.
     *
     * @param cursor The Cursor to load values from.
     */
    public final void load(final Cursor cursor) {
        mRepository.load(this, cursor);
        mRepository.putEntity(this);
    }

    /**
     * Persist the record to the database.
     * Inserts the record if it does not exists and updates the record if it does exists.
     *
     * @return The record id, or {@code -1} if an error occurred.
     */
    @NotNull
    public final Long save() {
        Long result;

        if (mId == null) {
            result = mRepository.create(this);
        } else {
            result = mRepository.update(this);
        }

        if (result != -1) {
            mId = result;
            mRepository.putEntity(this);
            notifyChange();
        }

        return result;
    }

    /**
     * Deletes the record from the database.
     */
    public final void delete() {
        mRepository.delete(this);
        mRepository.removeEntity(this);
        notifyChange();
        mId = null;
    }

    /**
     * Notifies observers that this record has changed.
     */
    private void notifyChange() {
//        if (EllieProvider.isImplemented()) {
//            mEllie.getContext().getContentResolver().notifyChange(EllieProvider.createUri(getClass(), id), null);
//        }
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