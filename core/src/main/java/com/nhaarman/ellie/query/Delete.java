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

public final class Delete extends QueryBase {

    private Ellie mEllie;

    public Delete() {
        super(null, null);
        mEllie = Ellie.getInstance();
    }

    public Delete with(final Ellie ellie) {
        mEllie = ellie;
        return this;
    }

    public From from(final Class<? extends Model> table) {
        return new From(this, table);
    }

    @Override
    public String getPartSql() {
        return "DELETE";
    }

    private Ellie getEllie() {
        return mEllie;
    }

    public static final class From extends ExecutableQueryBase {

        private From(final Delete parent, final Class<? extends Model> table) {
            super(parent, table);
        }

        public Where where(final String where) {
            return where(where, (Object[]) null);
        }

        public Where where(final String where, final Object... args) {
            return new Where(this, mTable, where, args);
        }

        @Override
        public String getPartSql() {
            return "FROM " + ((Delete) mParent).getEllie().getTableName(mTable);
        }
    }

    public static final class Where extends ExecutableQueryBase {

        private final String mWhere;
        private final Object[] mWhereArgs;

        public Where(final Query parent, final Class<? extends Model> table, final String where, final Object[] args) {
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
