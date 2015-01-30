package DecayMongo.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import DecayMongo.xml.XmlPathTool;

/**
 * 
 * 2014年10月23日
 * @author decaywood
 *
 * 加载XML配置文件
 */
public class ConfigureUtil {

    private static final Logger log = Logger.getLogger(ConfigureUtil.class.toString());
    
    private ConfigureUtil() {}

    /**
     * 读取相对路径输入流
     * 
     * 首先，Java中的getResourceAsStream有以下几种： 
     *   1. Class.getResourceAsStream(String path) ： path 不以’/'开头时默认是从此类所在的包下取资源，以’/'开头则是从
     *      ClassPath根下获取。其只是通过path构造一个绝对路径，最终还是由ClassLoader获取资源。
     *   2. Class.getClassLoader.getResourceAsStream(String path) ：默认则是从ClassPath根下获取，path不能以’/'开头，
     *     最终是由ClassLoader获取资源。
     * 
     * @param path
     * @return
     * @throws Exception
     *
     */
    public static InputStream getResourceStream(String path) throws Exception {
        if(log != null)
            log.info(" getResourceAsStream " + " path -> " + path);
        String stripped = path.startsWith("/") ?
                path.substring(1) : path;

        InputStream stream = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if(classLoader!=null) {   
            stream = classLoader.getResourceAsStream(stripped);
        }
        if(stream == null){
            throw new Exception( path + " not found" );
        }
        return stream;
    }

    /**
     * 读取绝对路径输入流
     * 
     * @param path
     * @return
     * @throws Exception
     *
     */
    public static InputStream getXMLStream(String path) throws Exception {
        path = XmlPathTool.getBinPath(path);
        InputStream stream = new FileInputStream(path);
        if(log != null)
            log.info(" getXMLStream " + " path -> " + path);
        return stream;
    }
    
}
