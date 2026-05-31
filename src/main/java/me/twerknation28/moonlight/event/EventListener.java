package me.twerknation28.moonlight.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface EventListener {
    int priority() default 0;
    
    boolean receiveCanceled() default true;
}
