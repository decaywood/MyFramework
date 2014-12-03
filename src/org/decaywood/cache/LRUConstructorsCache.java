package org.decaywood.cache;

import java.lang.reflect.Constructor;
import java.util.logging.Logger;

import org.decaywood.utils.LRUCache;

/**
 * 2014年12月3日
 * @author decaywood
 *
 */
public class LRUConstructorsCache<T> implements ConstructorsCache.ConstructorsCacheDefinition<T>{

    private final static Logger log = Logger.getLogger(ConcurrentConstructorsCache.class.getName());
    
    private final LRUCache<String, Constructor<T>> cache;

    public LRUConstructorsCache(int cacheSize){
        cache = new LRUCache<String, Constructor<T>>(cacheSize);
    }

    /**
     * 2014年12月3日
     * @author decaywood
     *
     */ 
    @Override
    public Constructor<T> get(Class<T> clazz) {
        try {
            String name = clazz.getName();
            Constructor<T> constructor = cache.get(name);
            if(constructor != null) 
                return constructor;
            constructor = clazz.getConstructor(null);
            cache.put(name, constructor);
            return constructor;
        } catch (Exception e) {
            log.info("Something is wrong when get constructor");
        }
        return null;
    }
    
    
}
