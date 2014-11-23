package com.nhaarman.ellie.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * <p>
 * An annotation that indicates a method is a setter for a column.
 * </p>
 */
@Target(METHOD)
@Retention(CLASS)
public @interface SetterFor {

    /**
     * Returns the column name.
     *
     * @return The column name.
     */
    public String value();
}
