package org.decaywood.resolver.decoder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import com.mongodb.DBObject;

/**
 * 2014年11月2日
 * @author decaywood
 *
 */
public abstract class AbstractDecoder implements DecoderDef{

    private static Logger log;
    
    protected boolean next;
    protected Field field;
    protected DBObject dbObject;
    protected Class<? extends Annotation> annotationClass;
    protected AbstractDecoder nextDecoder;
    protected Object value;
    
    public AbstractDecoder(Class<? extends Annotation> annotationType) {
        this.annotationClass = annotationType;
        log = Logger.getLogger(this.getClass().getName());
    }
    
    public static interface Initiator{
        public AbstractDecoder initDecoder(Field field, DBObject dbObject);
    }
    
    public AbstractDecoder initDecoder(Field field, DBObject dbObject){
        this.field = field;
        this.dbObject = dbObject;
        next = field.getAnnotation(annotationClass) == null;
        return isNext() ? nextDecoder : prepareData();
    }
    
    public boolean isNext() {
        return next;
    }
    
     
    protected abstract AbstractDecoder prepareData();
        
    
    public AbstractDecoder setNextDecoder(AbstractDecoder nextDecoder) {
        this.nextDecoder = nextDecoder;
        return nextDecoder;
    }
    
    @Override
    public boolean isEmpty(){
        if(next && nextDecoder != null)
            return nextDecoder.isEmpty();
        return value == null;
    }
    
    protected void initiateObject(Object object, Object result){
        try {
            field.set(object, result);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
 
}
