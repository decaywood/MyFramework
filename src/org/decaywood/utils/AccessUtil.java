package org.decaywood.utils;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import org.decaywood.annotations.GroupReference;
import org.decaywood.annotations.ID;
import org.decaywood.annotations.Reference;
import org.decaywood.cache.FieldsCache;

import com.mongodb.DBRef;

/**
 * 2014年11月1日
 * @author decaywood
 *
 */
public class AccessUtil {

  private final static Logger log = Logger.getLogger(AccessUtil.class.getName());
    
    public static Object convertToDbReference(Reference reference, Class<?> clazz, String idStr){
        if(StringUtil.isEmpty(idStr)){
            return null;
        }
        Object result = null;
        if(reference.reduced()){
            result = convertToManualRef(clazz, idStr);
        }else{
            result = convertToDbReference(clazz, idStr);
        }
        return result;
    }
    
    
    public static Object convertToDbReference(GroupReference groupReference, Class<?> clazz, String idStr){
        if(StringUtil.isEmpty(idStr)){
            return null;
        }
        Object result = null;
        if(groupReference.reduced()){
            result = convertToManualRef(clazz, idStr);
        }else{
            result = convertToDbReference(clazz, idStr);
        }
        return result;
    }
    
    
    private static Object convertToManualRef(Class<?> clazz, String IDString){
        Object result = null;
        try{
            Field idField = FieldsCache.getInstance().getIdField(clazz);
            ID idAnnotation = idField.getAnnotation(ID.class);
            result = idAnnotation.type().getConvertedID(IDString);
        }catch(Exception e){
            log.info(e.getMessage());
        }
        return result;
    }
    
    private static DBRef convertToDbReference(Class<?> clazz, String idStr){
        try {
            String name = MappingUtil.getEntityName(clazz);
            ID idAnnotation = getIDAnnotation(clazz);
            Object dbID = idAnnotation.type().getConvertedID(idStr);
            return new DBRef(name, dbID);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return null;
    }
    
    private static ID getIDAnnotation(Class<?> clazz){
        Field idField = null;
        try {
            idField = FieldsCache.getInstance().getIdField(clazz);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return idField.getAnnotation(ID.class);
    }
    
    public static String fromDBReference(Reference ref, Object value){
        String result = null;
        if(ref.reduced()){
            result = value.toString();
        }else{
            DBRef dbRef = (DBRef)value;
            result = dbRef.getId().toString();
        }
        return result;
    }
    
    public static String fromDBReference(GroupReference groupReference, Object value){
        String result = null;
        if(groupReference.reduced()){
            result = value.toString();
        }else{
            DBRef dbRef = (DBRef)value;
            result = dbRef.getId().toString();
        }
        return result;
    }
    
    public static Object convertToDbReference(Class<?> clazz, String fieldName, Class<?> refClass, String idStr){
        Object result = null;
        Field field = null;
        try{
            field = FieldsCache.getInstance().getField(clazz, fieldName);
        }catch(Exception e){
            log.info(e.getMessage());
        }
        Reference annotation = field.getAnnotation(Reference.class);
        if(annotation != null){
            result = convertToDbReference(annotation, refClass, idStr);
        }else{
            GroupReference refList = field.getAnnotation(GroupReference.class);
            result = convertToDbReference(refList, refClass, idStr);
        }
        return result;
    }
    
}
