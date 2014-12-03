package org.decaywood.listener;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.decaywood.Annotations;
import org.decaywood.EntityDefinition;
import org.decaywood.MongoDBconstant;
import org.decaywood.annotations.GroupReference;
import org.decaywood.annotations.Reference;
import org.decaywood.cache.DaosPool;
import org.decaywood.dao.ToolDao;
import org.decaywood.query.IQuery;

/**
 * 
 * 2014年10月29日
 * @author decaywood
 *
 */
public class DeleteListener extends BaseListener{
    
    public DeleteListener() {}
     
    public DeleteListener(List<Field> groupRefFields, List<Field> refFields) {
         super(groupRefFields, refFields);
    }

    @Override
    public void deleteAction(EntityDefinition entity) {
        
        for(Field field : referenceFields)
            processReference(entity, field);
        
        for(Field field : groupReferenceFields)
            processGroupReference(entity, field);
        
        
    }
    
    private void processGroupReference(EntityDefinition entity, Field field){
        
        Object value = getValue(entity, field);
        if(value == null) return;
        
        List<String> IDs = null;
        
        Class<?> type = field.getType();
        GroupReference annotation = field.getAnnotation(GroupReference.class);
        Class<?> componentClass = null;
        
        if(type.isArray()){
            componentClass = getImplementClass(annotation, type.getComponentType());
            IDs = deleteArray(value);
        }
        
        else if(Collection.class.isAssignableFrom(type)){
            ParameterizedType paramType = (ParameterizedType)field.getGenericType();
            Type[] types = paramType.getActualTypeArguments();
            componentClass = getImplementClass(annotation, (Class<?>)types[0]);
            IDs = deleteCollection(value);
        }
        
        else if(Map.class.isAssignableFrom(type)){
            ParameterizedType paramType = (ParameterizedType)field.getGenericType();
            Type[] types = paramType.getActualTypeArguments();
            componentClass = getImplementClass(annotation, (Class<?>)types[0]);
            IDs = deleteMap((Class<?>)types[0], componentClass, value);
        }
        
        if(IDs == null) return;
        
        ToolDao dao = DaosPool.getInstance().get(componentClass);
        IQuery query = dao.query().in(MongoDBconstant.ID, IDs);
        dao.deleteData(query);
        
    }
    
    private List<String> deleteArray(Object value){
        List<String> IDs = new ArrayList<String>();
        int len = Array.getLength(value);
        for(int index = 0; index < len; index++){
            Object item = Array.get(value, index);
            if(item == null) continue;
            EntityDefinition entity = (EntityDefinition)item;
            IDs.add(entity.getID());
        }
        return IDs;
    }
    
    private List<String> deleteCollection(Object value){
        List<String> IDs = new ArrayList<String>();
        Collection<EntityDefinition> entitys = (Collection<EntityDefinition>)value;
        for(EntityDefinition entity : entitys){
            if(entity == null) continue;
            IDs.add(entity.getID());
        }
        return IDs;
    }
    
    private List<String> deleteMap(Class<?> keyClass, Class<?> valueClass, Object value){
        List<String> IDs = new ArrayList<String>();
        Map<Object, EntityDefinition> map = (Map<Object, EntityDefinition>)value;
        for(Map.Entry<Object, EntityDefinition> entry : map.entrySet()){
            if(entry.getValue() == null) continue;
            IDs.add(entry.getValue().getID());
        }
        return IDs;
    }
    
    private void processReference(EntityDefinition entity, Field field){
        Object value = getValue(entity, field);
        if(value != null){
            Class<?> fieldClass = field.getType();
            Reference annotation = field.getAnnotation(Reference.class);
            fieldClass = getImplementClass(annotation, fieldClass);
            ToolDao<Object> dao = DaosPool.getInstance().get(fieldClass);
            dao.deleteData(value);
        }
    }
    
    private Object getValue(Object object, Field field){
        Object value = null;
        try {
            value = field.get(object); //取得类中的域的引用
        } catch (Exception e) {
            log.info("Can not get field's value");
        }
        return value;
    }
    
    private Class<?> getImplementClass(Reference annotation, Class<?> fieldClass){
        if(!fieldClass.isInterface()) return fieldClass;
        if(annotation == null) return fieldClass;
        Class<?> tempClass = annotation.implementClass();
        return tempClass != Annotations.class ? tempClass : fieldClass;
    }
    
    
    private Class<?> getImplementClass(GroupReference annotation, Class<?> fieldClass){
        if(!fieldClass.isInterface()) return fieldClass;
        if(annotation == null) return fieldClass;
        Class<?> tempClass = annotation.implementClass();
        return tempClass != Annotations.class ? tempClass : fieldClass;
    }

}
