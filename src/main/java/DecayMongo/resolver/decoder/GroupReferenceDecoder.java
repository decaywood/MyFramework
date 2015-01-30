package DecayMongo.resolver.decoder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.decaywood.Annotations;
import org.decaywood.EntityDefinition;
import org.decaywood.MongoDBconstant;
import org.decaywood.annotations.GroupReference;
import org.decaywood.cache.ConstructorsCache;
import org.decaywood.cache.DaosCache;
import org.decaywood.dao.ToolDao;
import org.decaywood.query.IQuery;
import org.decaywood.utils.AccessUtil;

/**
 * 2014年11月3日
 * @author decaywood
 *
 */
public class GroupReferenceDecoder extends AbstractGroupDecoder {

    private final static Logger log = Logger.getLogger(GroupReferenceDecoder.class.getName());
    
    /**
     * @param annotationType
     */
    private GroupReference annotation;
    
    public GroupReferenceDecoder() {
        super(GroupReference.class);
    }

  
    @Override
    protected Object decodeArray(Class<?> componentClass){
        boolean canRead = (annotation.cascade() & Annotations.CASCADE_RETRIEVE) != 0;
        Object result = canRead ? getReferenceArray(componentClass) : 
                                 getSimpleArray(componentClass);
        return result;
    }
    
    @Override
    protected Object decodeCollection(Class<?> componentClass){
        boolean canRead = (annotation.cascade() & Annotations.CASCADE_RETRIEVE) != 0;
        Collection<?> result = canRead ? getReferenceCollection(componentClass) : 
                                         getSimpleCollection(componentClass);
        return result;
    }
    
    
    @Override
    protected Object decodeMap(Class<?> keyClass, Class<?> valueClass) {
        boolean canRead = (annotation.cascade() & Annotations.CASCADE_RETRIEVE) != 0;
        Map<?, ?> result = canRead ? getReferenceMap(keyClass, valueClass) : 
                                 getSimpleMap(keyClass, valueClass);
        return result;
    }
    
    @Override
    protected AbstractDecoder prepareData() {
        annotation = field.getAnnotation(GroupReference.class);
        String fieldName = field.getName();
        String name = annotation.name();
        fieldName = Annotations.DEFAULT_NAME.equals(name) ? fieldName : name;
        value = dbObject.get(fieldName);
        return this;
    }
    
