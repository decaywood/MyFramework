package DecayMongo.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

import org.decaywood.resources.ResourcesPathTool;
import org.dom4j.io.DOMReader;
import org.dom4j.io.SAXReader;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * 
 * @author decaywood 2014年10月25日
 * @import dom4j.jar
 * 
 *            JAVA 解析 XML 通常有两种方式，DOM 和 SAX。DOM 虽然是 W3C的标准，提供了标准的解析方式，
 *         但它的解析效率一直不尽如人意，因为使用DOM解析XML时，解析器读入整个文档并构建一个驻留内存的树结构（节点树），
 *         然后您的代码才可以使用 DOM的标准接口来操作这个树结构。但大部分情况下我们只对文档的部分内容感兴趣，根本就不用先解析整个文档，
 *         并且从节点树的根节点来索引一些我们需要的数据也是非常耗时的。
 *            SAX是一种XML解析的替代方法。相比于文档对象模型DOM，SAX 是读取和操作 XML 数据的更快速、更轻量的方 法。
 *         SAX允许您在读取文档时处理它，从而不必等待整个文档被存储之后才采取操作。它不涉及 DOM 所必需的开销和概念跳跃。 SAX
 *         API是一个基于事件的API ，适用于处理数据流，即随着数据的流动而依次处理数据。SAX API
 *         在其解析您的文档时发生一定事件的时候会通知您。在您对其响应时，您不作保存的数据将会 被抛弃。
 *         下面是一个SAX解析XML的示例（有点长，因为详细注解了SAX事件处理的所有方法），SAX
 *         API中主要有四种处理事件的接口，它们分别是ContentHandler，DTDHandler， EntityResolver 和
 *         ErrorHandler 。下面的例子可能有点冗长，实际上只要继承DefaultHandler 类 ，再覆盖一部分 处理事件的方法
 *         同样可以达到这个示例的效果，但为了纵观全局，还是看看SAX API里面所有主要的事件解析方法吧。（
 *         实际上DefaultHandler就是实现了上面的四个事件处理器接口，然后提供了每个抽象方法的默认实现。）
 * 
 */
public class ReaderCreater {

    private static Logger log = Logger.getLogger(ReaderCreater.class.toString());
    
    public static final ErrorHandler DEFAULT_ERROR_HANDLER = new DefaultErrorHandler();
    public static final ContentHandler DEFAULT_CONTENT_HANDLER = new DefaultContentHandler();
    public static final EntityResolver DEFAULT_ENTITY_RESOLVER = new DefaultEntityResolver();

    private DOMReader domReader;
    private SAXReader saxReader;
    private XMLReader xmlReader;
    
    public XMLReader createXMLReader() throws SAXException {
        xmlReader = XMLReaderFactory.createXMLReader();
        return xmlReader;
    }
    
    public DOMReader createDOMReader() {
        domReader = new DOMReader();
        return domReader;
    }
    
    public SAXReader createSAXReader() {
        saxReader = new SAXReader();
        saxReader.setMergeAdjacentText(true);
        saxReader.setValidation(true);
        return saxReader;
    }

    private static class DefaultEntityResolver implements EntityResolver{
        /* 
         * 允许应用程序解析外部实体。 
         * 解析器将在打开任何外部实体（顶级文档实体除外）前调用此方法 
         * 参数意义如下： 
         *     publicId ： 被引用的外部实体的公共标识符，如果未提供，则为 null。 
         *     systemId ： 被引用的外部实体的系统标识符。 
         * 返回： 
         *     一个描述新输入源的 InputSource 对象，或者返回 null， 
         *     以请求解析器打开到系统标识符的常规 URI 连接。 
         *     
         *     默认定义为standard包中的dtd文件相对路径
         */  
        @Override
        public InputSource resolveEntity(String publicId, String systemId)
                throws SAXException, IOException {
            String path = null;
            try {
                path = ResourcesPathTool.getResourcePath("framework-configuration.dtd");
            } catch (Exception e) {
                e.printStackTrace();
            }
            InputSource source = new InputSource(path);
            return source;
        }
        
    }
    
    private static class DefaultDTDHandler implements DTDHandler{

