package DecayMongo.resolver.decoder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.mongodb.DBObject;

/**
 * 2014年11月21日
 * @author decaywood
 *
 */
public class DefaultDecoder extends AbstractDecoder{

    /**
     * @param annotationType
     */
    public DefaultDecoder() {
        super(null);
    }

    /**
     * 2014年11月21日
     * @author decaywood
     *
     */ 
    @Override
    public void decode(Object object) {}

    /**
     * 2014年11月21日
     * @author decaywood
     *
     */ 
    @Override
    protected AbstractDecoder prepareData() { return null; }
    
    /**
     * 2014年11月21日
     * @author decaywood
     *
     */ 
    @Override
    public AbstractDecoder initDecoder(Field field, DBObject dbObject) {
        next = true;
        return nextDecoder;
    }

}
