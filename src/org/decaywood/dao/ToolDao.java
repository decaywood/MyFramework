package org.decaywood.dao;

import java.util.List;

import org.decaywood.annotations.ID;
import org.decaywood.constant.MongoDBconstant;
import org.decaywood.utils.MappingUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * 2014年10月31日
 * @author decaywood
 *
 */
public class ToolDao<T> extends RemoteDao<T> {

    public ToolDao(Class<T> clazz) {
        super(clazz);
    }
    
     
    public DBObject getKeys() {
        return keys;
    }
    
     
    public T findOneLazily(String id){
        DBObject dbo = new BasicDBObject();
        ID idAnnotation = clazz.getAnnotation(ID.class);
        dbo.put(MongoDBconstant.ID, idAnnotation.type().getConvertedID(id));
        DBObject result = collection.findOne(dbo, keys);
        return MappingUtil.fromDBObject(clazz, result);
    }
    
    public List<T> findNotLazily(DBObject query){
        DBCursor cursor = collection.find(query);
        return MappingUtil.toList(clazz, cursor);
    }
    
    public List<T> findNotLazily(int pageNum, int pageSize){
        DBCursor cursor = collection.find().skip((pageNum-1)*pageSize).limit(pageSize);
        return MappingUtil.toList(clazz, cursor);
    }
    
     
    public synchronized long getMaxId(){
        double d = this.max(MongoDBconstant.ID);
        return (long)d;
    }

}
