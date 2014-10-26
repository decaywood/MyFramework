package org.decaywood.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 2014年10月25日
 * @author decaywood
 *
 * 全局名称类
 */
public enum GlobalName {
    
    USER_CONFIG("user-config"),
    USER_CONFIG_NAME("name"),
    
    DATABASE_INFO("database-info"),
    DATABASE_INFO_NAME("name"),
    DATABASE_INFO_DRIVER("driver"),
    DATABASE_INFO_URL("url"),
    DATABASE_INFO_DATABASE_LOGNAME("username"),
    DATABASE_INFO_DIALECT("dialect"),
   
    RELATE("relate"),
    RELATE_POJO_XML_PATH("pojo-xml-path");
    
    private static Map<String, GlobalName> globalNameMap;
    
    private String name;
  
    private GlobalName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    private static void init(){
        if(globalNameMap == null){
            globalNameMap = new HashMap<String, GlobalName>();
            GlobalName[] globalNames = GlobalName.values();
            for(GlobalName globalName : globalNames)
                globalNameMap.put(globalName.toString(), globalName);
        }
    }
    
    public static GlobalName getGlobalName(String name){
        init();
        return globalNameMap.get(name);
    }
}
