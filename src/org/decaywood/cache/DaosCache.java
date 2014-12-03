package org.decaywood.cache;

import org.decaywood.dao.ToolDao;

 


/**
 * 2014年12月3日
 * @author decaywood
 *
 */
public class DaosCache<E> {
    
    private DaosCacheDefinition<E> cache;
    
    public static interface DaosCacheDefinition<T>{
        public ToolDao<T> get(Class<T> clazz);
    }
    
    
    public DaosCache() {}
    
    private static class InnerClass {
        final static DaosCache instance = new DaosCache();
    } 
    
    public static DaosCache getInstance(){
        return InnerClass.instance;
    }
    
    public boolean isEmpty(){
        return cache == null;
    }
    
    public void initiate(DaosCacheDefinition<E> cache){
        if(this.cache != null)  return;
        this.cache = cache;
    }
    
    public ToolDao<E> get(Class<E> clazz){
        return cache.get(clazz);
    }

}
