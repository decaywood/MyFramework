package org.decaywood.cache;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.decaywood.cache.DaosPool.DaosPoolDefinition;
import org.decaywood.dao.ToolDao;

/**
 * 2014年10月31日
 * @author decaywood
 *
 */
public class ConcurrentDaosPool<T> implements DaosPool.DaosPoolDefinition{

    
    private final ConcurrentMap<String, SoftReference<ToolDao<?>>> cache;
    
    private ConcurrentDaosPool(){
        cache = new ConcurrentHashMap<String, SoftReference<ToolDao<?>>>();
    }

    /**
     * 2014年12月3日
     * @author decaywood
     *
     */ 
    @Override
    public ToolDao get(Class clazz) {
        String name = clazz.getName();
        SoftReference<ToolDao<?>> softReference = cache.get(name);
        if(softReference != null)
            return (ToolDao<T>)softReference.get();
        ToolDao<?> dao = new ToolDao<>(clazz);
        softReference = new SoftReference<ToolDao<?>>(dao);
        SoftReference<ToolDao<?>> temp = cache.putIfAbsent(name, softReference);
        return (ToolDao<T>) (temp != null ? temp.get() : softReference.get()); 
    }

    
    
    

    
}
