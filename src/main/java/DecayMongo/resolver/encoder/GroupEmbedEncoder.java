package DecayMongo.resolver.encoder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.decaywood.Annotations;
import org.decaywood.annotations.GroupEmbed;
import org.decaywood.utils.MappingUtil;

import com.mongodb.DBObject;

/**
 * 2014年10月30日
 * @author decaywood
 *
 */
public class GroupEmbedEncoder extends AbstractGroupEncoder {

    @Override
    protected void confirmMarker() {
        GroupEmbed annotation = field.getAnnotation(GroupEmbed.class);
        next = annotation == null;
    }
    

    @Override
    public String getFieldName() {
        GroupEmbed annotation = field.getAnnotation(GroupEmbed.class);
        String name = annotation != null ? annotation.name() : Annotations.DEFAULT_NAME;
        String fieldName = field.getName();
        fieldName = Annotations.DEFAULT_NAME.equals(name) ? fieldName : name;
        return fieldName;
    }

   
    @Override
    protected Object encodeArray(){
        List<Object> result = new ArrayList<Object>();
        int len = Array.getLength(value);
        for(int index = 0; index < len; index++){
            Object object = Array.get(value, index);
            object = object != null ? MappingUtil.toDBObject(object) : null;
            result.add(object);
        }
        return result;
    }

    
    @Override
    protected Object encodeCollection(){
        List<Object> result = new ArrayList<Object>();
        Collection<?> collection = (Collection<?>)value;
        for(Object object : collection){
            object = object != null ? MappingUtil.toDBObject(object) : null;
            result.add(object);
        }
        return result;
    }
    
    @Override
    protected Object encodeMap(){
        Map<?, ?> map = (Map<?, ?>)value;
        Map<Object, DBObject> result = new HashMap<Object, DBObject>();
        for(Object key : map.keySet()){
            Object object = map.get(key);
            DBObject value = object != null ? MappingUtil.toDBObject(object) : null;
            result.put(key, value);
        }
        return result;
    }
}
