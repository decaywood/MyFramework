package DecayMongo;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.net.SocketFactory;

import org.decaywood.cache.ConcurrentConstructorsCache;
import org.decaywood.cache.ConcurrentDaosCache;
import org.decaywood.cache.ConcurrentFieldsCache;
import org.decaywood.cache.ConstructorsCache;
import org.decaywood.cache.ConstructorsCache.ConstructorsCacheDefinition;
import org.decaywood.cache.DaosCache;
import org.decaywood.cache.DaosCache.DaosCacheDefinition;
import org.decaywood.cache.FieldsCache;
import org.decaywood.cache.FieldsCache.FieldsCacheDefinition;
import org.decaywood.cache.LRUConstructorsCache;
import org.decaywood.cache.LRUDaosCache;
import org.decaywood.cache.LRUFieldsCache;
import org.decaywood.utils.ConfigureUtil;
import org.decaywood.utils.ReaderCreater;
import org.decaywood.utils.ReflectUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mongodb.DBDecoderFactory;
import com.mongodb.DBEncoderFactory;
import com.mongodb.DefaultDBDecoder;
import com.mongodb.DefaultDBEncoder;
import com.mongodb.LazyDBDecoder;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;


/**
 * 
 * 2014年10月24日
 * @author decaywood
 *
 * 解析配置文件
 */
public class ConfigReader {
    
    private HashMap<GlobalName, String> infoMap;
    private ReaderCreater readerCreater;
    private MongoConnection connection;
    
    private static final Logger log = Logger.getLogger(ConfigReader.class.toString());
    
    public ConfigReader(MongoConnection connection){
        this.connection = connection;
        reset();
    }
    
    private void reset(){
        readerCreater = new ReaderCreater();
        infoMap = new HashMap<GlobalName, String>();
    }
    
    public ConfigReader read(String path) throws Exception {
        
        if(log != null)
            log.info(" configure " + " resource path -> " + path);
        
        InputStream stream = ConfigureUtil.getXMLStream(path);
        SAXReader saxReader = readerCreater.createSAXReader();
        
        saxReader.setEntityResolver(ReaderCreater.DEFAULT_ENTITY_RESOLVER);
        Document doc = saxReader.read(stream);
        
        return parseXml(doc);
    }
    
    private ConfigReader parseXml(Document doc){
        
        Element userConfig = doc.getRootElement().element(GlobalName.USER_CONFIG.toString());
        String userName = userConfig.attributeValue(GlobalName.USER_CONFIG_NAME.toString());
        
        if (userName != null) {
            infoMap.put(GlobalName.USER_CONFIG_NAME, userName);
        }
        
        processDatabaseInfo(userConfig, userName);
        processOptions(userConfig, userName);
        processFrameworkConfig(userConfig, userName);
        
        if(log != null)
            log.info(" Configured SessionFactory: " + userName);
        return this;
    }
    
    private void processDatabaseInfo(Element element, String userName){
        
        Iterator<?> iterator = element.elementIterator(GlobalName.DATABASE_INFO.toString());
        
        while (iterator.hasNext()) {
            
            Element dbInfo = (Element)iterator.next();
            String dbInfoName = dbInfo.attributeValue(GlobalName.DATABASE_INFO_NAME.toString());
            GlobalName globalName = GlobalName.getGlobalName(dbInfoName);
            String value = dbInfo.getText().trim();
            if(log != null)
                log.info(dbInfoName + " -> " + value);
            
            infoMap.put(globalName, value);
            
            
        }
        setDatabaseInfo();
    }
    
    private void processOptions(Element element, String name){
        
        Iterator<?> elements = element.elementIterator(GlobalName.OPTIONS.toString());
        boolean noUserDefine = true;
        
        while (elements.hasNext()) {
            
            noUserDefine = false;
            Element option = (Element)elements.next();
            String optionName = option.attributeValue(GlobalName.OPTIONS_NAME.toString());
            GlobalName globalName = GlobalName.getGlobalName(optionName);
            String value = option.getText().trim();
            
            if(log != null)
                log.info("option" + " -> " + value);
            
            infoMap.put(globalName, value);
            
        }
        
        if(noUserDefine) return;
        
        setOptions();
    }
    
    private void processFrameworkConfig(Element element, String name){
        
        Iterator<?> elements = element.elementIterator(GlobalName.CACHE.toString());
        boolean noUserDefine = true;
        
        while (elements.hasNext()) {
            
            noUserDefine = false;
            Element cache = (Element)elements.next();
            
            String typeName = cache.attributeValue(GlobalName.CACHE_TYPE.toString());
            String size = cache.attributeValue(GlobalName.CACHE_SIZE.toString());
            
            GlobalName globalName = GlobalName.getGlobalName(typeName);
            String value = cache.getText().trim();
            
            if(log != null)
                log.info("cache" + " -> " + value);
            
            infoMap.put(globalName, value);
            
            int cacheSize = size != null ? Integer.parseInt(size) : 100;
            cacheInitate(cacheSize);
        }
        
    }
    
