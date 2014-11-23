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

package com.nhaarman.ellie.query;

import com.nhaarman.ellie.Ellie;
import com.nhaarman.ellie.Model;

public final class Update extends QueryBase {

    private Ellie mEllie;

    public Update(final Class<? extends Model> table) {
        super(null, table);
        mEllie = Ellie.getInstance();
    }

    public Update with(final Ellie ellie) {
        mEllie = ellie;
        return this;
    }

    public Set set(final String set) {
        return set(set, (Object[]) null);
    }

    public Set set(final String set, final Object... args) {
        return new Set(this, mTable, set, args);
    }

    @Override
    public String getPartSql() {
        return "UPDATE " + mEllie.getTableName(mTable);
    }

    public static final class Set extends ExecutableQueryBase {

        private final String mSet;
        private final Object[] mSetArgs;

        private Set(final Query parent, final Class<? extends Model> table, final String set, final Object... args) {
            super(parent, table);
            mSet = set;
            mSetArgs = args;
        }

        public Where where(final String where) {
            return where(where, (Object[]) null);
        }

        public Where where(final String where, final Object... args) {
            return new Where(this, mTable, where, args);
        }

        @Override
        public String getPartSql() {
            return "SET " + mSet;
        }

        @Override
        public String[] getPartArgs() {
            return toStringArray(mSetArgs);
        }
    }

    public static final class Where extends ExecutableQueryBase {

        private final String mWhere;
        private final Object[] mWhereArgs;

        private Where(final Query parent, final Class<? extends Model> table, final String where, final Object[] args) {
            super(parent, table);
            mWhere = where;
            mWhereArgs = args;
        }

        @Override
        public String getPartSql() {
            return "WHERE " + mWhere;
        }

        @Override
        public String[] getPartArgs() {
            return toStringArray(mWhereArgs);
        }
    }
}
