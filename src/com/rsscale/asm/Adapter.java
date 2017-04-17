package com.rsscale.asm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Project: RSPSScale
 *
 * @author Caleb Whiting
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Adapter {

	String value();

	String[] dependencies() default {};

}
