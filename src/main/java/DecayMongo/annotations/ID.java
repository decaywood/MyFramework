package DecayMongo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 2014年10月29日
 * @author decaywood
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ID {
    
    
    public IDSequence type() default IDSequence.AUTO_GENERATE;
    
    
    public long start() default 1L;
    
    
}
