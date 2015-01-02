DecayMongo设计文档
====================================
        DecayMongo是一个轻量级的MongoDB框架，旨在封装MongoDB驱动，让开发者可以更好的用面向对象思维操纵MongoDB数
    据库。这篇文档主要介绍DecayMongo的主要功能，技术细节也将在不久的将来进行开源，还有很多功能处于未实现和不成熟
    的状态，但是基本的对象映射和Xml属性配置特性已经实现。DecayMongo是一个有野心的项目，未来它将集成一个强大的高性
    能并发框架，让无锁并发成为可能，在实际应用中，DecayMongo将成为理想的数据缓冲层和数据访问层。目前DecayMongo项目
    还需要大量的单元测试以及集成测试，希望志同道合的朋友能够一起参与！
# 连接MongoDB
        本节将介绍DecayMongo与MongoDB进行连接的方法，假定你已经配置好MongoDB的使用环境，如果没有配置好请先配置好
    MongoDB数据库环境。DecayMongo与MongoDB进行连接是XML进行参数配置的，利用XML配置参数的有点是若有参数修改可以
    不用重新编译代码，只需修改XML文件即可，下面将介绍DecayMongo的各个XML参数以及在Java中如何连接MongoDB。
## 配置XML
        连接MongoDB所需要的一些基本配置都可以通过XML配置完成。大致可以包括数据库访问参数、数据库运行参数、缓存类
    型参数。XML配置树示例如下：
    <framework-config>
      <user-config name = "decaywood">
       <database-info name="databaseName">test</database-info>
       <database-info name="userName">java</database-info>
       <database-info name="host">localhost</database-info>
       <database-info name="port">27017</database-info>
       <database-info name="password">123456</database-info>
       <cache type="daosCache" cache-size="500">lru</cache>
       <cache type="constructorCache">concurrent</cache>
       <cache type="fieldsCache">lru</cache>
       <client-options option="maxWaitTime">2009</client-options>
       <client-options option="maxConnectionIdleTime">132</client-options>
       <client-options option="maxConnectionLifeTime">123</client-options>
       <client-options option="connectTimeout">123</client-options>
       </user-config>
    </framework-config>
### MongoDB访问参数（database-info）：
        MongoDB访问参数包括：数据库名称（databaseName）、用户名称（userName）、IP地址（host）本机为localhost、
    端口（port）、密码（password），配置访问参数只需要在user-config下建立database-info子目录即可，注意访问参
    数是必须配置的，password如果没有也需要配置，参数为空即可：
    <database-info name="password"></database-info>
### 数据库运行参数（client-options）：
    数据库运行参数有很多，包括
    description
    minConnectionsPerHost 
    connectionsPerHost 
    threadsAllowedToBlockForConnectionMultiplier 
    maxWaitTime 
    maxConnectionIdleTime 
    maxConnectionLifeTime
    connectTimeout 
    socketTimeout 
    autoConnectRetry 
    socketKeepAlive 
    maxAutoConnectRetryTime 
    readPreference 
    dbDecoderFactory 
    dbEncoderFactory 
    writeConcern 
    socketFactory 
    cursorFinalizerEnabled 
    alwaysUseMBeans 
    heartbeatFrequency 
    minHeartbeatFrequency
    heartbeatConnectTimeout
    heartbeatSocketTimeout 
    heartbeatThreadCount 
    acceptableLatencyDifference 
    requiredReplicaSetName
    配置运行参数只需要在user-config下建立client-options子目录即可，如果使用默认参数可以不配置。
### 缓存类型参数（cache）
        DecayMongo对很多常用数据提供了缓存池，保证框架能够进行快速响应，缓存池面向接口设计，有多种实现策略，
    通过缓存类型参数可以配置缓存池实现策略。目前只实现了普通并发缓存策略和LRU缓存策略。配置缓存类型只需
    要在user-config下建立cache子目录即可例如：
    <cache type="constructorCache">concurrent</cache>
## 使用DecayMongo进行MongoDB连接
        配置好XML文件后，只需与DecayMongo的控制实例进行交互就能轻易地操控MongoDB了，DecayMongo的主要控制实例
    有两个：MongoConnection以及DaosCache，MongoConnection主要负责与MongoDB进行连接以及将XML配置初始化至MongoDB。
    DaosCache则会生成需要持久化的类的对应的数据访问对象。通过这个对象就可以完成这个持久化类所属实例的增删改查。
