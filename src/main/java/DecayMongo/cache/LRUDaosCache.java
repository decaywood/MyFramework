package DecayMongo.cache;

import org.decaywood.dao.ToolDao;
import org.decaywood.utils.LRUCache;

/**
 * 2014年12月3日
 * @author decaywood
 *
 */
public class LRUDaosCache<T> implements DaosCache.DaosCacheDefinition<T>{
    
    
    private final LRUCache<String, ToolDao<T>> cache;
    
    public LRUDaosCache(int cacheSize){
        cache = new LRUCache<String, ToolDao<T>>(cacheSize);
    }

    /**
     * 2014年12月3日
     * @author decaywood
     *
     */ 
    @Override
    public ToolDao<T> get(Class<T> clazz) {
        String name = clazz.getName();
        ToolDao<T> toolDao = cache.get(name);
        if(toolDao != null)
            return toolDao;
        ToolDao<T> dao = new ToolDao<T>(clazz);
        cache.put(name, dao);
        return dao; 
    }


}
