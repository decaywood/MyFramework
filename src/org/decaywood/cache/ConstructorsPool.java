package org.decaywood.cache;

import java.lang.reflect.Constructor;

/**
 * 2014年12月3日
 * @author decaywood
 *
 */
public class ConstructorsPool{
    
    private ConstructorsPoolDefinition cache;
    
    public static interface ConstructorsPoolDefinition{
        public Constructor get(Class clazz);
    }
    
    private static class InnerClass {
        final static ConstructorsPool instance = new ConstructorsPool();
    } 
    
    public static ConstructorsPool getInstance(){
        return InnerClass.instance;
    }
    
    
    private ConstructorsPool() {}

   
    public Constructor get(Class clazz) {
        return cache.get(clazz);
    }

}
