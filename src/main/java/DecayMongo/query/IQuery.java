package DecayMongo.query;

import java.util.Collection;
import java.util.List;

import com.mongodb.DBObject;

/**
 * 
 * 2014年10月29日
 * @author decaywood
 *
 */
public interface IQuery <T> {

    
    public IQuery<T> is(String key, Object value);
    
    public IQuery<T> notEquals(String key, Object value);
    
    public IQuery<T> or(IQuery... querys);
    
    public IQuery<T> and(IQuery<T>... querys);
    
    public IQuery<T> greaterThan(String key, Object value);
    
    public IQuery<T> greaterThanEquals(String key, Object value);
    
    public IQuery<T> lessThan(String key, Object value);
    
    public IQuery<T> lessThanEquals(String key, Object value);
    
    public IQuery<T> in(String key, List<?> list);
    
    public IQuery<T> in(String key, Object... values);
    
    public IQuery<T> notIn(String key, List<?> list);
    
    public IQuery<T> notIn(String key, Object... values);
    
    public IQuery<T> all(String key, List<?> list);
    
    public IQuery<T> all(String key, Object... values);
    
    public IQuery<T> regex(String key, String regex);
    
    public IQuery<T> size(String key, int value);
    
    public IQuery<T> mod(String key, int divisor, int remainder);
    
    public IQuery<T> existsField(String key);
    
    public IQuery<T> notExistsField(String key);
    
    public IQuery<T> where(String whereStr);
    
    public IQuery<T> withinCenter(String key, double x, double y, double radius);
    
    public IQuery<T> withinBox(String key, double x1, double y1, double x2, double y2);
    
    public IQuery<T> near(String key, double x, double y);

    public IQuery<T> near(String key, double x, double y, double maxDistance);
    
    public IQuery<T> slice(String key, long num);
    
    public IQuery<T> slice(String key, long begin, long length);
    
    public IQuery<T> returnFields(String... fieldNames);
    
    public IQuery<T> notReturnFields(String... fieldNames);
    
    public IQuery<T> sort(String orderBy);
    
    public IQuery<T> pageNumber(int pageNumber);
    
    public IQuery<T> pageSize(int pageSize);
 
    public T result();
    
    public List<T> results();
    
    public Collection<T> results(Class<?> implementClass);
    
    public long count();
    
    public boolean exists();
    
    public List distinct(String key);

    public DBObject getCondition(); 
}
