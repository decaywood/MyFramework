package DecayMongo.resolver.decoder;

import DecayMongo.Annotations;
import DecayMongo.annotations.Property;

/**
 * 2014年11月2日
 * @author decaywood
 *
 */
public class PropertyDecoder extends AbstractDecoder{

    
//    private static final Logger log = Logger.getLogger(PropertyDecoder.class.getName());
    /**
     * @param annotationType
     */
    public PropertyDecoder() {
        super(Property.class);
    }


    @Override
    protected AbstractDecoder prepareData() {
        String fieldName = field.getName();
        Property annotation = field.getAnnotation(Property.class);
        String name = annotation != null ? annotation.name() : Annotations.DEFAULT_NAME;
        fieldName = Annotations.DEFAULT_NAME.equals(name) ? fieldName : name;
        value = dbObject.get(fieldName);
        return this;
    }


    @Override
    public void decode(Object object) {
        initiateObject(object, value);
    }
    
    
    /**
     * 2014年11月21日
     * @author decaywood
     *
     */ 
    @Override
    public boolean isNext() {
        return false;
    }
   

}
