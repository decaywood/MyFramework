package DecayMongo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import DecayMongo.Annotations;

/**
 * 
 * 2014年10月29日
 * @author decaywood
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
    
    
    public String name() default Annotations.DEFAULT_NAME;
    
    
    public boolean lazy() default false;
    
}
