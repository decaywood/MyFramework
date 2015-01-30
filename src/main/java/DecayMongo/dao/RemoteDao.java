package DecayMongo.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import DecayMongo.query.IQuery;
import DecayMongo.utils.MappingUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceCommand.OutputType;
import com.mongodb.MapReduceOutput;


/**
 * 2014年10月31日
 * @author decaywood
 *
 */
public class RemoteDao<T> extends BasicDao<T> {

    public RemoteDao(Class<T> clazz){
        super(clazz);
    }
    
    public double max(String key){
        return max(key, new BasicDBObject());
    }
    
    public double max(String key, IQuery<T> query){
        return max(key, query.getCondition());
    }
    
    private double max(String key, DBObject query){
        if(!this.exists(query)){
            return 0;
        }
        StringBuilder map = new StringBuilder("function(){emit('");
        map.append(key);
        map.append("', {'value':this.");
        map.append(key);
        map.append("});}");
        String reduce = "function(key, values){var max=values[0].value; for(var i=1;i<values.length; i++){if(values[i].value>max){max=values[i].value;}} return {'value':max}}";
        Iterable<DBObject> results = null;
        try{
            results = mapReduce(map.toString(), reduce, query);
        }catch(Exception e){
            log.info(e.getMessage());
        }
        DBObject result = results.iterator().next();
        DBObject dbo = (DBObject)result.get("value");
        return Double.parseDouble(dbo.get("value").toString());
    }
    
    public double min(String key){
        return min(key, new BasicDBObject());
    }
    
    public double min(String key, IQuery<T> query){
        return min(key, query.getCondition());
    }
    
    private double min(String key, DBObject query){
        if(!this.exists(query)){
            return 0;
        }
        StringBuilder map = new StringBuilder("function(){emit('");
        map.append(key);
        map.append("', {'value':this.");
        map.append(key);
        map.append("});}");
        String reduce = "function(key, values){var min=values[0].value; for(var i=1;i<values.length; i++){if(values[i].value<min){min=values[i].value;}} return {'value':min}}";
        Iterable<DBObject> results = null;
        try{
            results = mapReduce(map.toString(), reduce, query);
        }catch(Exception e){
            log.info(e.getMessage());
        }
        DBObject result = results.iterator().next();
        DBObject dbo = (DBObject)result.get("value");
        return Double.parseDouble(dbo.get("value").toString());
    }
    
    public double sum(String key){
        return sum(key, new BasicDBObject());
    }
    
    public double sum(String key, IQuery<T> query){
        return sum(key, query.getCondition());
    }
    
    private double sum(String key, DBObject query){
        if(!this.exists(query)){
            return 0;
        }
        StringBuilder map = new StringBuilder("function(){emit('");
        map.append(key);
        map.append("', {'value':this.");
        map.append(key);
        map.append("});}");
        String reduce = "function(key, values){var sum=0; for(var i=0;i<values.length; i++){sum+=values[i].value;} return {'value':sum}}";
        Iterable<DBObject> results = null;
        try{
            results = mapReduce(map.toString(), reduce, query);
        }catch(Exception e){
            log.info(e.getMessage());
        }
        DBObject result = results.iterator().next();
        DBObject dbo = (DBObject)result.get("value");
        return Double.parseDouble(dbo.get("value").toString());
    }
    
    public double average(String key){
        return average(key, new BasicDBObject());
    }
    
    public double average(String key, IQuery<T> query){
        return average(key, query.getCondition());
    }
    
    private double average(String key, DBObject query){
        long count = collection.count(query);
        if(count == 0){
            return 0;
        }
        double sum = this.sum(key, query);
        return sum / count;
    }
    
    public Iterable<DBObject> mapReduce(MapReduceCommand cmd){
        MapReduceOutput output = collection.mapReduce(cmd);
        return output.results();
    }
    
    public Iterable<DBObject> mapReduce(String map, String reduce){
        MapReduceOutput output = collection.mapReduce(map, reduce, null, OutputType.INLINE, null);
        return output.results();
    }
    
    public Iterable<DBObject> mapReduce(String map, String reduce, IQuery<T> query){
        return mapReduce(map, reduce, query.getCondition());
    }
    
    private Iterable<DBObject> mapReduce(String map, String reduce, DBObject query){
        MapReduceOutput output = collection.mapReduce(map, reduce, null, OutputType.INLINE, query);
        return output.results();
    }
    
    public Iterable<DBObject> mapReduce(
            String map,
            String reduce, 
            String outputTarget, 
            MapReduceCommand.OutputType outputType,
            String orderBy,
            IQuery<T> query){
        return mapReduce(map, reduce, outputTarget, outputType, orderBy, query.getCondition());
    }
    
    private synchronized Iterable<DBObject> mapReduce(
            String map,
            String reduce, 
            String outputTarget, 
            MapReduceCommand.OutputType outputType, 
            String orderBy, 
            DBObject query){
        MapReduceOutput output = collection.mapReduce(map, reduce, outputTarget, outputType, query);
        DBCollection c = output.getOutputCollection();
        DBCursor cursor = null;
        if(orderBy != null){
            cursor = c.find().sort(MappingUtil.getSort(orderBy));
        }else{
            cursor = c.find();
        }
        List<DBObject> list = new ArrayList<DBObject>();
        for(Iterator<DBObject> it = cursor.iterator(); it.hasNext(); ){
            list.add(it.next());
        }
        return list;
    }
    
    public Iterable<DBObject> mapReduce(
            String map,
            String reduce, 
            String outputTarget, 
            MapReduceCommand.OutputType outputType,
            String orderBy,
            int pageNum,
            int pageSize,
            IQuery<T> query){
        return mapReduce(map, reduce, outputTarget, outputType, orderBy, pageNum, pageSize, query.getCondition());
    }
    
    private synchronized Iterable<DBObject> mapReduce(
            String map,
            String reduce,
            String outputTarget,
            MapReduceCommand.OutputType outputType, 
            String orderBy, 
            int pageNum,
            int pageSize,
            DBObject query){
        MapReduceOutput output = collection.mapReduce(map, reduce, outputTarget, outputType, query);
        DBCollection c = output.getOutputCollection();
        DBCursor cursor = null;
        cursor = orderBy != null ? c.find().sort(MappingUtil.getSort(orderBy)).skip((pageNum-1)*pageSize).limit(pageSize)
                                 : c.find().skip((pageNum-1)*pageSize).limit(pageSize);
        List<DBObject> list = new ArrayList<DBObject>();
        for(Iterator<DBObject> it = cursor.iterator(); it.hasNext(); ){
            list.add(it.next());
        }
        cursor.close();
        return list;
    }
    
    
    //TODO
//    public AggregateOperation<T> aggregate(){
//        return new AggregateOperation<T>(collection);
//    }
    
    
    private boolean exists(DBObject query){
        return collection.findOne(query) != null;
    }

    
}
