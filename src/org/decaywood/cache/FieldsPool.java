package org.decaywood.cache;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import org.decaywood.annotations.Embed;
import org.decaywood.annotations.GroupEmbed;
import org.decaywood.annotations.GroupReference;
import org.decaywood.annotations.ID;
import org.decaywood.annotations.Property;
import org.decaywood.annotations.Reference;

/**
 * 2014年11月24日
 * @author decaywood
 *
 */
public class FieldsPool {

private final static Logger logger = Logger.getLogger(FieldsPool.class.getName());
    
    private final ConcurrentMap<String, SoftReference<Field[]>> cache;
    
    private static class InnerClass {
        final static FieldsPool instance = new FieldsPool();
    } 
    
    public static FieldsPool getInstance(){
        return InnerClass.instance;
    }
    
    private FieldsPool(){
        cache = new ConcurrentHashMap<String, SoftReference<Field[]>>();
    }
    
    
    private Field[] getAllFields(Class<?> clazz){
        List<Field> allFields = new ArrayList<Field>();
        allFields.addAll(filterFields(clazz.getDeclaredFields()));
        Class<?> parent = clazz.getSuperclass();
        while((parent != null) && (parent != Object.class)){
            allFields.addAll(filterFields(parent.getDeclaredFields()));
            parent = parent.getSuperclass();
        }
        return allFields.toArray(new Field[allFields.size()]);
    }
    
    
    private List<Field> filterFields(Field[] fields){
        List<Field> result = new ArrayList<Field>();
        for(Field field : fields){
            if (!Modifier.isStatic(field.getModifiers())){
                field.setAccessible(true);
                result.add(field);
            }
        }
        return result;
    }
    
    public Field[] get(Class<?> clazz){
        String name = clazz.getName();
        SoftReference<Field[]> softReference = cache.get(name);
        if(softReference != null){
            return softReference.get();
        }
        
        Field[] fields = getAllFields(clazz);
        softReference = new SoftReference<Field[]>(fields);
        SoftReference<Field[]> temp = cache.putIfAbsent(name, softReference);
        return temp != null ? temp.get() : softReference.get();
    }
   
    
    public Field getIdField(Class<?> clazz) throws Exception {
        Field result = null;
        Field[] fields = get(clazz);
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
        Field[] fields = get(clazz);
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
        boolean result = false;
        Field[] fields = get(clazz);
        for(Field field : fields){
            if(isEmbed(field, fieldName) || isGroupEmbed(field, fieldName)){
                return true;
            }
        }
        return false;
    }
    
    public boolean isEmbedField(Class<?> clazz, String fieldName){
        Field[] fields = get(clazz);
        for(Field field : fields){
            if(isEmbed(field, fieldName)){
                return true;
            }
        }
        return false;
    }
    
    
    public boolean isGroupEmbedField(Class<?> clazz, String fieldName){
        Field[] fields = get(clazz);
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
