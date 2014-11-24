package org.decaywood.dao;

import java.util.List;

import org.decaywood.EntityDefinition;
import org.decaywood.listener.ListenerDef;
import org.decaywood.query.IQuery;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

/**
 * 数据访问接口
 * 2014年10月29日
 * @author decaywood
 *
 */
public interface DaoDef <T> {
 
    /**
     * The default write concern is ACKNOWLEDGED, you can change it.
     * @param concern 
     */
    public void setWriteConcern(WriteConcern concern);
    
    public WriteConcern getWriteConcern();
    
    public void addListener(ListenerDef listener);
    
    
    
    public void notifyCreate(final EntityDefinition entity);
    /**
     * notify all listeners after an entity is inserted.
     * the listeners' process code is executed asynchronized.
     * @param entity the entity contains all fields' value.
     */
    public void notifyRetrieve(final EntityDefinition entity);
    
    /**
     * notify all listeners after an entity is updated.
     * the listeners' process code is executed asynchronized.
     * @param entity the entity contains all fields' value.
     */
    public void notifyUpdated(final EntityDefinition entity);
    /**
     * notify all listeners after an entity is deleted.
     * the listeners' process code is executed asynchronized.
     * @param entity the entity contains all fields' value.
     */
    public void notifyDeleted(final EntityDefinition entity);
    /**
     * Insert an entity to mongoDB.
     * @param t
     * @return 
     */
    public WriteResult createData(T t);
    
    /**
     * Batch insert.
     * @param list 
     * @return 
     */
    public WriteResult createData(List<T> list);
    
    /**
     * Save an entity to mongoDB. 
     * If no id in it, then insert the entity.
     * Else, check the id type, to confirm do save or insert.
     * @param t 
     * @return 
     */
    public WriteResult updateData(T t);
    
    
    /**
     * Drop the collection. 
     * It will automatically drop all indexes from this collection.
     */
    public void dropCollection();
    
    /**
     * Remove an entity.
     * @param t 
     * @return 
     */
    public WriteResult deleteData(T t);

    /**
     * Remove an entity by id.
     * @param id 
     * @return 
     */
    public WriteResult deleteData(String id);
    
    /**
     * Batch remove by id.
     * @param idList
     * @return 
     */
    public WriteResult deleteData(List<String> idList);
    
    /**
     * Remove by condition.
     * @param key the condition field
     * @param value the condition value
     * @return 
     */
    public WriteResult deleteData(String key, Object value);

    public WriteResult deleteData(IQuery<T> query);

    public boolean exists(String id);
 
    public boolean exists(String key, Object value);
  
    public T findOne();
   
    public T findOne(String id);
   
    public T findOne(String key, Object value);

    
    public List<T> findAll();
     
    public List<T> findAll(String orderBy);
 
    public List<T> findAll(int pageNum, int pageSize);
    
    
    public List<T> findAll(String orderBy, int pageNum, int pageSize);
    
    public List distinct(String key);

    
    public long count();
    
  
    public long count(String key, Object value);
    
    public DBCollection getCollection();

    public Class<T> getEntityClass();

    public DBObject getKeyFields();

    public List<ListenerDef> getListenerList();
    
    public IQuery<T> query();
    
}