    private void cacheInitate(int cacheSize){
        
        if(checkNull(GlobalName.CACHE_TYPE_CONSTRUCTORS_CACHE))
            if(ConstructorsCache.getInstance().isEmpty())
                setConstructorsCache(cacheSize);
        if(checkNull(GlobalName.CACHE_TYPE_DAOS_CACHE))
            if(DaosCache.getInstance().isEmpty())
                setDaosCache(cacheSize);
        if(checkNull(GlobalName.CACHE_TYPE_FIELDS_CACHE))
            if(FieldsCache.getInstance().isEmpty())
                setFieldsCache(cacheSize);
        
    }
    
    private void setConstructorsCache(int cacheSize){
        
        String type = getInfo(GlobalName.CACHE_TYPE_CONSTRUCTORS_CACHE);
        type = type.toUpperCase();
        ConstructorsCacheDefinition cache = null;
        switch (type) {
            case "LRU": cache = new LRUConstructorsCache(cacheSize); break;
            case "CONCURRENT": cache = new ConcurrentConstructorsCache(); break;
            default: cache = new LRUConstructorsCache(cacheSize); break;
        }
        ConstructorsCache.getInstance().initiate(cache);
    }
    
   private void setDaosCache(int cacheSize){
        
        String type = getInfo(GlobalName.CACHE_TYPE_DAOS_CACHE);
        type = type.toUpperCase();
        DaosCacheDefinition cache = null;
        switch (type) {
            case "LRU": cache = new LRUDaosCache(cacheSize); break;
            case "CONCURRENT": cache = new ConcurrentDaosCache(); break;
            default: cache = new LRUDaosCache(cacheSize); break;
        }
        DaosCache.getInstance().initiate(cache);
    }
   
   private void setFieldsCache(int cacheSize){
       
       String type = getInfo(GlobalName.CACHE_TYPE_FIELDS_CACHE);
       type = type.toUpperCase(); 
       FieldsCacheDefinition cache = null;
       switch (type) {
           case "LRU": cache = new LRUFieldsCache(cacheSize); break;
           case "CONCURRENT": cache = new ConcurrentFieldsCache(); break;
           default: cache = new LRUFieldsCache(cacheSize); break;
       }
       FieldsCache.getInstance().initiate(cache);
   }
    
    /**
     * 2014年11月17日
     * @author decaywood
     *
     * 用回调的方式进行参数注入
     */
    private void setDatabaseInfo(){
        
        connection.setDatabase(getInfo(GlobalName.DATABASE_INFO_NAME_DATABASE_LOGNAME));
        
        connection.setUsername(getInfo(GlobalName.DATABASE_INFO_NAME_USERNAME));
        
        connection.setHost(getInfo(GlobalName.DATABASE_INFO_NAME_HOST));
        
        connection.setPort(Integer.parseInt(getInfo(GlobalName.DATABASE_INFO_NAME_PORT)));
        
        connection.setPassword(getInfo(GlobalName.DATABASE_INFO_NAME_PASSWORD));
        
        
    }
    
