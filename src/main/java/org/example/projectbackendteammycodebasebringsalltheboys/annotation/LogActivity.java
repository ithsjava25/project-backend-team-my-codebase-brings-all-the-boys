package org.example.projectbackendteammycodebasebringsalltheboys.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.ActivityAction;
import org.example.projectbackendteammycodebasebringsalltheboys.enums.EntityType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogActivity {
  ActivityAction action();

  int parentIdParamIndex() default 0;

  EntityType entityType();

  boolean orphan() default false;

  /** Index of the method parameter that is the acting User. -1 = auto-detect first User in args. */
  int actorParamIndex() default -1;
}
