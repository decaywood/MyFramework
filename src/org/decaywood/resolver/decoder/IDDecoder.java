package org.decaywood.resolver.decoder;

import org.decaywood.MongoDBconstant;
import org.decaywood.annotations.ID;

/**
 * 2014年11月2日
 * @author decaywood
 *
 */
public class IDDecoder extends AbstractDecoder{

    /**
     * @param annotationType
     */
    public IDDecoder() {
        super(ID.class);
    }

    @Override
    public void decode(Object object) {
        initiateObject(object, value.toString());
    }

    @Override
    protected AbstractDecoder prepareData() {
        value = dbObject.get(MongoDBconstant.ID);
        return this;
    }

}
