package org.decaywood.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.decaywood.Annotations;

/**
 * 
 * 2014年10月28日
 * 
 * @author decaywood
 * 
 *         Ø RetentionPolicy.SOURCE 注解将被编译器丢弃。
 * 
 *         Ø RetentionPolicy.CLASS 注解在class文件中可用，但会被VM丢弃。
 * 
 *         Ø RetentionPolicy.RUNTIME VM将在运行期也保留注释，因此可以通过反射机制读取注解的信息。
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Embed {
    
    
    
    public String name() default Annotations.DEFAULT_NAME;
    
    
    public boolean lazy() default false;
    
    
}
