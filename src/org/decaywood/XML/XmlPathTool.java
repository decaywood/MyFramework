package org.decaywood.XML;


/**
 * 
 * 2014年10月26日
 * @author decaywood
 *
 */
public class XmlPathTool {

    public static String getBinPath(String resourcesFileName) throws Exception {
        
        String path = null;
        path = XmlPathTool.class.getResource("").getPath();
        path = path.substring(1) + resourcesFileName;
        return path;
    }
}
