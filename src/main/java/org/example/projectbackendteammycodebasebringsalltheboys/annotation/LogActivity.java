package org.example.projectbackendteammycodebasebringsalltheboys.annotation;

import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogActivity {
    ActivityAction action();
    int caseIdParamIndex() default 0;
    EntityType entity();
    boolean noCase() default false;
}
