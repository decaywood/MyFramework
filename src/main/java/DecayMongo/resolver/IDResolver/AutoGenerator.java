package DecayMongo.resolver.IDResolver;

import java.lang.reflect.Field;

import org.bson.types.ObjectId;

/**
 * 2014年10月30日
 * @author decaywood
 *
 */
public class AutoGenerator implements IDGeneratorDef<ObjectId> {

    @Override
    public ObjectId generateID(Class<?> clazz, Object value, Field field) {
        if(value == null) return new ObjectId();
        if(!ObjectId.isValid( value.toString())) return new ObjectId();
        return new ObjectId(value.toString());
    }

}
