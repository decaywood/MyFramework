package org.decaywood.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import org.decaywood.Annotations;
import org.decaywood.EntityDefinition;
import org.decaywood.MongoConnection;
import org.decaywood.MongoDBconstant;
import org.decaywood.annotations.Entity;
import org.decaywood.annotations.GroupReference;
import org.decaywood.annotations.ID;
import org.decaywood.annotations.Reference;
import org.decaywood.annotations.Split;
import org.decaywood.cache.FieldsCache;
import org.decaywood.listener.CreateListener;
import org.decaywood.listener.DeleteListener;
import org.decaywood.listener.ListenerDef;
import org.decaywood.listener.RetrieveListener;
import org.decaywood.listener.UpdateListener;
import org.decaywood.query.IQuery;
import org.decaywood.query.Query;
import org.decaywood.utils.AccessUtil;
import org.decaywood.utils.MappingUtil;
import org.decaywood.utils.StringUtil;
import org.decaywood.utils.ThreadManager;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

/**
 * 
 * 2014年10月29日
 * @author decaywood
 *
 */
public class BasicDao<T> implements DaoDef<T>{

    protected final static Logger log = Logger.getLogger(BasicDao.class.getName());
    
    protected DBCollection collection;
    protected Class<T> clazz;
    protected DBObject keys;   
    /**
     * DB对象可以通过Mongo.get方法获得，代表了和数据库的一个连接。
     * 默认情况下，当执行完数据库的查询或者更新操作后，连接将自动回到连接池中。
     * 
     * 注意： 如果使用集群进行数据库操作，可能会存在不同步的问题
     * 官方解释如下：
     * 
     * Additionally in the case of a replica set with slaveOk option turned on,
     *  the read operations will be distributed evenly across all slaves.
     *   This means that within the same thread, a write followed by a read may be 
     *   sent to different servers (master then slave). 
     *   In turn the read operation may not see the data just written since replication is asynchronous. 
     *   If you want to ensure complete consistency in a “session” (maybe an http request), 
     *   you would want the driver to use the same socket, which you can achieve by using a “consistent request”.
     *    Call requestStart() before your operations and requestDone() to release the connection back to the pool.
     *    
     */
    private DB database;
    private final List<ListenerDef> listenerList;
    
    public BasicDao(Class<T> clazz) {
        this.clazz = clazz;
        database= MongoConnection.getInstance().getDB();
        listenerList = new ArrayList<ListenerDef>();
        keys = MappingUtil.getKeyFields(clazz);
        processSplit();
        processListener();
    }
    
    
    private int getReferenceCascade(Field field, List<Field> fields){
        if(field == null) return Annotations.CASCADE_DEFAULT;
        Reference reference = field.getAnnotation(Reference.class);
        if(reference == null) return Annotations.CASCADE_DEFAULT;
        fields.add(field);
        return reference.cascade();
    }
    
    
    private int getGroupReferenceCascade(Field field, List<Field> fields){
        if(field == null) return Annotations.CASCADE_DEFAULT;
        GroupReference reference = field.getAnnotation(GroupReference.class);
        if(reference == null) return Annotations.CASCADE_DEFAULT;
        fields.add(field);
        return reference.cascade();
    }
    
    
    private void processListener(){
        Field[] fields = FieldsCache.getInstance().get(clazz);
        int listenerBit = Annotations.CASCADE_DEFAULT;
        List<Field> groupRefFields = new ArrayList<Field>();
        List<Field> refFields = new ArrayList<Field>();
        for(Field field : fields){
//            if((listenerBit & 0x1111) == 0x1111) break;
            int refCascade = getReferenceCascade(field, refFields);
            int groupRefCascade = getGroupReferenceCascade(field, groupRefFields);
            listenerBit |= refCascade | groupRefCascade;
        }
        if((listenerBit & Annotations.CASCADE_CREATE) != 0)
            addListener(new CreateListener());
        if((listenerBit & Annotations.CASCADE_DELETE) != 0)
            addListener(new DeleteListener(groupRefFields, refFields));
        if((listenerBit & Annotations.CASCADE_UPDATE) != 0)
            addListener(new UpdateListener());
        if((listenerBit & Annotations.CASCADE_RETRIEVE) != 0)
            addListener(new RetrieveListener());
    }
    
    private void processSplit(){
        Entity annotation = clazz.getAnnotation(Entity.class);
        if(annotation == null)
            log.info("please add @Entity");
        String name = MappingUtil.getEntityName(clazz);
        String suffix = annotation.splitSuffix();
        Split split = annotation.split();
        String extend = split.getFormat();
        extend = StringUtil.isEmpty(extend) ? "-" + suffix : "-" + extend;
        extend = StringUtil.isEmpty(extend) ? "" : extend;
        initCollection(name + extend);
    }
    
