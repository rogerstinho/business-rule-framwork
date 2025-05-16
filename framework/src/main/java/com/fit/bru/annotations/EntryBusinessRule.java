package com.fit.bru.annotations;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EntryBusinessRule {
    String name();
    String description() default "";
}
