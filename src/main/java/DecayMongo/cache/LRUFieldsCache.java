package DecayMongo.cache;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import DecayMongo.utils.LRUCache;


/**
 * 2014年12月3日
 * @author decaywood
 *
 */
public class LRUFieldsCache implements FieldsCache.FieldsCacheDefinition{

    private final LRUCache<String, Field[]> cache;
    
    public LRUFieldsCache(int cacheSize){
        cache = new LRUCache<String, Field[]>(cacheSize);
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
    
    @Override
    public Field[] get(Class clazz){
        String name = clazz.getName();
        Field[] fields = cache.get(name);
        if(fields != null){
            return fields;
        }
        
        fields = getAllFields(clazz);
        cache.put(name, fields);
        return fields;
    }
   
    

}
