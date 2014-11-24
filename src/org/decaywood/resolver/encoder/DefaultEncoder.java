package org.decaywood.resolver.encoder;

/**
 * 2014年11月21日
 * @author decaywood
 *
 */
public class DefaultEncoder extends AbstractEncoder{

    /**
     * 2014年11月21日
     * @author decaywood
     *
     */ 
    @Override
    public String getFieldName() { return null; }

    /**
     * 2014年11月21日
     * @author decaywood
     *
     */ 
    @Override
    public Object encode() { return null; }

    /**
     * 2014年11月21日
     * @author decaywood
     *
     */ 
    @Override
    protected void confirmMarker() {
        next = true;
    }

}
