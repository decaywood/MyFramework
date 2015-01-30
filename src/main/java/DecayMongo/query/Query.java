package DecayMongo.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

 









import org.decaywood.EntityDefinition;
import org.decaywood.MongoDBconstant;
import org.decaywood.annotations.ID;
import org.decaywood.cache.FieldsCache;
import org.decaywood.dao.BasicDao;
import org.decaywood.utils.AccessUtil;
import org.decaywood.utils.MappingUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * 2014年11月3日
 * @author decaywood
 *
 */
public class Query<T> implements IQuery<T> {

 private final static Logger log = Logger.getLogger(Query.class.getClass().getName());
    
    private BasicDao<T> dao;
    
    private DBObject slices;
    private DBObject fields;
    private boolean fieldsSpecified = false;
    
    private final DBObject condition = new BasicDBObject();
    private String orderBy;
    private int pageNumber = 0;   
    private int pageSize = 0;   
    
    private ID idAnnotation;
    public Query(BasicDao<T> dao){
        this.dao = dao;
    }
    
  
    
    @Override
    public IQuery<T> is(String key, Object value){
        appendEquals(key, null, value);
        return this;
    }
    
    @Override
    public IQuery<T> notEquals(String key, Object value){
        appendEquals(key, MongoDBconstant.NE, value);
        return this;
    }
    
    @Override
    public IQuery<T> or(IQuery... querys){
        List<DBObject> list = (List<DBObject>)condition.get(MongoDBconstant.OR);
        if(list == null){
            list = new ArrayList<DBObject>();
            condition.put(MongoDBconstant.OR, list);
        }
        for(IQuery<T> q : querys){
            list.add(q.getCondition());
        }
        return this;
    }
    
    @Override
    public IQuery<T> and(IQuery<T>... querys){
        List<DBObject> list = (List<DBObject>)condition.get(MongoDBconstant.AND);
        if(list == null){
            list = new ArrayList();
            condition.put(MongoDBconstant.AND, list);
        }
        for(IQuery<T> q : querys){
            list.add(q.getCondition());
        }
        return this;
    }
    
    
    @Override
    public IQuery<T> greaterThan(String key, Object value){
        appendThan(key, MongoDBconstant.GT, value);
        return this;
    }
    
    
    @Override
    public IQuery<T> greaterThanEquals(String key, Object value){
        appendThan(key, MongoDBconstant.GTE, value);
        return this;
    }
    
    
    @Override
    public IQuery<T> lessThan(String key, Object value){
        appendThan(key, MongoDBconstant.LT, value);
        return this;
    }
    
    
    @Override
    public IQuery<T> lessThanEquals(String key, Object value){
        appendThan(key, MongoDBconstant.LTE, value);
        return this;
    }
    
    @Override
    public IQuery<T> in(String key, List<?> list){
        if(list == null){
            list = Collections.emptyList();
        }
        return in(key, list.toArray());
    }
    
    @Override
    public IQuery<T> in(String key, Object... values){
        appendIn(key, MongoDBconstant.IN, values);
        return this;
    }
    
    @Override
    public IQuery<T> notIn(String key, List<?> list){
        if(list == null){
            list = Collections.emptyList();
        }
        return notIn(key, list.toArray());
    }
    
    
    @Override
    public IQuery<T> notIn(String key, Object... values){
        appendIn(key, MongoDBconstant.NIN, values);
        return this;
    }
    
    
    @Override
    public IQuery<T> all(String key, List<?> list){
        if(list == null){
            list = Collections.emptyList();
        }
        return all(key, list.toArray());
    }
    
    
    @Override
    public IQuery<T> all(String key, Object... values){
        append(key, MongoDBconstant.ALL, values);
        return this;
    }
    


