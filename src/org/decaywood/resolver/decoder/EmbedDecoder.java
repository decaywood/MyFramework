package org.decaywood.resolver.decoder;

import org.decaywood.annotations.Embed;
import org.decaywood.constant.Annotations;
import org.decaywood.utils.MappingUtil;

import com.mongodb.DBObject;

/**
 * 2014年11月2日
 * @author decaywood
 *
 */
public class EmbedDecoder extends AbstractDecoder{

    
    public EmbedDecoder() {
        super(Embed.class);
    }

    @Override
    public void decode(Object object) {
        Object valueObject = MappingUtil.fromDBObject(field.getType(), (DBObject)value);
        initiateObject(object, valueObject);
    }
    @Override
    protected AbstractDecoder prepareData() {
        String fieldName = field.getName();
        Embed annotation = field.getAnnotation(Embed.class);
        String name = annotation.name();
        fieldName = Annotations.DEFAULT_NAME.equals(name) ? fieldName : name;
        value = dbObject.get(fieldName);
        return this;
    }
    
}