    private void initCollection(String name){
        int nameLength = Math.max(0, name.length() - 1);
        name = name.endsWith("-") ? name.substring(0, nameLength) : name;
        Entity entity = clazz.getAnnotation(Entity.class);
        
        if(entity.capped() && !database.collectionExists(name)){
            
            DBObject options = new BasicDBObject("capped", true);
            long capSize = entity.capSize();
            long capMax = entity.capMax();
            
            if(capSize != Annotations.DEFAULT_CAP_SIZE)
                options.put("size", capSize);
            if(capMax != Annotations.DEFAULT_CAP_MAX)
                options.put("max", capMax);
            collection = database.createCollection(name, options);
        }else
            collection = database.getCollection(name);
    }

  
    @Override
    public void addListener(ListenerDef listener) {
        if(listener == null) return;
        this.listenerList.add(listener);
    }

    @Override
    public void notifyCreate(final EntityDefinition entity) {
        Executor executor = ThreadManager.getInstance().getExecutor();
        for(final ListenerDef listener : listenerList){
           executor.execute(new Runnable() {
                @Override
                public void run() {
                    listener.createAction(entity);
                }
           });
        }
    }

    @Override
    public void notifyRetrieve(final EntityDefinition entity) {
        Executor executor = ThreadManager.getInstance().getExecutor();
        for(final ListenerDef listener : listenerList){
           executor.execute(new Runnable() {
                @Override
                public void run() {
                    listener.retrieveAction(entity); 
                }
           });
        }
    }

    @Override
    public void notifyUpdated(final EntityDefinition entity) {
        Executor executor = ThreadManager.getInstance().getExecutor();
        for(final ListenerDef listener : listenerList){
           executor.execute(new Runnable() {
                @Override
                public void run() {
                    listener.updateAction(entity);;
                }
           });
        }
    }

    @Override
    public void notifyDeleted(final EntityDefinition entity) {
        Executor executor = ThreadManager.getInstance().getExecutor();
        for(final ListenerDef listener : listenerList){
           executor.execute(new Runnable() {
                @Override
                public void run() {
                    listener.deleteAction(entity); 
                }
           });
        }
    }

    @Override
    public WriteResult createData(T insertObj) {
        DBObject dbo = MappingUtil.toDBObject(insertObj);
        WriteResult writeResult = collection.insert(dbo, database.getWriteConcern());
        String id = dbo.get(MongoDBconstant.ID).toString();
        EntityDefinition entity = (EntityDefinition)insertObj;
        entity.setId(id);
        notifyCreate(entity);
        return writeResult;
    }

    @Override
    public WriteResult createData(List<T> list) {
        List<DBObject> dbObjects = new ArrayList<DBObject>();
        for(T element : list){
            DBObject object = MappingUtil.toDBObject(element);
            dbObjects.add(object);
        }
        WriteResult writeResult = collection.insert(dbObjects, database.getWriteConcern());
        int len = dbObjects.size();
        for(int i=0; i<len; i++){
            String id = dbObjects.get(i).get(MongoDBconstant.ID).toString();
            EntityDefinition entity = (EntityDefinition)(list.get(i));
            entity.setId(id);
            notifyCreate(entity);
        }
        return writeResult;
    }

    @Override
    public WriteResult updateData(T t) {
        EntityDefinition entity = (EntityDefinition)t;
        boolean cantUpdate = StringUtil.isEmpty(entity.getID()) 
        || !exists(MongoDBconstant.ID, entity.getID());
        if(cantUpdate)
            return createData(t);
        notifyUpdated(entity);
        return collection.save(MappingUtil.toDBObject(entity), database.getWriteConcern());
    }

    

    @Override
    public void dropCollection() {
        if(!listenerList.isEmpty()){
            //这一步仅仅是为了通知监听器
            List<T> list = findAll();
            for(T t : list){
                deleteData(t);
            }
        }
        collection.drop();
        collection.dropIndexes();
    }

    @Override
    public WriteResult deleteData(T t) {
        EntityDefinition entity = (EntityDefinition)t;
        return deleteData(entity.getID());
    }

    @Override
    public WriteResult deleteData(String id) {
        if(!listenerList.isEmpty()){
            EntityDefinition entity = (EntityDefinition)findOne(id);
            notifyDeleted(entity);
        }
        WriteResult writeResult = null;
        ID idAnnotation = getIDAnnotation();
        DBObject dbo = new BasicDBObject(MongoDBconstant.ID, idAnnotation.type().getConvertedID(id));
        writeResult = collection.remove(dbo, database.getWriteConcern());
        return writeResult;
    }

    @Override
    public WriteResult deleteData(List<String> idList) {
        int len = idList.size();
        Object[] array = new Object[len];
        ID idAnnotation = getIDAnnotation();
        int index = 0; 
        for(String id : idList){
            if(id == null) continue;
            array[index++] = idAnnotation.type().getConvertedID(id);
        }
        DBObject in = new BasicDBObject(MongoDBconstant.IN, array);
        return removeDBObjects(new BasicDBObject(MongoDBconstant.ID, in));
    }
    
