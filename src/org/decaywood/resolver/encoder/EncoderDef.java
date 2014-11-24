package org.decaywood.resolver.encoder;

/**
 * 2014年10月30日
 * @author decaywood
 *
 */
public interface EncoderDef {
    
    public boolean isEmpty();
    
    public String getFieldName();
    
    public Object encode();
    
}
