package org.decaywood.annotations;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * 2014年10月29日
 * @author decaywood
 *
 */
public enum Split {
    
    NONE(null),
    
    STRING(null),
    
    DAILY("yyyy-MM-dd"),
    
    MONTHLY("yyyy-MM"),
    
    YEARLY("yyyy");
    
    private SimpleDateFormat format;
    
    private Split(String pattern) {
        
        if(pattern == null) return;
        
        this.format = new SimpleDateFormat(pattern);
        
    }
    
    public String getFormat(){
        
        return this.format != null ? this.format.format(new Date()) : "";
        
    }
}