        /* 
         * 接收注释声明事件的通知。 
         * 参数意义如下： 
         *     name - 注释名称。 
         *     publicId - 注释的公共标识符，如果未提供，则为 null。 
         *     systemId - 注释的系统标识符，如果未提供，则为 null。 
         */  
        @Override
        public void notationDecl(String name, String publicId, String systemId)
                throws SAXException {
            if(log != null)
                log.info(" notationDecl ： " + 
                    " name ->" + name + " publicID: " + publicId +
                    " systemID" + systemId);
        }

        /* 
         * 接收未解析的实体声明事件的通知。 
         * 参数意义如下： 
         *     name - 未解析的实体的名称。 
         *     publicId - 实体的公共标识符，如果未提供，则为 null。 
         *     systemId - 实体的系统标识符。 
         *     notationName - 相关注释的名称。 
         */  
        @Override
        public void unparsedEntityDecl(String name, String publicId,
                String systemId, String notationName) throws SAXException {
            if(log != null)
                log.info(" unparsedEntityDecl ： " + 
                    " name ->" + name + " publicID: " + publicId +
                    " systemID" + systemId +
                    "notationName" + notationName);
        }
        
    }

    private static class DefaultErrorHandler implements ErrorHandler {

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            if(log != null)
                log.info(" SAXParseException ： " + 
                    " exception ->" + exception.getMessage());
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            if(log != null)
                log.info(" error ： " + 
                    " exception ->" + exception.getMessage());
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            if(log != null)
                log.info(" fatalError ： " + 
                    " exception ->" + exception.getMessage());
        }

    }
    
    private static class DefaultContentHandler implements ContentHandler{

        
        
        public DefaultContentHandler() {
            if(log != null)
                log.info(" DefaultContentHandler ");
        }
        
        @Override
        public void setDocumentLocator(Locator locator) {
            if(log != null)
                log.info(" setDocumentLocator ");
        }

        @Override
        public void startDocument() throws SAXException {
            if(log != null)
                log.info(" startDocument ");
        }

        @Override
        public void endDocument() throws SAXException {
            if(log != null)
                log.info(" endDocument ");
        }

        @Override
        public void startPrefixMapping(String prefix, String uri)
                throws SAXException {
            if(log != null)
                log.info(" startPrefixMapping : " + 
                    " prefix -> " + prefix + " uri -> " + uri);
        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
            if(log != null)
                log.info(" endPrefixMapping : " + 
                    " prefix -> " + prefix);
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes atts) throws SAXException {
            if(log != null)
                log.info(" startElement : " +
                    "uri -> " + uri + " localName -> " + localName
                    + " qName -> " + qName);
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            if(log != null)    
                log.info(" endElement : " +
                    "uri -> " + uri + " localName -> " + localName
                    + " qName -> " + qName);
        }

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            StringBuffer buffer = new StringBuffer();  
            for(int i = start ; i < start+length ; i++){  
                switch(ch[i]){  
                    case '\\':buffer.append("\\\\");break;  
                    case '\r':buffer.append("\\r");break;  
                    case '\n':buffer.append("\\n");break;  
                    case '\t':buffer.append("\\t");break;  
                    case '\"':buffer.append("\\\"");break;  
                    default : buffer.append(ch[i]);   
                }  
            }  
            if(log != null)
                log.info(" characters : " + buffer.toString());
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length)
                throws SAXException {
            StringBuffer buffer = new StringBuffer();  
            for(int i = start ; i < start+length ; i++){  
                switch(ch[i]){  
                    case '\\':buffer.append("\\\\");break;  
                    case '\r':buffer.append("\\r");break;  
                    case '\n':buffer.append("\\n");break;  
                    case '\t':buffer.append("\\t");break;  
                    case '\"':buffer.append("\\\"");break;  
                    default : buffer.append(ch[i]);   
                }  
            }  
            if(log != null)
                log.info(" ignorableWhitespace : " + buffer.toString());
        }

        @Override
        public void processingInstruction(String target, String data)
                throws SAXException {
            if(log != null)
                log.info(" processingInstruction : "
                + " target -> " + target + " data -> " + data);
        }

        @Override
        public void skippedEntity(String name) throws SAXException {
            if(log != null)
                log.info(" skippedEntity : " + name);
        }
        
    }

}
