package org.decaywood.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.decaywood.constant.Annotations;

/**
 * 
 * 2014年10月28日
 * @author decaywood
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GroupEmbed {
    
    
    public String name() default Annotations.DEFAULT_NAME;
    
    
    public boolean lazy() default false;
    
    
    public Class<?> groupClass() default Annotations.class;
    
    
}
