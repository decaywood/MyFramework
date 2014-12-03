package org.decaywood;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 2014年10月25日
 * @author decaywood
 *
 * 全局名称类
 */
public enum GlobalName {
    
    NULL(""),
    
    USER_CONFIG("user-config"),
    USER_CONFIG_NAME("name"),
    
    DATABASE_INFO("database-info"),
    DATABASE_INFO_NAME("name"),
    DATABASE_INFO_NAME_USERNAME("userName"),
    DATABASE_INFO_NAME_HOST("host"),
    DATABASE_INFO_NAME_PORT("port"),
    DATABASE_INFO_NAME_DATABASE_LOGNAME("databaseName"),
    DATABASE_INFO_NAME_PASSWORD("password"),
   
    OPTIONS("client-options"),
    OPTIONS_NAME("option"),
    OPTIONS_NAME_DESCRIPTION("description"),
    OPTIONS_NAME_MIN_CONNECT_PER_HOST("minConnectionsPerHost"),
    OPTIONS_NAME_CONNECT_PER_HOST("connectionsPerHost"),
    OPTIONS_NAME_THREAD_ALLOW_TO_BLOCK_FOR_CONNECTMULTIPLIER("threadsAllowedToBlockForConnectionMultiplier"),
    OPTIONS_NAME_MAX_WAIT_TIME("maxWaitTime"),
    OPTIONS_NAME_MAX_CONNECT_IDLE_TIME("maxConnectionIdleTime"),
    OPTIONS_NAME_MAX_CONNECT_LIFE_TIME("maxConnectionLifeTime"),
    OPTIONS_NAME_CONNECT_TIMEOUT("connectTimeout"),
    OPTIONS_NAME_SOCKET_TIMEOUT("socketTimeout"),
    OPTIONS_NAME_SOCKET_KEEP_ALIVE("socketKeepAlive"),
    OPTIONS_NAME_READ_PREFERENCE("readPreference"),
    OPTIONS_NAME_DATABASE_DECODER_FACTORY("dbDecoderFactory"),
    OPTIONS_NAME_DATABASE_ENCODER_FACTORY("dbEncoderFactory"),
    OPTIONS_NAME_WRITE_CONCERN("writeConcern"),
    OPTIONS_NAME_SOCKET_FACTORY("socketFactory"),
    OPTIONS_NAME_CURSOR_FINALIZER_ENABLED("cursorFinalizerEnabled"),
    OPTIONS_NAME_ALWAYS_USE_MBEANS("alwaysUseMBeans"),
    OPTIONS_NAME_HEARTBEAT_FREQUENCY("heartbeatFrequency"),
    OPTIONS_NAME_MIN_HEARTBEAT_FREQUENCY("minHeartbeatFrequency"),
    OPTIONS_NAME_HEARTBEAT_CONNECT_TIMEOUT("heartbeatConnectTimeout"),
    OPTIONS_NAME_HEART_BEAT_SOCKET_TIMEOUT("heartbeatSocketTimeout"),
    OPTIONS_NAME_ACCEPT_LATENCY_DIFFERENCE("acceptableLatencyDifference"),
    OPTIONS_NAME_REQ_REPLICA_SET_NAME("requiredReplicaSetName"),
     
    CACHE("cache"),
    CACHE_TYPE("type"),
    CACHE_SIZE("cache-size"),
    CACHE_TYPE_DAOS_CACHE("daosCache"),
    CACHE_TYPE_CONSTRUCTORS_CACHE("constructorCache"),
    CACHE_TYPE_FIELDS_CACHE("fieldsCache");
    
    private static Map<String, GlobalName> globalNameMap;
    
    private String name;
  
    private GlobalName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    private static void init(){
        if(globalNameMap == null){
            globalNameMap = new HashMap<String, GlobalName>();
            GlobalName[] globalNames = GlobalName.values();
            for(GlobalName globalName : globalNames)
                globalNameMap.put(globalName.toString(), globalName);
        }
    }
    
    public static GlobalName getGlobalName(String name){
        init();
        return globalNameMap.get(name);
    }
}