    private ID getIDAnnotation(){
        Field idField = null;
        try {
            idField = FieldsCache.getInstance().getIdField(clazz);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return idField.getAnnotation(ID.class);
    }
    
    private WriteResult removeDBObjects(DBObject condition){
        if(!listenerList.isEmpty()){
            DBCursor cursor = collection.find(condition);
            List<T> list = MappingUtil.toList(clazz, cursor);
            for(T t : list){
                notifyDeleted((EntityDefinition)t);
            }
        }
        return collection.remove(condition, database.getWriteConcern());
    }

    @Override
    public WriteResult deleteData(String key, Object value) {
        value = convert(key, value);
        multiDelete(new BasicDBObject(key, value));
        return null;
    }
    
    private WriteResult multiDelete(DBObject condition){
        DBCursor cursor = collection.find(condition);
        List<T> list = MappingUtil.toList(clazz, cursor);
        for(T entity : list){
            notifyDeleted((EntityDefinition) entity);
        }
        return collection.remove(condition, database.getWriteConcern());
    }
    
    private Object convert(String key, Object value){
        if(value instanceof EntityDefinition){
            EntityDefinition be = (EntityDefinition)value;
            return AccessUtil.convertToDbReference(clazz, key, be.getClass(), be.getID());
        }
        FieldsCache fieldsCache = FieldsCache.getInstance();
        if(!(value instanceof DBObject) && fieldsCache.isEmbedOrGroupEmbeded(clazz, key)){
            return MappingUtil.toDBObject(value);
        }
        return value;
    }

    @Override
    public WriteResult deleteData(IQuery<T> query) {
        return removeDBObjects(query.getCondition());
    }

    @Override
    public boolean exists(String id) {
        DBObject query = new BasicDBObject();
        ID idAnnotation = getIDAnnotation();
        query.put(MongoDBconstant.ID, idAnnotation.type().getConvertedID(id));
        return collection.findOne(query) != null;
    }

    @Override
    public boolean exists(String key, Object value) {
        value = convert(key, value);
        DBObject query = new BasicDBObject(key, value);
        return collection.findOne(query) != null;
    }

    @Override
    public T findOne() {
        DBObject result = collection.findOne();
        return MappingUtil.fromDBObject(clazz, result);
    }

    @Override
    public T findOne(String id) {
        DBObject query = new BasicDBObject();
        ID idAnnotation = getIDAnnotation();
        query.put(MongoDBconstant.ID, idAnnotation.type().getConvertedID(id));
        DBObject result = collection.findOne(query);
        return MappingUtil.fromDBObject(clazz, result);
    }

    @Override
    public T findOne(String key, Object value) {
        value = convert(key, value);
        return (T) collection.findOne(value);
    }

    @Override
    public List<T> findAll() {
        DBCursor cursor = collection.find(new BasicDBObject(), keys);
        return MappingUtil.toList(clazz, cursor);
    }

    @Override
    public List<T> findAll(String orderBy) {
        DBObject dbObject = MappingUtil.getSort(orderBy);
        DBCursor cursor = collection.find(new BasicDBObject(), keys).sort(dbObject);
        return MappingUtil.toList(clazz, cursor);
    }

    @Override
    public List<T> findAll(int pageNum, int pageSize) {
        DBCursor cursor = collection.find(new BasicDBObject(), keys).skip((pageNum-1)*pageSize).limit(pageSize);
        return MappingUtil.toList(clazz, cursor);
    }

    @Override
    public List<T> findAll(String orderBy, int pageNum, int pageSize) {
        DBObject dbo = MappingUtil.getSort(orderBy);
        DBCursor cursor = collection.find(new BasicDBObject(), keys).sort(dbo).skip((pageNum-1)*pageSize).limit(pageSize);
        return MappingUtil.toList(clazz, cursor);
    }

    @Override
    public List distinct(String key) {
        return collection.distinct(key);
    }

    @Override
    public long count() {
        return collection.count();
    }

    @Override
    public long count(String key, Object value) {
        return collection.count();
    }

    @Override
    public DBCollection getCollection() {
        return collection;
    }

    @Override
    public Class<T> getEntityClass() {
        return clazz;
    }

    @Override
    public DBObject getKeyFields() {
        return keys;
    }

    @Override
    public List<ListenerDef> getListenerList() {
        return listenerList;
    }

    @Override
    public void setWriteConcern(WriteConcern concern) {
        database.setWriteConcern(concern);
    }

    @Override
    public WriteConcern getWriteConcern() {
        return database.getWriteConcern();
    }

    @Override
    public IQuery<T> query(){
        return new Query<T>(this);
    }
    
    
    
}
