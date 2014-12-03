package org.decaywood.cache;

import org.decaywood.dao.ToolDao;

 


/**
 * 2014年12月3日
 * @author decaywood
 *
 */
public class DaosPool {
    
    DaosPoolDefinition cache;
    
    public static interface DaosPoolDefinition{
        public ToolDao get(Class clazz);
    }
    
    
    public DaosPool() {}
    
    private static class InnerClass {
        final static DaosPool instance = new DaosPool();
    } 
    
    public static DaosPool getInstance(){
        return InnerClass.instance;
    }
    
    public ToolDao get(Class clazz){
        return cache.get(clazz);
    }

}
