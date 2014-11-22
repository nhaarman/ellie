package com.nhaarman.ellie.internal;

import android.database.Cursor;

import com.nhaarman.ellie.Model;

public interface ModelRepository<T extends Model> {

    /* Direct database manipulation */

    void load(T entity, Cursor cursor);

    /* Model operations */

    T find(long id);

    Long create(T entity);

    Long update(T entity);

    Long createOrUpdate(T entity);

    void delete(T entity);

    /* Cache operations */

    void putEntity(T entity);

    T getEntity(long id);

    void removeEntity(T entity);

    T getOrFindEntity(long id);
}