    protected Map<?, ?> getReferenceMap(Class<?> keyClass, Class<?> valueClass){
        try {
            valueClass = getComponentImplementClass(annotation, valueClass);
            Class<?> mapClazz = getMapImplementClass();
            Map<Object, Object> result = (Map<Object, Object>) ConstructorsCache.getInstance().get(mapClazz).newInstance();
            Map<?, ?> map = (Map<?, ?>)value;
            ToolDao<?> dao = DaosCache.getInstance().get(valueClass);
            for(Object key : map.keySet()){
                Object item = map.get(key);
                if(item == null){
                    result.put(key, null);
                    continue;
                }
                String referenceID = AccessUtil.fromDBReference(annotation, valueClass);
                EntityDefinition entity = (EntityDefinition)dao.findOne(referenceID);
                result.put(key, entity);
            }
            return result;
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    protected Map<Object, Object> getSimpleMap(Class<?> keyClass, Class<?> valueClass){
        
        try {
            valueClass = getComponentImplementClass(annotation, valueClass);
            Class<?> mapClazz = getMapImplementClass();
            Map<?, ?> map = (Map<?, ?>)value;
            Map<Object, Object> result = (Map<Object, Object>) ConstructorsCache.getInstance().get(mapClazz).newInstance();
            for(Object key : map.keySet()){
                Object value = map.get(key);
                if(value == null){
                    result.put(key, null);
                    continue;
                }
                String referenceID = AccessUtil.fromDBReference(annotation, value);
                EntityDefinition entity = (EntityDefinition) ConstructorsCache.getInstance().get(valueClass).newInstance();
                entity.setID(referenceID);
                result.put(key, entity);
            }
            return result;
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return null;
    }
    
    
    @SuppressWarnings("unchecked")
    protected Collection<?> getReferenceCollection(Class<?> componentClass){
       
        try {
            
            Class<?> implementClass = getComponentImplementClass(annotation, componentClass);
            Class<?> collectionImplClass = (Class<?>) getCollectionImplementClass();
            Collection<?> result = (Collection<EntityDefinition>) ConstructorsCache.getInstance().get(collectionImplClass).newInstance();
            Collection<Object> collection = (Collection<Object>)value;
            List<String> idList = new ArrayList<String>();
            for(Object item : collection){
                String referenceID = item != null ? AccessUtil.fromDBReference(annotation, item) : null;
                idList.add(referenceID);
            }
            
            ToolDao<?> dao = DaosCache.getInstance().get(implementClass);
            IQuery<?> query = dao.query().in(MongoDBconstant.ID, idList);
            
            String orderBy = annotation.sort();
            result = orderBy != Annotations.DEFAULT_SORT ? query.sort(orderBy).results(collectionImplClass) : query.results(collectionImplClass);
            
            return  result;
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    protected Collection<EntityDefinition> getSimpleCollection(Class<?> componentClass){
        try {
            Class<?> implementClass = getComponentImplementClass(annotation, componentClass);
            Class<?> collectionImplClass = getCollectionImplementClass();
            Collection<EntityDefinition> result = (Collection<EntityDefinition>) ConstructorsCache.getInstance().get(collectionImplClass).newInstance();
            Collection<Object> collection = (Collection<Object>)value;
            for(Object item : collection){
                if(item == null){
                    result.add(null);
                    continue;
                }
                String referenceID = AccessUtil.fromDBReference(annotation, item);
                EntityDefinition entity = (EntityDefinition) ConstructorsCache.getInstance().get(implementClass).newInstance();
                entity.setID(referenceID);
                result.add(entity);
            }
            return result;
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return null;
    }
    
    
    protected Object getReferenceArray(Class<?> componentClass){
        Class<?> implementClass = getComponentImplementClass(annotation, componentClass);
        ToolDao<?> dao = DaosCache.getInstance().get(implementClass);
        List<?> list = (List<?>) value;
        List<String> IDList = new ArrayList<String>();
        for(Object item : list){
            String referenceID = item != null ? AccessUtil.fromDBReference(annotation, item) : null;
            IDList.add(referenceID);
        }
        IQuery<?> query = dao.query().in(MongoDBconstant.ID, IDList);
        String sort = annotation.sort();
        if(!Annotations.DEFAULT_SORT.equals(sort))
            query.sort(sort);
        List<?> entitys = query.results();
        
        
        int arraySize = Math.min(list.size(), entitys.size());
        Object array = Array.newInstance(implementClass, arraySize);
        for(int index = 0; index < arraySize; index++){
            Array.set(array, index, entitys.get(index));
        }
        return array;
    }
    
    protected Object getSimpleArray(Class<?> componentClass){
       
        try {
            Class<?> implementClass = getComponentImplementClass(annotation, componentClass);
            List<?> list = (List<?>) value;
            Object array = Array.newInstance(implementClass, list.size());
            for(int index = 0; index < list.size(); index++){
                Object item = list.get(index);
                if(item == null){
                    Array.set(array, index, null);
                    continue;
                }
                String referenceID = AccessUtil.fromDBReference(annotation, item);
                EntityDefinition entity = (EntityDefinition) ConstructorsCache.getInstance().get(implementClass).newInstance();
                entity.setID(referenceID);
                Array.set(array, index, entity);
            }
            return array;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


     
    @Override
    protected Class<?> getGroupClass() {
        return annotation != null ? annotation.groupClass() : null;
    }


    /**
     * 2014年11月21日
     * @author decaywood
     *
     */ 
    @Override
    protected Class<?> getImplementClass() {
        return annotation != null ? annotation.implementClass() : null;
    }


   


  

}
