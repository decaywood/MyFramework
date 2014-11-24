package org.decaywood.resolver.IDResolver;

import java.lang.reflect.Field;
import java.util.logging.Logger;

/**
 * 2014年10月31日
 * @author decaywood
 *
 */
public class UserDefineGenerator implements IDGeneratorDef<String> {

    private static final Logger log = Logger.getLogger(UserDefineGenerator.class.getName());
    
    @Override
    public String generateID(Class<?> clazz, Object value, Field field) {
        if(value == null){ 
            log.info("ID can not be null");
            return null;
        }
        else 
            return value.toString(); 
    }

}
