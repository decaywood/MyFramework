package DecayMongo.resolver.encoder;

import java.util.Collection;
import java.util.Map;

import org.decaywood.Annotations;
import org.decaywood.annotations.GroupReference;

/**
 * 2014年11月21日
 * @author decaywood
 *
 */
public abstract class AbstractGroupEncoder extends AbstractEncoder{

    
    
    @Override
    public Object encode() {
        
        Class<?> type = field.getType();
        
        if(type.isArray())
            return encodeArray();
        
        if(Collection.class.isAssignableFrom(type))
            return encodeCollection();
        
        if(Map.class.isAssignableFrom(type))
            return encodeMap();
        
        return null;
    }
    
    protected abstract Object encodeArray();
    
    protected abstract Object encodeCollection();
    
    protected abstract Object encodeMap();
    

}
