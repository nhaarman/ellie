/*
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface to perform database operations for a Model.
 *
 * @param <T> The Model instance to perform database operations for.
 */
public interface ModelRepository<T extends Model> {

    /* Direct database manipulation */

    void load(T entity, Cursor cursor);

    /* Model operations */

    @Nullable
    T find(long id);

    @NotNull
    Long create(T entity);

    @NotNull
    Long update(T entity);

    @NotNull
    Long createOrUpdate(T entity);

    void delete(T entity);

    /* Cache operations */

    void putEntity(T entity);

    T getEntity(long id);

    void removeEntity(T entity);

    T getOrFindEntity(long id);
}
