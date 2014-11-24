package org.decaywood.cache;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.decaywood.dao.ToolDao;

/**
 * 2014年10月31日
 * @author decaywood
 *
 */
public class DaosPool<T> {

    
private final ConcurrentMap<String, SoftReference<ToolDao<?>>> cache;
    
    private static class InnerClass<T> {
        final static DaosPool instance = new DaosPool();
    } 
    
    public static DaosPool getInstance(){
        return InnerClass.instance;
    }
    
    private DaosPool(){
        cache = new ConcurrentHashMap<String, SoftReference<ToolDao<?>>>();
    }
    
    public ToolDao<T> get(Class<?> clazz){
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
