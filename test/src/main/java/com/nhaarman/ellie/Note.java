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

import com.nhaarman.ellie.annotation.Column;
import com.nhaarman.ellie.annotation.GetterFor;
import com.nhaarman.ellie.annotation.NotNull;
import com.nhaarman.ellie.annotation.SetterFor;
import com.nhaarman.ellie.annotation.Table;

import java.util.Date;

@Table("notes")
public class Note extends Model {

    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String DATE = "date";

    @Column(TITLE)
    private String title;

    @Column(BODY)
    @NotNull
    private String body;

    @Column(DATE)
    private Date date;

    @GetterFor(TITLE)
    public String getTitle() {
        return title;
    }

    @SetterFor(TITLE)
    public void setTitle(final String title) {
        this.title = title;
    }

    @GetterFor(BODY)
    public String getBody() {
        return body;
    }

    @SetterFor(BODY)
    public void setBody(final String body) {
        this.body = body;
    }

    @GetterFor(DATE)
    public Date getDate() {
        return date;
    }

    @SetterFor(DATE)
    public void setDate(final Date date) {
        this.date = date;
    }
}