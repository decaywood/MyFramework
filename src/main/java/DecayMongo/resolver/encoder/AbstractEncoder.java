package DecayMongo.resolver.encoder;

import java.lang.reflect.Field;
import java.util.logging.Logger;

/**
 * 2014年10月30日
 * @author decaywood
 *
 */
public abstract class AbstractEncoder implements EncoderDef{

    private static final Logger log = Logger.getLogger(AbstractEncoder.class.getName());
    
    protected boolean next;
    protected Field field;
    protected Object value;
    protected Class<?> clazz;
    protected AbstractEncoder nextEncoder;
    
    public static interface Initiator{
        public AbstractEncoder initEncoder(Field field, Object object);
    }
    
    public AbstractEncoder initEncoder(Field field, Object object){
        this.field = field;
        value = convert(object, field);  
        clazz = object.getClass();
        confirmMarker();
        return next ? nextEncoder : this;
    }
    
    public boolean isNext() {
        return next;
    }
    
    protected abstract void confirmMarker();
    
    public AbstractEncoder setNextEncoder(AbstractEncoder nextEncoder) {
        this.nextEncoder = nextEncoder;
        return nextEncoder;
    }
    
    @Override
    public boolean isEmpty(){
        if(next && nextEncoder != null)
            return nextEncoder.isEmpty();
        return value == null;
    }
 
    
    private Object convert(Object object, Field field){
        Object value = null;
        try {
            value = field.get(object); //取得类中的域的引用
        } catch (IllegalAccessException e) {
            log.info(e.getMessage());
        }
        return value;
    }
    
}