    private void setOptions(){

        MongoClientOptions.Builder optionBuilder  = MongoClientOptions.builder();
        
        
        if(checkNull(GlobalName.OPTIONS_NAME_DESCRIPTION))
            optionBuilder.description(getInfo(GlobalName.OPTIONS_NAME_DESCRIPTION));
        
        
        if(checkNull(GlobalName.OPTIONS_NAME_MIN_CONNECT_PER_HOST));
            optionBuilder.minConnectionsPerHost(Integer.parseInt(getInfo(GlobalName.OPTIONS_NAME_MIN_CONNECT_PER_HOST)));
            
            
        if(checkNull(GlobalName.OPTIONS_NAME_CONNECT_PER_HOST))
            optionBuilder.connectionsPerHost(Integer.parseInt(getInfo(GlobalName.OPTIONS_NAME_CONNECT_PER_HOST)));
        
        
        if(checkNull(GlobalName.OPTIONS_NAME_THREAD_ALLOW_TO_BLOCK_FOR_CONNECTMULTIPLIER))
            optionBuilder.threadsAllowedToBlockForConnectionMultiplier(Integer.parseInt(getInfo(GlobalName.OPTIONS_NAME_THREAD_ALLOW_TO_BLOCK_FOR_CONNECTMULTIPLIER)));
       
        
        if(checkNull(GlobalName.OPTIONS_NAME_MAX_WAIT_TIME))
            optionBuilder.maxWaitTime(Integer.parseInt(getInfo(GlobalName.OPTIONS_NAME_MAX_WAIT_TIME)));
      
        
        if(checkNull(GlobalName.OPTIONS_NAME_MAX_CONNECT_IDLE_TIME))
            optionBuilder.maxConnectionIdleTime(Integer.parseInt(getInfo(GlobalName.OPTIONS_NAME_MAX_CONNECT_IDLE_TIME)));
       
        
        if(checkNull(GlobalName.OPTIONS_NAME_MAX_CONNECT_LIFE_TIME))
            optionBuilder.maxConnectionLifeTime(Integer.parseInt(getInfo(GlobalName.OPTIONS_NAME_MAX_CONNECT_LIFE_TIME)));
       
        
        if(checkNull(GlobalName.OPTIONS_NAME_CONNECT_TIMEOUT))
            optionBuilder.connectTimeout(Integer.parseInt(getInfo(GlobalName.OPTIONS_NAME_CONNECT_TIMEOUT)));
       
        
        if(checkNull(GlobalName.OPTIONS_NAME_SOCKET_TIMEOUT))
            optionBuilder.socketTimeout(Integer.parseInt(getInfo(GlobalName.OPTIONS_NAME_SOCKET_TIMEOUT)));
       
        
        if(checkNull(GlobalName.OPTIONS_NAME_SOCKET_KEEP_ALIVE))
            optionBuilder.socketKeepAlive(Boolean.parseBoolean(getInfo(GlobalName.OPTIONS_NAME_SOCKET_KEEP_ALIVE)));
      
        
        if(checkNull(GlobalName.OPTIONS_NAME_READ_PREFERENCE))
            setPreference(optionBuilder);
      
        
        if(checkNull(GlobalName.OPTIONS_NAME_DATABASE_DECODER_FACTORY))
            setDecoderFactory(optionBuilder);
       
        
        if(checkNull(GlobalName.OPTIONS_NAME_DATABASE_ENCODER_FACTORY))
            setEncoderFactory(optionBuilder);
      
        
        if(checkNull(GlobalName.OPTIONS_NAME_WRITE_CONCERN))
            setWriteConcern(optionBuilder);
     
        
        if(checkNull(GlobalName.OPTIONS_NAME_SOCKET_FACTORY))
            setSocketFactory(optionBuilder);
     
        
        if(checkNull(GlobalName.OPTIONS_NAME_CURSOR_FINALIZER_ENABLED))
            optionBuilder.cursorFinalizerEnabled(Boolean.parseBoolean(getInfo(GlobalName.OPTIONS_NAME_CURSOR_FINALIZER_ENABLED)));
      
        
        if(checkNull(GlobalName.OPTIONS_NAME_ALWAYS_USE_MBEANS))
            optionBuilder.alwaysUseMBeans(Boolean.parseBoolean(getInfo(GlobalName.OPTIONS_NAME_ALWAYS_USE_MBEANS)));
       
        
        if(checkNull(GlobalName.OPTIONS_NAME_HEARTBEAT_FREQUENCY))
            optionBuilder.heartbeatFrequency(Integer.parseInt(getInfo(GlobalName.OPTIONS_NAME_HEARTBEAT_FREQUENCY)));
      
        
        if(checkNull(GlobalName.OPTIONS_NAME_MIN_HEARTBEAT_FREQUENCY))
            optionBuilder.minHeartbeatFrequency(Integer.parseInt(getInfo(GlobalName.OPTIONS_NAME_MIN_HEARTBEAT_FREQUENCY)));
      
        
        if(checkNull(GlobalName.OPTIONS_NAME_HEARTBEAT_CONNECT_TIMEOUT))
            optionBuilder.heartbeatConnectTimeout(Integer.parseInt(getInfo(GlobalName.OPTIONS_NAME_HEARTBEAT_CONNECT_TIMEOUT)));
     
        
        if(checkNull(GlobalName.OPTIONS_NAME_SOCKET_TIMEOUT))
            optionBuilder.socketTimeout(Integer.parseInt(getInfo(GlobalName.OPTIONS_NAME_SOCKET_TIMEOUT)));
       
        
        if(checkNull(GlobalName.OPTIONS_NAME_ACCEPT_LATENCY_DIFFERENCE))
            optionBuilder.acceptableLatencyDifference(Integer.parseInt(getInfo(GlobalName.OPTIONS_NAME_ACCEPT_LATENCY_DIFFERENCE)));
      
        
        if(checkNull(GlobalName.OPTIONS_NAME_REQ_REPLICA_SET_NAME))
            optionBuilder.requiredReplicaSetName(getInfo(GlobalName.OPTIONS_NAME_REQ_REPLICA_SET_NAME));
        
        connection.setOptions(optionBuilder.build());
           
    }
    
