package org.decaywood.cache;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import org.decaywood.annotations.Embed;
import org.decaywood.annotations.GroupEmbed;
import org.decaywood.annotations.GroupReference;
import org.decaywood.annotations.ID;
import org.decaywood.annotations.Property;
import org.decaywood.annotations.Reference;


/**
 * 2014年12月3日
 * @author decaywood
 *
 */
public class FieldsPool {
    
    private final static Logger logger = Logger.getLogger(FieldsPool.class.getName());

    private FieldsPoolDefinition cache;
    
    public static interface FieldsPoolDefinition{
        
        public Field[] get(Class clazz);
        
    }
    
    private static class InnerClass {
        final static FieldsPool instance = new FieldsPool();
    } 
    
    public static FieldsPool getInstance(){
        return InnerClass.instance;
    }
    
    public Field[] get(Class clazz){
        return cache.get(clazz);
    }
    
    
    public Field getIdField(Class<?> clazz) throws Exception {
        Field result = null;
        Field[] fields = cache.get(clazz);
        for(Field f : fields){
            if(f.getAnnotation(ID.class) != null){
                result = f;
                break;
            }
        }
        if(result == null){
            throw new Exception(clazz.getName() + " does not contain @Id field.");
        }
        return result;
    }
    
     
    public String getIdFieldName(Class<?> clazz){
        String name = null;
        Field f = null;
        try{
            f = this.getIdField(clazz);
        }catch(Exception ex){
            logger.info(ex.getMessage());
        }
        if(f != null){
            name = f.getName();
        }
        return name;
    }
   
    
    public Field getField(Class<?> clazz, String fieldName) throws Exception {
        Field field = null;
        Field[] fields = cache.get(clazz);
        for(Field f : fields){
            if(f.getName().equals(fieldName)){
                field = f;
                break;
            }
        }
        if(field == null){
            for(Field f : fields){
                Property property = f.getAnnotation(Property.class);
                if(property != null && property.name().equals(fieldName)){
                    field = f;
                    break;
                }
                Embed embed = f.getAnnotation(Embed.class);
                if(embed != null && embed.name().equals(fieldName)){
                    field = f;
                    break;
                }
                GroupEmbed el = f.getAnnotation(GroupEmbed.class);
                if(el != null && el.name().equals(fieldName)){
                    field = f;
                    break;
                }
                Reference ref = f.getAnnotation(Reference.class);
                if(ref != null && ref.name().equals(fieldName)){
                    field = f;
                    break;
                }
                GroupReference rl = f.getAnnotation((GroupReference.class));
                if(rl != null && rl.name().equals(fieldName)){
                    field = f;
                    break;
                }
            }
        }
        if(field == null){
            throw new Exception("Field '" + fieldName + "' does not exists!");
        }
        return field;
    }
    
    
    public boolean isEmbedOrGroupEmbeded(Class<?> clazz, String fieldName){
        Field[] fields = cache.get(clazz);
        for(Field field : fields){
            if(isEmbed(field, fieldName) || isGroupEmbed(field, fieldName)){
                return true;
            }
        }
        return false;
    }
    
    public boolean isEmbedField(Class<?> clazz, String fieldName){
        Field[] fields = cache.get(clazz);
        for(Field field : fields){
            if(isEmbed(field, fieldName)){
                return true;
            }
        }
        return false;
    }
    
    
    public boolean isGroupEmbedField(Class<?> clazz, String fieldName){
        Field[] fields = cache.get(clazz);
        for(Field field : fields){
            if(isGroupEmbed(field, fieldName)){
                return true;
            }
        }
        return false;
    }
    
    private boolean isEmbed(Field field, String fieldName){
        return field.getName().equals(fieldName) && field.getAnnotation(Embed.class)!=null;
    }
    
    private boolean isGroupEmbed(Field field, String fieldName){
        return field.getName().equals(fieldName) && field.getAnnotation(GroupEmbed.class)!=null;
    }
}
