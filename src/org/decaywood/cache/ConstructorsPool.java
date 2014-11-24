package org.decaywood.cache;

import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;


/**
 * 2014年11月21日
 * @author decaywood
 *
 */
public class ConstructorsPool {
    
    private final static Logger log = Logger.getLogger(ConstructorsPool.class.getName());
    
    private final ConcurrentMap<String, SoftReference<Constructor<?>>> cache;

    private static class InnerClass<T> {
        final static ConstructorsPool instance = new ConstructorsPool();
    } 
    
    public static ConstructorsPool getInstance(){
        return InnerClass.instance;
    }
    
    private ConstructorsPool(){
        cache = new ConcurrentHashMap<String, SoftReference<Constructor<?>>>();
    }
    
    public <T> Constructor<T> get(Class<T> clazz){
       
        try {
            String name = clazz.getName();
            SoftReference<Constructor<?>> softReference = cache.get(name);
            if(softReference != null) 
                return (Constructor<T>)softReference.get();
            Class[] types = null;
            Constructor<?> constructor = null;
            constructor = clazz.getConstructor(types);
            softReference = new SoftReference<Constructor<?>>(constructor);
            SoftReference<Constructor<?>> temp = cache.putIfAbsent(name, softReference);
            return (Constructor<T>) (temp != null ? temp.get() : softReference.get());
        } catch (Exception e) {
            log.info("Something is wrong when get constructor");
        }
        return null;
    }
    
}
