package org.decaywood.resolver.decoder;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.decaywood.Annotations;
import org.decaywood.annotations.GroupEmbed;
import org.decaywood.cache.ConstructorsPool;
import org.decaywood.utils.MappingUtil;

import com.mongodb.DBObject;

/**
 * 2014年11月2日
 * @author decaywood
 *
 */
public class GroupEmbedDecoder extends AbstractGroupDecoder{

    private final static Logger log = Logger.getLogger(GroupEmbedDecoder.class.getName());
    
    public GroupEmbedDecoder() {
        super(GroupEmbed.class);
    }

   
    @Override
    protected Object decodeArray(Class<?> componentClass){
        List<?> list = (List<?>)value;
        Object result = Array.newInstance(componentClass, list.size());
        for(int index = 0; index < list.size(); index++){
            Object item = list.get(index);
            item = item != null ? MappingUtil.fromDBObject(componentClass, (DBObject)item) : null;
            Array.set(result, index, item);
        }
        return result;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected Object decodeCollection(Class<?> componentClass){
        try {
           
            Collection<?> collection = (Collection<?>)value;
            Collection<Object> result = (Collection<Object>) ConstructorsPool.getInstance().get(getCollectionImplementClass()).newInstance();
            for(Object item : collection){
                item = item != null ? MappingUtil.fromDBObject(componentClass, (DBObject)item) : null;
                result.add(item);
            }
            return result;
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return componentClass;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    protected Object decodeMap(Class<?> keyClass, Class<?> valueClass){
        try {
            Map<?, ?> map = (Map<?, ?>)value;
            Map<Object, Object> result = (Map<Object, Object>) ConstructorsPool.getInstance().get(getMapImplementClass()).newInstance();
            for(Object key : map.keySet()){
                Object val = map.get(key);
                Object resultKey = MappingUtil.fromDBObject(keyClass, (DBObject)key);
                Object resultValue = val != null ? MappingUtil.fromDBObject(valueClass, (DBObject)val) : null;
                result.put(resultKey, resultValue);
            }
            return result;
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return null;
    }
    
    @Override
    protected AbstractDecoder prepareData() {
        String fieldName = field.getName();
        GroupEmbed annotation = field.getAnnotation(GroupEmbed.class);
        String name = annotation.name();
        fieldName = Annotations.DEFAULT_NAME.equals(name) ? fieldName : name;
        value = dbObject.get(fieldName);
        return this;
    }


    
    @Override
    protected Class<?> getGroupClass() {
        GroupEmbed annotation = field.getAnnotation(GroupEmbed.class);
        return annotation.groupClass();
    }


    @Override
    protected Class<?> getImplementClass() {
        return null;
    }
    
   

}
