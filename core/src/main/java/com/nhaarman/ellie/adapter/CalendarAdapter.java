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

package com.nhaarman.ellie.adapter;

import com.nhaarman.ellie.TypeAdapter;

import java.util.Calendar;

/**
 * <p>
 * Converts Java {@link Calendar} values to SQLite INTEGER values.
 * </p>
 */
public class CalendarAdapter extends TypeAdapter<Calendar, Long> {

    /**
     * {@inheritDoc}
     *
     * @param value {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Long serialize(final Calendar value) {
        if (value != null) {
            return value.getTimeInMillis();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @param value {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Calendar deserialize(final Long value) {
        if (value != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(value);
            return calendar;
        }
        return null;
    }
}