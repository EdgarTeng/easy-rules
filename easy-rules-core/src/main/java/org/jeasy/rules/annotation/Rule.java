package org.jeasy.rules.annotation;

import java.lang.annotation.*;

import static org.jeasy.rules.api.Rule.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Rule {
    String name() default DEFAULT_NAME;

    String description() default DEFAULT_DESCRIPTION;

    int priority() default DEFAULT_PRIORITY;
}
