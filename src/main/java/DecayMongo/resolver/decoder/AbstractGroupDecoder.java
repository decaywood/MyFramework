package DecayMongo.resolver.decoder;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.decaywood.Annotations;
import org.decaywood.annotations.GroupReference;

/**
 * 2014年11月20日
 * @author decaywood
 *
 */
public abstract class AbstractGroupDecoder extends AbstractDecoder{

    public AbstractGroupDecoder(Class<? extends Annotation> annotationType) {
        super(annotationType);
    }

    @Override
    public void decode(Object object) {
        Class<?> type = field.getType();
        Object result = null;
        if(type.isArray()){
            result = decodeArray(type.getComponentType());
        }
        
        else if(Collection.class.isAssignableFrom(type)){
            ParameterizedType paramType = (ParameterizedType)field.getGenericType();
            Type[] types = paramType.getActualTypeArguments();
            result = decodeCollection((Class<?>)types[0]);
        }
        
        else if(Map.class.isAssignableFrom(type)){
            ParameterizedType paramType = (ParameterizedType)field.getGenericType();
            Type[] types = paramType.getActualTypeArguments();
            result = decodeMap((Class<?>)types[0], (Class<?>)types[1]);
        }
        initiateObject(object, result);
           
    }
    
    
    
    protected abstract Object decodeArray(Class<?> componentClass);
    
    
    
    protected abstract Object decodeCollection(Class<?> componentClass);
    
    
    
    protected abstract Object decodeMap(Class<?> keyClass, Class<?> valueClass);
    
    
    
    protected abstract Class<?> getGroupClass();
    protected abstract Class<?> getImplementClass();
    
    
    protected Class<?> getComponentImplementClass(Annotation annotation, Class<?> componentClass){
        
        if(!componentClass.isInterface()) return componentClass;
        if(annotation == null) return componentClass;
        Class<?> implementClass = getImplementClass();
        return implementClass != Annotations.class ? implementClass : componentClass;
        
    }
    
    protected Class<?> getCollectionImplementClass(){
        
        boolean exist = getGroupClass() != Annotations.class;
        return exist ? getGroupClass() : ArrayList.class;
        
    }
    
    protected Class<?> getMapImplementClass(){
        
        boolean exist = getGroupClass() != Annotations.class;
        return exist ? getGroupClass() : HashMap.class;
        
    }

}
