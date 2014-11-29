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

package com.nhaarman.ellie.internal;

import android.database.sqlite.SQLiteDatabase;

import com.nhaarman.ellie.Ellie;
import com.nhaarman.ellie.Model;
import com.nhaarman.ellie.ModelRepository;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.nhaarman.ellie.internal.Package.PACKAGE_NAME;

/**
 * Used internally to create and store ModelRepository instances.
 */
public interface RepositoryHolder {

    /**
     * The simple name of the generated subclass.
     */
    String IMPL_CLASS_NAME = "RepositoryHolderImpl";

    /**
     * The fully qualified name of the generated subclass.
     */
    String IMPL_CLASS_FQCN = PACKAGE_NAME + "." + IMPL_CLASS_NAME;

    /**
     * The type classes that should be used for the constructor call.
     */
    Class<?>[] CONSTRUCTOR_CLASSES = {Ellie.class, SQLiteDatabase.class, int.class};

    /**
     * Returns the {@link ModelRepository} instance for given type, if it exists.
     *
     * @param cls The type Class to retrieve the {@code ModelRepository} for.
     * @param <T> The type for the {@code ModelRepository}.
     *
     * @return The {@code ModelRepository}, or {@code null} if it doesn't exist.
     */
    @Nullable
    <T extends Model> ModelRepository<T> getModelRepository(Class<? extends Model> cls);

    /**
     * Returns all instantiated {@link ModelRepository}s.
     *
     * @return The list of {@code ModelRepository}s.
     */
    @NotNull
    @SuppressWarnings("rawtypes")
    List<? extends ModelRepository> getModelRepositories();

}
