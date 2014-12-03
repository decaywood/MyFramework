package org.decaywood.cache;

import java.lang.reflect.Constructor;

/**
 * 2014年12月3日
 * @author decaywood
 *
 */
public class ConstructorsCache<E>{
    
    private ConstructorsCacheDefinition<E> cache;
    
    public static interface ConstructorsCacheDefinition<T>{
        public Constructor<T> get(Class<T> clazz);
    }
    
    private static class InnerClass {
        final static ConstructorsCache instance = new ConstructorsCache();
    } 
    
    public static ConstructorsCache getInstance(){
        return InnerClass.instance;
    }
    
    public boolean isEmpty(){
        return cache == null;
    }
    
    public void initiate(ConstructorsCacheDefinition<E> cache){
        if(this.cache != null)  return;
        this.cache = cache;
    }
    
    private ConstructorsCache() {}

   
    public Constructor<E> get(Class<E> clazz) {
        return cache.get(clazz);
    }

}
