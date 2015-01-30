package DecayMongo.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 2014年12月3日
 * @author decaywood
 *
 *  用这个类有两大好处：一是它本身已经实现了按照访问顺序的存储，
 *  也就是说，最近读取的会放在最前面，最最不常读取的会放在最后（当然，它也可以实现按照插入顺序存储）。
 *  第二，LinkedHashMap本身有一个方法用于判断是否需要移除最不常读取的数，
 *  但是，原始方法默认不需要移除（这是，LinkedHashMap相当于一个linkedlist），
 *  所以，我们需要override这样一个方法，使得当缓存里存放的数据个数超过规定个数后，
 *  就把最不常用的移除掉。LinkedHashMap的API写得很清楚，推荐大家可以先读一下。
 *  
 *  
 */
public class LRUCache<K, V> {
    
    private static final float hashTableLoadFactor = 0.75f;  
    
    private LinkedHashMap<K,V> map;  
    private int cacheSize;  
      
    /** 
    *  固定大小的缓存工具，用于保存最近使用过的数据，用于cache包中池的策略模式中
    *  
    */  
    public LRUCache (int cacheSize) {  
        
       this.cacheSize = cacheSize;  
       
       /**
        * 这一行的处理是尽可能的节省空间，由于LRUCache是固定大小的，而父类HashMap中的负载因子会
        * 使实际的可用空间缩小( threshold = (int)(capacity * loadFactor); )
        * 所以，需要扩增capacity传入Hashmap构造器，当然，LRUcache的cacheSize不变
        * 这样的好处是，不会因为数据的增多触发Hashmap的resize()方法，造成不必要的复制操作
        * 浪费cpu时间
        */
       int hashTableCapacity = (int)Math.ceil(cacheSize / hashTableLoadFactor) + 1;  
       
       /**
        * LinkedHashMap是HashMap的一个子类，它保留插入的顺序，
        * 如果需要输出的顺序和输入时的相同，那么就选用LinkedHashMap。
        * LinkedHashMap是Map接口的哈希表和链接列表实现，具有可预知的迭代顺序。
        * 此实现提供所有可选的映射操作，并允许使用null值和null键。
        * 此类不保证映射的顺序，特别是它不保证该顺序恒久不变。
        * LinkedHashMap实现与HashMap的不同之处在于，后者维护着一个运行于所有条目的双重链接列表。
        * 此链接列表定义了迭代顺序，该迭代顺序可以是插入顺序或者是访问顺序。
        * 注意，此实现不是同步的。如果多个线程同时访问链接的哈希映射，
        * 而其中至少一个线程从结构上修改了该映射，则它必须保持外部同步。
        * 根据链表中元素的顺序可以分为：按插入顺序的链表，和按访问顺序(调用get方法)的链表。  
        * 默认是按插入顺序排序，如果指定按访问顺序排序，那么调用get方法后，会将这次访问的元素移至链表尾部，
        * 不断访问可以形成按访问顺序排序的链表。  可以重写removeEldestEntry方法返回true值指定插入元素时移除最老的元素。
        * 第三个参数：
        * true表示按照访问顺序迭代，false时表示按照插入顺序
        */
       map = new LinkedHashMap<K,V>(hashTableCapacity, hashTableLoadFactor, true) {  
          
        private static final long serialVersionUID = 1L;

        
        @Override protected boolean removeEldestEntry (Map.Entry<K,V> eldest) {  
             return size() > LRUCache.this.cacheSize; 
             
        
        }  
       }; 
       
    }  
      
    
    public synchronized V get (K key) {  
       return map.get(key); 
       
    }  
      
    
    public synchronized V put (K key, V value) {  
       return map.put (key, value); 
    }  
      
   
    public synchronized void clear() {  
       map.clear();
    }  
      
    
    public synchronized int usedEntries() {  
       return map.size(); 
    }  
      
    
    public synchronized Collection<Map.Entry<K,V>> getAll() {  
       return new ArrayList<Map.Entry<K,V>>(map.entrySet()); 
    }  
        
      
          
      
} 
 


