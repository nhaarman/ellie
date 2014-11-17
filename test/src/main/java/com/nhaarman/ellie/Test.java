package com.nhaarman.ellie;

import com.nhaarman.ellie.annotation.Column;
import com.nhaarman.ellie.annotation.Table;

/**
 * Created by Niek on 18-11-2014.
 */
@Table("test")
public class Test extends Model {

	@Column("a")
	public int a;

//	@Column("Test")
//	public String test;

}
