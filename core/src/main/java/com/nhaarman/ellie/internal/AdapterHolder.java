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

package com.nhaarman.ellie.internal;

import com.nhaarman.ellie.Migration;
import com.nhaarman.ellie.Model;
import com.nhaarman.ellie.TypeAdapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.nhaarman.ellie.internal.Package.PACKAGE_NAME;

/**
 * Used internally to instantiate the {@link ModelAdapter}s, {@link TypeAdapter}s and {@link Migration}s.
 * <p/>
 * The compiler generates the one and only subclass of this interface; users should normally not subclass this.
 * The class should be instantiated by reflection, where the fully qualified name is {@link #IMPL_CLASS_FQCN},
 * using {@link #CONSTRUCTOR_CLASSES} for the constructor call.
 */
public interface AdapterHolder {

    /**
     * The simple name of the generated subclass.
     */
    String IMPL_CLASS_NAME = "AdapterHolderImpl";

    /**
     * The fully qualified name of the generated subclass.
     */
    String IMPL_CLASS_FQCN = PACKAGE_NAME + "." + IMPL_CLASS_NAME;

    /**
     * The type classes that should be used for the constructor call.
     */
    Class<?>[] CONSTRUCTOR_CLASSES = {};

    /**
     * Returns a sorted, ascending list of {@link Migration}s.
     *
     * @return The list of {@code Migration}s.
     */
    @NotNull
    List<? extends Migration> getMigrations();

    /**
     * Returns the {@link ModelAdapter} instance for given type, if it exists.
     *
     * @param cls The type Class to retrieve the {@code ModelAdapter} for.
     * @param <T> The type for the {@code ModelAdapter} if it exists, {@code null} otherwise.
     *
     * @return The {@code ModelAdapter}.
     */
    @Nullable
    <T extends Model> ModelAdapter<T> getModelAdapter(Class<? extends Model> cls);

    /**
     * Returns all instantiated {@link ModelAdapter}s.
     *
     * @return The list of {@code ModelAdapter}s.
     */
    @SuppressWarnings("rawtypes")
    @NotNull
    List<? extends ModelAdapter> getModelAdapters();

    /**
     * Returns a {@link TypeAdapter} for given type Class.
     *
     * @param cls The type Class to retrieve the {@code TypeAdapter} for.
     * @param <D> Deserialized type, i.e. the Java type.
     * @param <S> Serialized type, i.e. the SQLite type.
     *
     * @return The {@code TypeAdapter}.
     */
    @Nullable
    <D, S> TypeAdapter<D, S> getTypeAdapter(Class<D> cls);
}