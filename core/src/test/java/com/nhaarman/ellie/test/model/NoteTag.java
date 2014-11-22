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

package com.nhaarman.ellie.test.model;

import com.nhaarman.ellie.Model;
import com.nhaarman.ellie.annotation.Column;
import com.nhaarman.ellie.annotation.ForeignKey;
import com.nhaarman.ellie.annotation.Table;

import static com.nhaarman.ellie.annotation.ForeignKey.ReferentialAction.CASCADE;

@Table("noteTags")
public class NoteTag extends Model {

	public static final String NOTE = "note";
	public static final String TAG = "tag";

	@Column(NOTE)
	@ForeignKey(onDelete = CASCADE)
	public Note note;

	@Column(TAG)
	@ForeignKey(onDelete = CASCADE)
	public Tag tag;
}