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

package com.nhaarman.ellie.test.model;


import com.nhaarman.ellie.Model;
import com.nhaarman.ellie.annotation.Column;
import com.nhaarman.ellie.annotation.NotNull;
import com.nhaarman.ellie.annotation.Table;

@Table("tags")
public class Tag extends Model {

    public static final String NAME = "name";

    @Column(NAME)
    @NotNull
    public String name;
}