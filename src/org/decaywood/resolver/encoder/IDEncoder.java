package org.decaywood.resolver.encoder;

import org.decaywood.MongoDBconstant;
import org.decaywood.annotations.ID;

/**
 * 2014年10月30日
 * @author decaywood
 *
 */
public class IDEncoder extends AbstractEncoder {

    
    @Override
    protected void confirmMarker() {
        ID annotation = field.getAnnotation(ID.class);
        next = annotation == null;
    }

    @Override
    public String getFieldName() {
        return MongoDBconstant.ID;
    }

    @Override
    public Object encode() {
        ID annotation = field.getAnnotation(ID.class);
        return annotation.type().getGeneratedID(clazz, value, field);
    }
    
    
    public boolean isEmpty() {
        return false;
    }

}
