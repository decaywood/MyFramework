package org.decaywood.resolver.encoder;

import org.decaywood.Annotations;
import org.decaywood.annotations.Property;

/**
 * 2014年11月1日
 * @author decaywood
 *
 */
public class PropertyEncoder extends AbstractEncoder {

    @Override
    protected void confirmMarker() {
        next = false;
    }

    @Override
    public String getFieldName() {
        Property annotation = field.getAnnotation(Property.class);
        String name = annotation != null ? annotation.name() : Annotations.DEFAULT_NAME;
        String fieldName = field.getName();
        fieldName = Annotations.DEFAULT_NAME.equals(name) ? fieldName : name;
        return fieldName;
    }

    @Override
    public Object encode() {
        return value;
    }

     
}
