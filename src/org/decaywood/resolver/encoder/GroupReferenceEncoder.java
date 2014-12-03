package org.decaywood.resolver.encoder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.decaywood.Annotations;
import org.decaywood.EntityDefinition;
import org.decaywood.annotations.GroupReference;
import org.decaywood.cache.DaosCache;
import org.decaywood.dao.BasicDao;
import org.decaywood.utils.AccessUtil;
import org.decaywood.utils.MappingUtil;

import com.mongodb.DBObject;

/**
 * 2014年11月1日
 * @author decaywood
 *
 */
public class GroupReferenceEncoder extends AbstractGroupEncoder {

    
    @Override
    protected void confirmMarker() {
        GroupReference annotation = field.getAnnotation(GroupReference.class);
        next = annotation == null;
    }
    
    @Override
    public String getFieldName() {
        GroupReference annotation = field.getAnnotation(GroupReference.class);
        String name = annotation != null ? annotation.name() : Annotations.DEFAULT_NAME;
        String fieldName = field.getName();
        fieldName = Annotations.DEFAULT_NAME.equals(name) ? fieldName : name;
        return fieldName;
    }

    
    /**
     * 处理数组元素Entity
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected Object encodeArray(){
        Class<?> componentType = field.getType().getComponentType();
        GroupReference annotation = field.getAnnotation(GroupReference.class);
        List<Object> result = new ArrayList<Object>();
        componentType = getImplementClass(componentType, annotation);
        BasicDao dao = DaosCache.getInstance().get(componentType);
        int len = Array.getLength(value);
        for(int index = 0; index < len; index++){
            EntityDefinition entity = (EntityDefinition) Array.get(value, index);
            if(entity == null) continue;
            int cascade = annotation.cascade();
            boolean isCreate = (cascade & Annotations.CASCADE_CREATE) != 0;
            boolean isUpdate = (cascade & Annotations.CASCADE_UPDATE) != 0;
            if(isCreate && isUpdate){
                dao.updateData(entity);
            }
            result.add(AccessUtil.convertToDbReference(annotation, entity.getClass(), entity.getID()));
        }
        return result;
    }
    
    
    @Override
    protected Object encodeCollection(){
        List<DBObject> result = new ArrayList<DBObject>();
        Collection<?> collection = (Collection<?>)value;
        for(Object object : collection){
            if(object == null) continue;
            result.add(MappingUtil.toDBObject(object));
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
    
    
    protected Class<?> getImplementClass(Class<?> clazz, GroupReference annotation){
        if(!clazz.isInterface()) return clazz;
        if(annotation == null) return clazz;
        Class<?> tempClass = annotation.implementClass();
        return tempClass != Annotations.class ? tempClass : clazz;
    }

}
