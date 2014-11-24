package org.decaywood;

/**
 * 
 * 2014年10月26日
 * @author decaywood
 * 
 * 要使得某个Java Entity能和MongoDB Document实现相互转换，
 *  
 *
 */
public interface EntityDefinition {
    
    public void setId(String id);
    public String getID();
    
}
