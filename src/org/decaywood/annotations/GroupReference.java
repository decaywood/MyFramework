package org.decaywood.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.decaywood.Annotations;

/**
 * 
 * 2014年10月29日
 * @author decaywood
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GroupReference {
    
    
    
    public String name() default Annotations.DEFAULT_NAME;
    
    
    
    public int cascade() default Annotations.CASCADE_DEFAULT;
    
    
    
    public String sort() default Annotations.DEFAULT_SORT;
    
    
    
    public boolean reduced() default false;
    
    
    
    public Class<?> implementClass() default Annotations.class;
    
    
    
    public Class<?> groupClass() default Annotations.class;
    
    
}