    @Override
    public IQuery<T> regex(String key, String regex){
        append(key, null, Pattern.compile(regex));
        return this;
    }
    
    
    @Override
    public IQuery<T> size(String key, int value){
        append(key, MongoDBconstant.SIZE, value);
        return this;
    }
    
    
    @Override
    public IQuery<T> mod(String key, int divisor, int remainder){
        append(key, MongoDBconstant.MOD, new int[]{divisor, remainder});
        return this;
    }
    
    
    @Override
    public IQuery<T> existsField(String key){
        append(key, MongoDBconstant.EXISTS, Boolean.TRUE);
        return this;
    }
    
    
    @Override
    public IQuery<T> notExistsField(String key){
        append(key, MongoDBconstant.EXISTS, Boolean.FALSE);
        return this;
    }
    
    
    @Override
    public IQuery<T> where(String whereStr){
        append(MongoDBconstant.WHERE, null, whereStr);
        return this;
    }
    
    
    @Override
    public IQuery<T> withinCenter(String key, double x, double y, double radius){
        DBObject dbo = new BasicDBObject(MongoDBconstant.CENTER, new Object[]{new Double[]{x, y}, radius});
        append(key, MongoDBconstant.WITHIN, dbo);
        return this;
    }
    
    
    @Override
    public IQuery<T> withinBox(String key, double x1, double y1, double x2, double y2){
        DBObject dbo = new BasicDBObject(MongoDBconstant.BOX, new Object[]{new Double[]{x1, y1}, new Double[]{x2, y2} });
        append(key, MongoDBconstant.WITHIN, dbo);
        return this;
    }
    
    
    @Override
    public IQuery<T> near(String key, double x, double y){
        append(key, MongoDBconstant.NEAR, new Double[]{x, y});
        return this;
    }

    
    @Override
    public IQuery<T> near(String key, double x, double y, double maxDistance){
        append(key, MongoDBconstant.NEAR, new Double[]{x, y, maxDistance});
        return this;
    }
    
    
    @Override
    public IQuery<T> slice(String key, long num){
        DBObject dbo = new BasicDBObject(MongoDBconstant.SLICE, num);
        return addSlice(key, dbo);
    }
    
    
    @Override
    public IQuery<T> slice(String key, long begin, long length){
        DBObject dbo = new BasicDBObject(MongoDBconstant.SLICE, new Long[]{begin, length});
        return addSlice(key, dbo);
    }
    
    
    
    
    @Override
    public IQuery<T> returnFields(String... fieldNames){
        return specifyFields(1, fieldNames);
    }
    
    
    @Override
    public IQuery<T> notReturnFields(String... fieldNames){
        return specifyFields(0, fieldNames);
    }
    
    
    
    @Override
    public IQuery<T> sort(String orderBy){
        this.orderBy = orderBy;
        return this;
    }
    
    
    @Override
    public IQuery<T> pageNumber(int pageNumber){
        this.pageNumber = pageNumber;
        return this;
    }
    
    
    @Override
    public IQuery<T> pageSize(int pageSize){
        this.pageSize = pageSize;
        return this;
    }
    
    
    @Override
    public T result(){
        try{
            checkSingle();
        }catch(Exception e){
            log.info(e.getMessage());
        }
        DBObject dbo = null;
        DBCollection coll = dao.getCollection();
        if(fieldsSpecified){
            dbo = coll.findOne(condition, fields);
        }else if(slices != null){
            dbo = coll.findOne(condition, slices);
        }else{
            dbo = coll.findOne(condition);
        }
        return MappingUtil.fromDBObject(dao.getEntityClass(), dbo);
    }
    
    
    @Override
    public List<T> results(){
        DBCursor cursor = null;
        DBCollection coll = dao.getCollection();
        if(fieldsSpecified){
            cursor = coll.find(condition, fields);
        }else{
            cursor = coll.find(condition, dao.getKeyFields());
        }
        if(orderBy != null){
            cursor.sort(MappingUtil.getSort(orderBy));
        }
        if(pageNumber>0 && pageSize>0){
            cursor.skip((pageNumber-1)*pageSize).limit(pageSize);
        }
        return MappingUtil.toList(dao.getEntityClass(), cursor);
    }
    
    
    @Override
    public Collection<T> results(Class<?> implementClass) {
        DBCursor cursor = null;
        DBCollection coll = dao.getCollection();
        if(fieldsSpecified){
            cursor = coll.find(condition, fields);
        }else{
            cursor = coll.find(condition, dao.getKeyFields());
        }
        if(orderBy != null){
            cursor.sort(MappingUtil.getSort(orderBy));
        }
        if(pageNumber>0 && pageSize>0){
            cursor.skip((pageNumber-1)*pageSize).limit(pageSize);
        }
        return MappingUtil.toRealCollection(dao.getEntityClass(), cursor, implementClass);
    }
   
    
    @Override
    public long count(){
        return dao.getCollection().count(condition);
    }
    
    
    @Override
    public boolean exists(){
        DBObject dbo = dao.getCollection().findOne(condition);
        return dbo != null;
    }
    
    
    @Override
    public List<?> distinct(String key){
        return dao.getCollection().distinct(key, condition);
    }

    
    @Override
    public DBObject getCondition() {
        return condition;
    }
    
    
    private IQuery<T> specifyFields(int value, String... fieldNames){
        if(fields == null){
            fields = new BasicDBObject();
        }
        for(String field : fieldNames){
            
            if(fields.get(field)==null){
                fields.put(field, value);
            }
        }
        fieldsSpecified = true;
        return this;
    }
    

    
    private void checkSingle() throws Exception{
        if(orderBy!=null || pageNumber!=0 || pageSize!=0){
            throw new Exception("You should use results() to get a list, when you use sorting or pagination");
        }
    }
    
    
    