### MongoConnection
        MongoConnection类持有三个重要的类的实例，分别为：MongoClient、MongoClientOptions、DB，三个类的作用可以概括
    为对MongoDB进行交互、配置。而MongoConnection则是对这些功能进行进一步的封装。如刚才介绍的，MongoConnection
    主要作用是将XML配置信息设置到MongoDB中，具体细节为通过一个配置解析类ConfigReader读取XML信息，并进行组织提取，
    完成这一步后通过回调MongoConnection把信息通过MongoClientOptions进行配置。之后进行用户验证，随即生成MongoClient
    实例进而获取DB实例。
        DB实例是数据交互的核心，它被数据访问对象通过MongoConnection间接持有。就像前面提到的，数据访问对象由
    DaosCache生成。
### DaosCache
    DaosCache类是一个缓存类，它对数据访问对象进行缓存，每一个实现了EntityDefinition对象的类在进行数据库操作时都
    会被DaosCache所缓存，具体的缓存策略由用户通过XML进行配置。
# 对象文档映射
    在对象(Object，也称实体Entity)、文档(Document)之间实现自动转换，是DecayMongo的最核心功能，这能让你直接用面向
    对象的概念来操作MongoDB数据库，而不用去关心底层的数据库细节。
## EntityDefinition接口
    要使得某个Java实体能和MongoDB Document实现相互转换，该实体需要实现EntityDefinition接口，该接口中有2个方法，
    如下：
    public void setID(String id);
    public String getID();
## 注解
    DecayMongo是基于注解机制进行对象-数据转换的，通过注解标记，利用反射机制将目标对象转换为MongoDB可以识别的
    Document格式。DecayMongo中的注解氛围两类--域注解以及类注解
### 类注解
    被类注解标记的对象将被映射到MongoDB中，对应的被注解标记的域在MongoDB中将有对应的数据结构。
#### Entity--DecayMongo持久化对象标记注解
##### 注解信息：
    public @interface Entity
###### 1、作用域：
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
###### 2、属性：
    Name：String型，表示其在MongoDB中的collection的名称。name属性可以省略，默认使用类名的全小写。
    Split：enum型，表示划分方式：enum包括：
      None：不进行划分、
      String：进行用户定义划分、
      Daily：以天为单位进行日期划分、   
      Monthly：以月为单位进行划分、
      Yearly：以年为单位进行划分。
    splitSuffix：如果以用户定义方式进行划分，必须设置splitSuffix。
    Capped：boolean型，表示该Entity类对应的是Capped Collection，默认False。
    capSize：long型，设置Capped Collection的大小。
    capMax：long型，设置Capped Collection的最多能存储多少个document。如果设置了capped=true，
    则需要设置capSize和capMax两者中的其中一个。
    类注解只有Entity一个，所有需要进行持久化的类必须标记该注解，相应的，类中需要持久化的域也需要标记对应的注解。
### 域注解
        域注解即标记在域上的注解，它可以标识域的类型以及在MongoDB中希望存储成的数据格式。域注解在DecayMongo中不是
    必须的，所有未标记域注解的域默认忽略（暂未在代码中定义，现版本默认为@Property）。下面，将一一介绍DecayMongo
    中的各个注解的作用。
#### ID--实体ID注解
##### 注解信息：
    public @interface ID
###### 1、作用域：
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
###### 2、属性：
    Type：enum类型：
        AUTO_GENERATE：默认值。表示由MongoDB自动产生id值。在MongoDB中，id被保存成为一个ObjectId对象。
        AUTO_INCREASE：ID自增长（暂未实现）。
        USER_DEFINE：用户定义。
    Start：如果使用type=IdType.AUTO_INCREASE，那么还可以设置start参数，使得id从指定的值开始增长。
        映射到MongoDB中的_id。@Id属性在Java代码中必须为String类型，但其在MongoDB中的数据类型，
    则根据对@Id的type属性的设置而定。
#### Property--基本类型注解
##### 注解信息：
    public @interface Property
###### 1、作用域：
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
###### 2、属性：
    Name：String型，用于指定映射到MongoDB collection中某个field。属性name可以省略，表示采用与Entity的Field相同的名称。
    Lazy：Boolean型，含义是：取出该实体的列表时，是否取出该field值。如果为true，表示不取出。默认值为false。
    该注解可以省略。它用来映射基本数据类型，包括：String、int、long、short、byte、float、double、boolean、char、Date、
    Timestamp等，以及这些基本数据类型组成的数组、List、Set、Map。
#### Ignore--忽略注解
##### 注解信息：
    public @interface Ignore
###### 1、作用域：
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
###### 2、属性：
    无
    表示该属性不需要被映射。当保存实体时，该属性不会保存至MongoDB；同样，该属性也不会从MongoDB中取出。
