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
 
   
    public void setWriteConcern(WriteConcern concern);
    
    public WriteConcern getWriteConcern();
    
    public void addListener(ListenerDef listener);
    
    
    
    public void notifyCreate(final EntityDefinition entity);
     
    public void notifyRetrieve(final EntityDefinition entity);
    
    
    public void notifyUpdated(final EntityDefinition entity);
     
    public void notifyDeleted(final EntityDefinition entity);
     
    public WriteResult createData(T t);
    
    
    public WriteResult createData(List<T> list);
    
     
    public WriteResult updateData(T t);
    
    
    
    public void dropCollection();
    
     
    public WriteResult deleteData(T t);

     
    public WriteResult deleteData(String id);
    
    
    public WriteResult deleteData(List<String> idList);
    
     
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
