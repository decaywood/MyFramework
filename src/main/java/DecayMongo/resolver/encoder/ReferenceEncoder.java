package DecayMongo.resolver.encoder;

import org.decaywood.Annotations;
import org.decaywood.EntityDefinition;
import org.decaywood.annotations.Reference;
import org.decaywood.cache.DaosCache;
import org.decaywood.dao.BasicDao;
import org.decaywood.utils.AccessUtil;

/**
 * 2014年11月1日
 * @author decaywood
 *
 */
public class ReferenceEncoder extends AbstractEncoder {

    
    @Override
    protected void confirmMarker() {
        Reference annotation = field.getAnnotation(Reference.class);
        next = annotation == null;
    }

    
    @Override
    public String getFieldName() {
        Reference annotation = field.getAnnotation(Reference.class);
        String name = annotation != null ? annotation.name() : Annotations.DEFAULT_NAME;
        String fieldName = field.getName();
        fieldName = Annotations.DEFAULT_NAME.equals(name) ? fieldName : name;
        return fieldName;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object encode() {
        Reference annotation = field.getAnnotation(Reference.class);
        int cascade = annotation.cascade();
        EntityDefinition entity = (EntityDefinition)value;
        boolean isCreate = (cascade & Annotations.CASCADE_CREATE) != 0;
        boolean isUpdate = (cascade & Annotations.CASCADE_UPDATE) != 0;
        if(isCreate && isUpdate){
            clazz = getImplementClass(annotation);
            BasicDao dao = DaosCache.getInstance().get(clazz);
            dao.updateData(entity);
        }
        return AccessUtil.convertToDbReference(annotation, clazz, entity.getID());
    }
    
    private Class<?> getImplementClass(Reference annotation){
        if(!clazz.isInterface()) return clazz;
        if(annotation == null) return clazz;
        Class<?> tempClass = annotation.implementClass();
        return tempClass != Annotations.class ? tempClass : clazz;
    }

}
