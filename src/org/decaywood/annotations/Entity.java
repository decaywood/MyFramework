package org.decaywood.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;

import org.decaywood.Annotations;

/**
 * 
 * 2014年10月28日
 * @author decaywood
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
    
    
    public String name() default Annotations.DEFAULT_NAME;
    
    
    public Split split() default Split.NONE;
    
    
    public String splitSuffix() default Annotations.SUFFIX;
    
    
    public boolean capped() default false;
    
    
    public long capSize() default Annotations.DEFAULT_CAP_SIZE;
    
    
    public long capMax() default Annotations.DEFAULT_CAP_MAX;
    
    
}
