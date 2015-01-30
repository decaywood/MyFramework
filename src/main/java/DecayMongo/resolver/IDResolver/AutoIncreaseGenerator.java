package DecayMongo.resolver.IDResolver;

import java.lang.reflect.Field;

import DecayMongo.annotations.ID;
import DecayMongo.cache.DaosCache;
import DecayMongo.dao.ToolDao;



/**
 * 2014年10月31日
 * @author decaywood
 *
 */
public class AutoIncreaseGenerator implements IDGeneratorDef<Long> {

    @Override
    public Long generateID(Class<?> clazz, Object value, Field field) {
        if(value != null) return Long.parseLong(value.toString());
        ToolDao<?> dao = DaosCache.getInstance().get(clazz);
        long max = dao.getMaxId();
        ID idAnnotation = field.getAnnotation(ID.class);
        return max == 0 ? idAnnotation.start() : max + 1L;
    }

}
