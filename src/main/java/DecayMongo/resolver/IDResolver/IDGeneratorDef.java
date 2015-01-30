package DecayMongo.resolver.IDResolver;

import java.lang.reflect.Field;

/**
 * 2014年10月30日
 * @author decaywood
 *
 */
public interface IDGeneratorDef<R> {

    public R generateID(Class<?> clazz, Object value, Field field);
    
}
