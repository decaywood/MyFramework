package DecayMongo.cache;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.decaywood.dao.ToolDao;

/**
 * 2014年10月31日
 * @author decaywood
 *
 */
public class ConcurrentDaosCache<T> implements DaosCache.DaosCacheDefinition<T>{

    
    private final ConcurrentMap<String, SoftReference<ToolDao<T>>> cache;
    
    public ConcurrentDaosCache(){
        cache = new ConcurrentHashMap<String, SoftReference<ToolDao<T>>>();
    }

    /**
     * 2014年12月3日
     * @author decaywood
     *
     */ 
    @Override
    public ToolDao<T> get(Class<T> clazz) {
        String name = clazz.getName();
        SoftReference<ToolDao<T>> softReference = cache.get(name);
        if(softReference != null)
            return (ToolDao<T>)softReference.get();
        ToolDao<T> dao = new ToolDao<>(clazz);
        softReference = new SoftReference<ToolDao<T>>(dao);
        SoftReference<ToolDao<T>> temp = cache.putIfAbsent(name, softReference);
        return (ToolDao<T>) (temp != null ? temp.get() : softReference.get()); 
    }

    
    
    

    
}
