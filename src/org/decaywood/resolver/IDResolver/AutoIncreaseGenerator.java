package org.decaywood.resolver.IDResolver;

import java.lang.reflect.Field;

import org.decaywood.annotations.ID;
import org.decaywood.cache.DaosPool;
import org.decaywood.dao.ToolDao;



/**
 * 2014年10月31日
 * @author decaywood
 *
 */
public class AutoIncreaseGenerator implements IDGeneratorDef<Long> {

    @Override
    public Long generateID(Class<?> clazz, Object value, Field field) {
        if(value != null) return Long.parseLong(value.toString());
        ToolDao<?> dao = DaosPool.getInstance().get(clazz);
        long max = dao.getMaxId();
        ID idAnnotation = field.getAnnotation(ID.class);
        return max == 0 ? idAnnotation.start() : max + 1L;
    }

}
