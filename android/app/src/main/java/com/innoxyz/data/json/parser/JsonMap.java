package com.innoxyz.data.json.parser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-6
 * Time: 下午4:29
 * To change this template use File | Settings | File Templates.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonMap {
    String name() default "";
    JSONType type() default JSONType.UNASSIGNED;
    boolean required() default true;
}
