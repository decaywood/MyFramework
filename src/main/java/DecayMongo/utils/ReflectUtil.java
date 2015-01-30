package DecayMongo.utils;

/**
 * 
 * 2014年10月25日
 * 
 * @author decaywood
 * 
 *         反射工具，用于各种反射操作
 * 
 *            动态加载资源时，往往需要从三种类加载器里选择：系统或说程序的类加载器、当前类加载器、以及当前线程的上下文类加载器。
 *         系统类加载器通常不会使用,此类加载器处理启动应用程序时classpath指定的类，可以通过ClassLoader.getSystemClassLoader()来获得。
 *         所有的ClassLoader.getSystemXXX()接口也是通过这个类加载器加载的。一般不要显式调用这些方法，应该让其他类加载器代理到系统类加载器上。
 *         由于系统类加载器是JVM最后创建的类加载器，这样代码只会适应于简单命令行启动的程序。一旦代码移植到EJB、Web应用或者Java Web
 *         Start应用程序中，程序肯定不能正确执行。因此一般只有两种选择，当前类加载器和线程上下文类加载器。
 *            当前类加载器是指当前方法所在类的加载器。这个类加载器是运行时类解析使用的加载器，Class.forName(String)和
 *         Class.getResource(String)也使用该类加载器。代码中X.class的写法使用的类加载器也是这个类加载器。
 *         线程上下文类加载器在Java2(J2SE)时引入。每个线程都有一个关联的上下文类加载器。如果你使用new Thread()方式生成新的线程，
 *         新线程将继承其父线程的上下文类加载器。如果程序对线程上下文类加载器没有任何改动的话，程序中所有的线程将都使用系统类加载器作为上下文类加载器。
 *         Web应用和Java企业级应用中，应用服务器经常要使用复杂的类加载器结构来实现JNDI（Java命名和目录接口)、线程池、组件热部署等功能，
 *         因此理解这一点尤其重要。为什么要引入线程的上下文类加载器？将它引入J2SE并不是纯粹的噱头，由于Sun没有提供充分的文档解释说明这一点，这使许多开发者很糊涂。
 *         实际上，上下文类加载器为同样在J2SE中引入的类加载代理机制提供了后门。通常JVM中的类加载器是按照层次结构组织的，目的是每个类加载器
 *         （除了启动整个JVM的原初类加载器）都有一个父类加载器。当类加载请求到来时，类加载器通常首先将请求代理给父类加载器。只有当父类加载器失败后
 *         ，它才试图按照自己的算法查找并定义当前类。有时这种模式并不能总是奏效。这通常发生在JVM核心代码必须动态加载由应用程序动态提供的资源时
 *         。拿JNDI为例，它的核心是由JRE核心类(rt.jar)实现的。但这些核心JNDI类必须能加载由第三方厂商提供的JNDI实现。这种情况下调用父类加载器
 *         （原初类加载器）来加载只有其子类加载器可见的类，这种代理机制就会失效。解决办法就是让核心JNDI类使用线程上下文类加载器，
 *         从而有效的打通类加载器层次结构，逆着代理机制的方向使用类加载器。
 *            顺便提一下，XML解析API(JAXP)也是使用此种机制。当JAXP还是J2SE扩展时，XML解析器使用当前累加载器方法来加载解析器实现。
 *         但当JAXP成为J2SE核心代码后，类加载机制就换成了使用线程上下文加载器，这和JNDI的原因相似。
 */
public class ReflectUtil {
 
    /**
     * 
     * 
     * @param name
     * @param caller
     * @return Class
     * @throws ClassNotFoundException
     *
     */
    public static Class<?> classForName(String name, Class caller) throws Exception {
         
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            return contextClassLoader.loadClass(name);
        }
        return Class.forName(name, true, caller.getClassLoader());
    }

    /**
     * 
     * 
     * @param name
     * @return Class
     * @throws Exception
     *
     */
    public static Class<?> classForName(String name) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if(classLoader != null){
            return classLoader.loadClass(name);
        }
        return Class.forName(name);
    }
}
