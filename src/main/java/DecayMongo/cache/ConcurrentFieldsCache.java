package DecayMongo.cache;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 2014年11月24日
 * @author decaywood
 *
 */
public class ConcurrentFieldsCache<T> implements FieldsCache.FieldsCacheDefinition{


    
    private final ConcurrentMap<String, SoftReference<Field[]>> cache;
    
 
    
    public ConcurrentFieldsCache(){
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
    
    @Override
    public Field[] get(Class clazz){
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
   
    
    
    
}