#### Embed--内嵌格式注解
##### 注解信息：
    Public @interface Embed 
###### 1、作用域：
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
###### 2、属性：
    Name：String型，用于指定映射到MongoDB collection中某个field。属性name可以省略，表示采用与Entity的Field相同的名称。
    Lazy：Boolean型，含义是：取出该实体的列表时，是否取出该field值。如果为true，表示不取出。默认值为false。
        标记为Embed注解，表示该域是一个嵌入的对象。嵌入对象只是一个普通的POJO，不需要@Entity注解，不需要实现
    EntityDefinition接口，也不需要有@Id。DecayMongo将会让Java对象持久化为MongoDB中的内嵌形文档。
#### GroupEmbed--内嵌格式集合注解
##### 注解信息：
    public @interface GroupEmbed
###### 1、作用域：
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
###### 2、属性：
    Name：String型，用于指定映射到MongoDB collection中某个field。属性name可以省略，表示采用与Entity的Field相同的名称。
    Lazy：Boolean型，含义是：取出该实体的列表时，是否取出该field值。如果为true，表示不取出。默认值为false。
    groupClass指集合实现类（即Collection或者Map接口的实现类），若集合不是数组，则此属性必须填写，否则报错。
    标记为GroupEmbed注解，表示该域是一组嵌入的对象。GroupEmbed注解支持数组、Collection接口、Map接口，但都必须使用
    泛型。当使用Map的时候，嵌入对象只能作为Map的value，而不能作为key。注意：和Embed一样，嵌入的对象都必须是自己定
    义的Java对象。如果是原始数据类型组成的Collection、Map等，请使用@Property，而不是@EmbedList。跟@Property一样。
#### Reference--引用注解
##### 注解信息：
    public @interface Reference 
###### 1、作用域：
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
###### 2、属性：
    Name：String型，用于指定映射到MongoDB collection中某个field。属性name可以省略，表示采用与Entity的Field相同的名称。
    Cascade：int类型，用于指定关联操作，所谓关联，就是如果有设置为Reference标记的域的话，含有该域的持久对象进行增删改
    查操作时，该域会对其行为采取对应的措施。Cascade有五种类型可选：
        CASCADE_DEFAULT表示不进行关联操作。
        CASCADE_CREATE表示插入对象时若该域还未持久化到数据库则将该域对象进行持久化。
        CASCADE_RETRIEVE表示查询时会关联取出该域表示的对象。
        CASCADE_UPDATE表示更新时会同步更新该对象。
        CASCADE_DELETE表示删除域所在类的对象时，会进行关联删除。
    Reduced：为true时直接存储的是该域ObjectID序列，通过ID在数据库查找该对象对应文档；
    为false时，将会存储域名，更加直观，但是更消耗存储空间。
    ImplementClass：若该域为接口，必须通过此属性指明实现类。
        标记为Reference注解，表示该域是一个引用类型，类似于关系型数据库中的外键。
#### GroupReference--引用集合注解
##### 注解信息：
    public @interface GroupReference
###### 1、作用域：
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
###### 2、属性：
    Name：String型，用于指定映射到MongoDB collection中某个field。属性name可以省略，表示采用与Entity的Field相同的名称。
    Cascade：int类型，用于指定关联操作，所谓关联，就是如果有设置为Reference标记的域的话，含有该域的持久对象进行增删改
    查操作时，该域会对其行为采取对应的措施。Cascade有五种类型可选：
        CASCADE_DEFAULT表示不进行关联操作。
        CASCADE_CREATE表示插入对象时若该域还未持久化到数据库则将该域对象进行持久化。
        CASCADE_RETRIEVE表示查询时会关联取出该域表示的对象。
        CASCADE_UPDATE表示更新时会同步更新该对象。
        CASCADE_DELETE表示删除域所在类的对象时，会进行关联删除。
    Sort：String型，用于关联取出该List或Set属性时使用的排序规则，其形式是："{'level':1}"，或"{'level':1, 
    'timestamp':-1}"这样的排序字符串。如果不排序，则不用设置sort值。
    Reduced：为true时直接存储的是该域Object ID序列，通过ID在数据库查找该对象对应文档；
    为false时，将会存储域名，更加直观，但是更消耗存储空间。
    ImplementClass：若该域为接口，必须通过此属性指明实现类。
      标记为Reference注解，表示该域是一个引用类型，类似于关系型数据库中的外键。groupClass指集合实现类
    （即Collection或者Map接口的实现类），若集合不是数组，则此属性必须填写，否则报错。

