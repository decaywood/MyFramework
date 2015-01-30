package DecayMongo.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import DecayMongo.Annotations;
import DecayMongo.MongoDBconstant;
import DecayMongo.annotations.Embed;
import DecayMongo.annotations.Entity;
import DecayMongo.annotations.GroupEmbed;
import DecayMongo.annotations.Property;
import DecayMongo.cache.ConstructorsCache;
import DecayMongo.cache.FieldsCache;
import DecayMongo.resolver.DecodeManager;
import DecayMongo.resolver.EncodeManager;
import DecayMongo.resolver.decoder.AbstractDecoder;
import DecayMongo.resolver.encoder.AbstractEncoder;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * 
 * 2014年10月29日
 * @author decaywood
 *
 */
public class MappingUtil<T> {
    
    private static final Logger log = Logger.getLogger(MappingUtil.class.getName());

  
    public static <T> T fromDBObject(Class<T> clazz, DBObject dbObject){
        try {
            if(dbObject == null) return null;
            DecodeManager manager = new DecodeManager();
            boolean isBasic = BasicTypeChecker.getInstance().isBasicType(clazz);
            T object = (T) (isBasic ? dbObject.get("value") : ConstructorsCache.getInstance().get(clazz).newInstance());
            if(isBasic) return object;
            Field[] fields = FieldsCache.getInstance().get(clazz);
            for(Field field : fields){
                AbstractDecoder decoder = manager.initDecoder(field, dbObject);
                if(decoder!=null && !decoder.isEmpty()){
                    decoder.decode(object);
                }
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    public static DBObject toDBObject(Object object){ 
        if(object == null) return null;
        EncodeManager manager = new EncodeManager();
        Class<?> clazz = object.getClass();
        Field[] fields = FieldsCache.getInstance().get(clazz);
        DBObject dbo = new BasicDBObject();
        for(Field field : fields){
            AbstractEncoder encoder = manager.initEncoder(field, object);
            if(encoder!=null && !encoder.isEmpty()){
                dbo.put(encoder.getFieldName(), encoder.encode());
            }
        }
        return dbo;
    }
    
    public static <T> List<T> toList(Class<T> clazz, DBCursor cursor){
        List<T> list = new ArrayList<T>();
        while(cursor.hasNext()){
            DBObject dbo = cursor.next();
            list.add(fromDBObject(clazz, dbo));
        }
        cursor.close();
        return list;
    }
    
    
    public static <T> Collection<T> toRealCollection(Class<T> clazz, DBCursor cursor, Class<?> implementClass){
        Collection<T> collection = null;
        try {
            collection = (Collection<T>) ConstructorsCache.getInstance().get(implementClass).newInstance();
            while(cursor.hasNext()){
                DBObject dbo = cursor.next();
                collection.add(fromDBObject(clazz, dbo));
            }
            cursor.close();
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return collection;
    }
    
    
    public static DBObject getSort(String orderBy){
        DBObject sort = new BasicDBObject();
        orderBy = orderBy.replaceAll("[{}'']", "");
        String[] arr = orderBy.split(",");
        for(String s : arr){
            String[] kv = s.split(":");
            String k = kv[0].trim();
            String v = kv[1].trim();
            if(k.equals("id")){  
                k = MongoDBconstant.ID;
            }
            sort.put(k, Integer.parseInt(v));
        }
        return sort;
    }
    
    
//    public static List<DBIndex> getDBIndex(String index){
//        List<DBIndex> list = new ArrayList<DBIndex>();
//        index = index.replaceAll("\\}[^{^}]+\\{", "};{");
//        index = index.replaceAll("[{}'']", "");
//        String[] items = index.split(";");
//        for(String item : items){
//            DBObject keys = new BasicDBObject();
//            DBObject options = new BasicDBObject("background", true);
//            String[] arr = item.split(",");
//            for(String s : arr){
//                String[] kv = s.split(":");
//                String k = kv[0].trim();
//                String v = kv[1].trim();
//                 
//                if(v.equalsIgnoreCase("2d") || v.equalsIgnoreCase("text")){
//                    keys.put(k, v);
//                }
//                else if(k.equalsIgnoreCase("expireAfterSeconds")){
//                    options.put(k, Integer.parseInt(v));
//                }
//                else if(v.equals("1") || v.equals("-1")){
//                    keys.put(k, Integer.parseInt(v));
//                }
//                else if(v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false")){
//                    options.put(k, Boolean.parseBoolean(v));
//                }
//                else if(k.equalsIgnoreCase("name")){
//                    options.put(k, v);
//                }
//            }
//            DBIndex dbi = new DBIndex();
//            dbi.setKeys(keys);
//            dbi.setOptions(options);
//            list.add(dbi);
//        }
//        return list;
//    }
    
   
    public static String getEntityName(Class<?> clazz){
        Entity entity = clazz.getAnnotation(Entity.class);
        String name = entity.name();
        if(name.equals(Annotations.DEFAULT_NAME)){
            name = clazz.getSimpleName().toLowerCase();
        }
        return name;
    }
    
  
    public static DBObject getKeyFields(Class<?> clazz){
        DBObject keys = new BasicDBObject();
        Field[] fields = FieldsCache.getInstance().get(clazz);
        for(Field field : fields){
            String fieldName = field.getName();
            Property property = field.getAnnotation(Property.class);
            if(property!=null && property.lazy()){
                String name = property.name();
                if(!name.equals(Annotations.DEFAULT_NAME)){
                    fieldName = name;
                }
                keys.put(fieldName, 0);
                continue;
            }
            Embed embed = field.getAnnotation(Embed.class);
            if(embed!=null && embed.lazy()){
                String name = embed.name();
                if(!name.equals(Annotations.DEFAULT_NAME)){
                    fieldName = name;
                }
                keys.put(fieldName, 0);
                continue;
            }
            GroupEmbed embedList = field.getAnnotation(GroupEmbed.class);
            if(embedList!=null && embedList.lazy()){
                String name = embedList.name();
                if(!name.equals(Annotations.DEFAULT_NAME)){
                    fieldName = name;
                }
                keys.put(fieldName, 0);
                continue;
            }
        }
        return keys;
    }
    
}
