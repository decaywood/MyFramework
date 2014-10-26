package org.decaywood.resources;

/**
 * 
 * 2014年10月26日
 * @author decaywood
 *
 * 用于寻找当前包下的DTD文件路径，原理为当前类编译后所在位置
 * 路径与文件位置逻辑目录基本一致，只有bin和src这个文件名的区别
 *
 */
public class ResourcesPathTool {

    public static String getResourcePath(String resourcesFileName){
        
        String path = null;
        path = ResourcesPathTool.class.getResource("").getPath();
        path = path.replaceAll("/bin/", "/src/").substring(1) + resourcesFileName;
        return path;
    }
    
}
