package org.decaywood.config;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import org.decaywood.resources.ResourcesPathTool;
import org.decaywood.util.ConfigureUtil;
import org.decaywood.util.ReaderCreater;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 
 * 2014年10月24日
 * @author decaywood
 *
 */
public class ConfigReader {
    
    private HashMap<GlobalName, String> infoMap;
    private ReaderCreater readerCreater;
    
    private static final Logger log = Logger.getLogger(ConfigReader.class.toString());
    
    public ConfigReader(){
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
        processMappingXMLPath(userConfig, userName);
        if(log != null)
            log.info(" Configured SessionFactory: " + userName);
        return this;
    }
    
    private void processDatabaseInfo(Element element, String userName){
        Iterator iterator = element.elementIterator(GlobalName.DATABASE_INFO.toString());
        while (iterator.hasNext()) {
            Element dbInfo = (Element)iterator.next();
            String dbInfoName = dbInfo.attributeValue(GlobalName.DATABASE_INFO_NAME.toString());
            GlobalName globalName = GlobalName.getGlobalName(dbInfoName);
            String value = dbInfo.getText().trim();
            if(log != null)
                log.info(dbInfoName + " -> " + value);
            infoMap.put(globalName, value);
            
        }
    }
    
    private void processMappingXMLPath(Element element, String name){
        Iterator elements = element.elementIterator(GlobalName.RELATE.toString());
        while (elements.hasNext()) {
            Element relate = (Element)elements.next();
            String xmlPath = relate.attributeValue(GlobalName.RELATE_POJO_XML_PATH.toString());
            addPojoXmlPath(xmlPath);
            if(log != null)
                log.info("xmlPath" + " -> " + xmlPath);
        }
    }
    
    public void addPojoXmlPath(String resourceName){
        try {
            log.info(" Reading rela from resource -> " + resourceName);
            resourceName = ResourcesPathTool.getResourcePath(resourceName);
            InputStream inputStream = ConfigureUtil.getXMLStream(resourceName);
            SAXReader saxReader = readerCreater.createSAXReader();
            saxReader.setEntityResolver(ReaderCreater.DEFAULT_ENTITY_RESOLVER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws Exception {
        new ConfigReader().read("framework-config.xml");
    }
}