    private void setSocketFactory(Builder builder){
        
        String socketFactory = getInfo(GlobalName.OPTIONS_NAME_SOCKET_FACTORY);
        
        switch (socketFactory) {
            case "defaultFactory": builder.socketFactory(SocketFactory.getDefault()); break;
            default:  break;
        }
        
    }
    
    private void setWriteConcern(Builder builder){
        
        String writeConcernName = getInfo(GlobalName.OPTIONS_NAME_WRITE_CONCERN);
        
        switch (writeConcernName){
        
            case "acknowledged": builder.writeConcern(WriteConcern.ACKNOWLEDGED); break;
            
            case "fsyncSafe": builder.writeConcern(WriteConcern.FSYNC_SAFE); break;
            
            case "fsynced": builder.writeConcern(WriteConcern.FSYNCED); break;
            
            case "journalSafe": builder.writeConcern(WriteConcern.JOURNAL_SAFE); break;
            
            case "journaled": builder.writeConcern(WriteConcern.JOURNALED); break;
            
            case "majority": builder.writeConcern(WriteConcern.MAJORITY); break;
            
            case "normal": builder.writeConcern(WriteConcern.NORMAL); break;
            
            case "replicaAcknowledged": builder.writeConcern(WriteConcern.REPLICA_ACKNOWLEDGED); break;
            
            case "replicaSafe": builder.writeConcern(WriteConcern.REPLICAS_SAFE); break;
            
            case "safe": builder.writeConcern(WriteConcern.SAFE); break;
            
            case "unacknowledged": builder.writeConcern(WriteConcern.UNACKNOWLEDGED); break;
            
            default: builder.writeConcern(WriteConcern.NORMAL); break;
           
        }
    }
    
    private void setEncoderFactory(Builder builder){
        
        String factoryName = getInfo(GlobalName.OPTIONS_NAME_DATABASE_ENCODER_FACTORY);
        
        switch (factoryName) {
            case "DefaultFactory": builder.dbEncoderFactory(DefaultDBEncoder.FACTORY); break;
            default: 
            try {
                Class<?> clazz = ReflectUtil.classForName(factoryName, this.getClass());
                if(DBEncoderFactory.class.isAssignableFrom(clazz))
                    builder.dbEncoderFactory((DBEncoderFactory) ConstructorsCache.getInstance().get(clazz).newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        }
    }
    
    private void setDecoderFactory(Builder builder){
        
        String factoryName = getInfo(GlobalName.OPTIONS_NAME_DATABASE_DECODER_FACTORY);
        
        switch (factoryName) {
            case "DefaultFactory": builder.dbDecoderFactory(DefaultDBDecoder.FACTORY); break;
            case "LazyDBDecoderFactory": builder.dbDecoderFactory(LazyDBDecoder.FACTORY); break;
            default: 
            try {
                Class<?> clazz = ReflectUtil.classForName(factoryName, this.getClass());
                if(DBDecoderFactory.class.isAssignableFrom(clazz))
                    builder.dbDecoderFactory((DBDecoderFactory) ConstructorsCache.getInstance().get(clazz).newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        }
       
    }
             
   
    
    private void setPreference(Builder builder){
        
        String preferenceName = getInfo(GlobalName.OPTIONS_NAME_READ_PREFERENCE);
        
        switch (preferenceName) {
        
            case "nearest": builder.readPreference(ReadPreference.nearest()); break;   
            
            case "primary": builder.readPreference(ReadPreference.primary()); break;
            
            case "primaryPreferred": builder.readPreference(ReadPreference.primaryPreferred()); break; 
            
            case "secondary": builder.readPreference(ReadPreference.secondary()); break;
            
            case "secondaryPreferred": builder.readPreference(ReadPreference.secondaryPreferred()); break;
            
            default: 
            try {
                Class<?> clazz = ReflectUtil.classForName(preferenceName, this.getClass());
                if(ReadPreference.class.isAssignableFrom(clazz))
                    builder.readPreference((ReadPreference) ConstructorsCache.getInstance().get(clazz).newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        }
        
    }
    
    private boolean checkNull(GlobalName globalName){
        return infoMap.get(globalName) == null ? false : true;
    }
    
    private String getInfo(GlobalName globalName){
        return infoMap.get(globalName);
    }
  
}