    private ID getIDAnnotation(){
        if(idAnnotation == null){
            Field idField = null;
            try {
                idField = FieldsCache.getInstance().getIdField(dao.getEntityClass());
                idAnnotation = idField.getAnnotation(ID.class);
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        }
        return idAnnotation;
    }
    
    private Object getDBID(String ID){
        ID annotations = getIDAnnotation();
        return annotations.type().getConvertedID((String)ID);
    }
    
    private void appendEquals(String key, String op, Object value){
        Class<T> clazz = dao.getEntityClass();
        if(key.equals(MongoDBconstant.ID)){
            Object dbId = getDBID((String)value);
            append(MongoDBconstant.ID, op, dbId);
        }
        else if(key.indexOf(".")!=-1){
            append(key, op, value);
        }
        else{
            Field field = null;
            try{
                field = FieldsCache.getInstance().getField(clazz, key);
            }catch(Exception e){
                log.info(e.getMessage());
            }
            if(field.isAnnotationPresent(ID.class)){
                Object dbId = getDBID((String)value);
                append(MongoDBconstant.ID, op, dbId);
            }
            else if(value instanceof EntityDefinition){
                EntityDefinition entity = (EntityDefinition)value;
                Object dbReference = AccessUtil.convertToDbReference(clazz, key, entity.getClass(), entity.getID());
                append(key, op, dbReference);
            }
            else{
                append(key, op, value);
            }
        }
    }
    
    private void appendThan(String key, String op, Object value){
        Class<T> clazz = dao.getEntityClass();
        if(key.equals(MongoDBconstant.ID)){
            Object dbId = getDBID((String)value);
            append(MongoDBconstant.ID, op, dbId);
        }
        else if(key.indexOf(".")!=-1){
            append(key, op, value);
        }
        else{
            Field field = null;
            try{
                field = FieldsCache.getInstance().getField(clazz, key);
            }catch(Exception e){
                log.info(e.getMessage());
            }
            if(field.isAnnotationPresent(ID.class)){
                Object dbId = getDBID((String)value);
                append(MongoDBconstant.ID, op, dbId);
            }
            else{
                append(key, op, value);
            }
        }
    }
    
    private void appendIn(String key, String op, Object... values){
        if(key.equals(MongoDBconstant.ID)){
            append(MongoDBconstant.ID, op, toIds(values));
        }
        else if(key.indexOf(".")!=-1){
            append(key, op, values);
        }
        else{
            Field field = null;
            try{
                Class<T> clazz = dao.getEntityClass();
                field = FieldsCache.getInstance().getField(clazz, key);
            }catch(Exception e){
                log.info(e.getMessage());
            }
            if(field.isAnnotationPresent(ID.class)){
                append(MongoDBconstant.ID, op, toIds(values));
            }
            else if(values.length != 0 && values[0] instanceof EntityDefinition){
                append(key, op, toReferenceList(key, values));
            }
            else{
                append(key, op, values);
            }
        }
    }
    
    private void append(String key, String op, Object value){
        if(op == null) {
            condition.put(key, value);
            return;
        }
        Object obj = condition.get(key);
        DBObject dbo = null;
        if(!(obj instanceof DBObject)) {
            dbo = new BasicDBObject(op, value);
            condition.put(key, dbo);
        } else {
            dbo = (DBObject)condition.get(key);
            dbo.put(op, value);
        }
    }
    
    private List<Object> toIds(Object... values){
        List<Object> idList = new ArrayList<Object>();
        Class<T> clazz = dao.getEntityClass();
        int len = values.length;
        for(int i=0; i<len; i++){
            if(values[i] != null){
                Object dbId = getDBID((String)values[i]);
                idList.add(dbId);
            }
        }
        return idList;
    }
    
    private List<Object> toReferenceList(String key, Object... values){
        List<Object> refList = new ArrayList<Object>();
        Class<T> clazz = dao.getEntityClass();
        int len = values.length;
        for(int i=0; i<len; i++){
            if(values[i] != null){
                EntityDefinition entity = (EntityDefinition)values[i];
                Object refObj = AccessUtil.convertToDbReference(clazz, key, entity.getClass(), entity.getID());
                refList.add(refObj);
            }
        }
        return refList;
    }
    
    
    private IQuery<T> addSlice(String key, DBObject dbo){
        if(slices == null){
            slices = new BasicDBObject();
        }
        slices.put(key, dbo);
        dao.getKeyFields().put(key, dbo);
        if(fields == null){
            fields = new BasicDBObject();
        }
        fields.put(key, dbo);
        return this;
    }



    
}
