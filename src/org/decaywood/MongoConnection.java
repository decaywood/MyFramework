package org.decaywood;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.decaywood.utils.ThreadManager;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
/**
 * 
 * 2014年10月26日
 * @author decaywood
 *
 * ConnectionUtils记录了连接数据库所需参数,可看作连接数据库的初始化方法
 * MongoDB的Java驱动是线程安全的，对于一般的应用，只要一个Mongo实例即可，Mongo有个内置的连接池（池大小默认为10个）。
 * 对于有大量写和读的环境中，为了确保在一个Session中使用同一个DB时，我们可以用以下方式保证一致性：
 * DB mdb = mongo.getDB('dbname');
 * mdb.requestStart();
 *   .....业务代码
 * mdb.requestDone();
 *
 */
public class MongoConnection {

    private static Logger logger = Logger.getLogger(MongoConnection.class.toString());
    
    private String host;
    private int port;
    private List<ServerAddress> replicaSet;
    private ReadPreference readPreference;
    private MongoClientOptions options;
    private String database;
    private String username;
    private String password;
    
    /**
     * MongoClient对象内部实现了一个连接池。MongoClient对象是线程安全的，
     * 因此可以只创建一个，在多线程环境下安全使用。
     * 因此，我们可以用将MongoClient变量作为一个Singleton类的成员变量，
     * 从而保证只创建一个连接池。
     * MongoClient.close方法将关闭当前所有活跃的连接。
     * 所以要在web工程被从Tomcat或者GlassFish容器中注销的时候确保调用close方法。
     */
    private MongoClient mongoClient;
    /**
     *  DB和DBCollection是绝对线程安全的，它们被缓存起来了，
     *  所以在应用中取到的可能是同一个对象。
     *  
     *  DB对象可以通过MongoClient.get方法获得，代表了和数据库的一个连接。
     *  默认情况下，当执行完数据库的查询或者更新操作后，连接将自动回到连接池中。不需要我们手动调用代码放回池中。
     *  至于如何实现，我猜测是update,query,save方法内部有finally块，那里面有还连接到池中的代码
     *  
     *  官方文档：
     *  
     *   The MongoClient instance actually represents a pool of connections to the database;
     *   you will only need one instance of class MongoClient even with multiple threads. 
     *   See the concurrency doc page for more information.
     *   
     *   
     */
    private DB db;
    
    /*
     * 嵌套类单例模式
     */
    private static class InnerClass {
        final static MongoConnection instance = new MongoConnection();
    } 
    
    public static MongoConnection getInstance(){
        return InnerClass.instance;
    }

    /**
     * 
     * 2014年11月22日
     * @author decaywood
     *
     * 调用ConfigReader读取Xml文件配置信息，通过回调方式将配置信息
     * 注入当前类的域中，完成信息读取。
     * 
     */
    public void startConnect(){
        try{
            ConfigReader configReader = new ConfigReader(this);
            configReader.read("framework-config.xml");
            doConnect();
        }catch(Exception ex) {
            ex.printStackTrace();
            logger.info("Can not connect to host " + host);
        }
    }
    
    
    private void doConnect() throws Exception {
        
        MongoCredential credential = null;
        
        if(username != null && password != null){
            /**
             * 进行用户名跟密码的验证，mongodb新版驱动采用这种验证方式！
             */
            credential = MongoCredential.createCredential(username, database, password.toCharArray());
        }
        
        List<MongoCredential> credentials = Arrays.asList(credential);
        
        if(host != null && port != 0){
            
            ServerAddress serverAddress = new ServerAddress(host, port);
            mongoClient = options != null ? new MongoClient(serverAddress, credentials, options)
                                          : new MongoClient(serverAddress, credentials);
        }
        else if(replicaSet != null){
            mongoClient = options != null ? new MongoClient(replicaSet, credentials, options)
                                          : new MongoClient(replicaSet, credentials);
            if(readPreference != null){
                mongoClient.setReadPreference(readPreference);
            }
        }
        
        if(mongoClient != null)
            db = mongoClient.getDB(database);
        else
            throw new Exception("Can not get database instance! Please ensure connected to mongoDB correctly.");
        
    }
    
    
  
    /**
     * 当应用程序退出的时候，可以调用close()方法关闭MongoConnection，以便立即释放所有资源。
     */
    public void close(){
        ThreadManager.getInstance().destroy();
        if(mongoClient != null){
            mongoClient.close();
        }
    }

    public MongoConnection setHost(String host){
        this.host = host;
        return this;
    }
    
    public MongoConnection setPort(int port){
        this.port = port;
        return this;
    }
    
    public MongoConnection setDatabase(String database){
        this.database = database;
        return this;
    }
    
    public MongoConnection setUsername(String username){
        this.username = username;
        return this;
    }
    
    public MongoConnection setPassword(String password){
        this.password = password;
        return this;
    }

    public MongoConnection setOptions(MongoClientOptions options) {
        this.options = options;
        return this;
    }

    public MongoConnection setReplicaSet(List<ServerAddress> replicaSet) {
        this.replicaSet = replicaSet;
        return this;
    }

    public MongoConnection setReadPreference(ReadPreference readPreference) {
        this.readPreference = readPreference;
        return this;
    }

    public DB getDB(){
        return db;
    }
}
