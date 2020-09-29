package dev.myclinic.vertx.rcpt.resolvedmap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MasterNameMapAnnot {
    public String[] candidates() default "";
}
