package DecayMongo.resolver.decoder;

import DecayMongo.Annotations;
import DecayMongo.EntityDefinition;
import DecayMongo.annotations.Reference;
import DecayMongo.cache.ConstructorsCache;
import DecayMongo.cache.DaosCache;
import DecayMongo.dao.ToolDao;
import DecayMongo.utils.AccessUtil;

/**
 * 2014年11月3日
 * @author decaywood
 *
 */
public class ReferenceDecoder extends AbstractDecoder{

    /**
     * @param annotationType
     */
    public ReferenceDecoder() {
        super(Reference.class);
    }

 
    @Override
    public void decode(Object object) {
        Reference annotation = field.getAnnotation(Reference.class);
        EntityDefinition entity = null;
        boolean canRead = (annotation.cascade() & Annotations.CASCADE_RETRIEVE) != 0;
        entity = canRead ? getReferenceEntity() : getTheNewEntity();
        initiateObject(object, entity);
    }

    @Override
    protected AbstractDecoder prepareData() {
        String fieldName = field.getName();
        Reference annotation = field.getAnnotation(Reference.class);
        String name = annotation.name();
        fieldName = Annotations.DEFAULT_NAME.equals(name) ? fieldName : name;
        value = dbObject.get(fieldName);
        return this;
    }
    
    protected EntityDefinition getReferenceEntity(){
        Reference annotation = field.getAnnotation(Reference.class);
        String referenceID = AccessUtil.fromDBReference(annotation, value);
        Class<?> implementClass = getImplementClass(annotation, field.getType());
        ToolDao<?> dao = DaosCache.getInstance().get(implementClass);
        return (EntityDefinition) dao.findOne(referenceID);
    }
    
    protected EntityDefinition getTheNewEntity(){
        try {
            Reference annotation = field.getAnnotation(Reference.class);
            String referenceID = AccessUtil.fromDBReference(annotation, value);
            Class<?> implementClass = getImplementClass(annotation, field.getType());
            EntityDefinition entity = (EntityDefinition) ConstructorsCache.getInstance().get(implementClass).newInstance();
            entity.setID(referenceID);
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private Class<?> getImplementClass(Reference annotation, Class<?> fieldClass){
        if(!fieldClass.isInterface()) return fieldClass;
        if(annotation == null) return fieldClass;
        Class<?> tempClass = annotation.implementClass();
        return tempClass != Annotations.class ? tempClass : fieldClass;
    }

}
