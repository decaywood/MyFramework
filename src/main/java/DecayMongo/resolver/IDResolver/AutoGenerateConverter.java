package DecayMongo.resolver.IDResolver;

import org.bson.types.ObjectId;

import DecayMongo.utils.StringUtil;

/**
 * 
 * 2014年10月29日
 * @author decaywood
 *
 */
public class AutoGenerateConverter implements IDConverterDef<ObjectId>{
   
    @Override
    public ObjectId convert(String ID) {
        if(StringUtil.isEmpty(ID)) return null;
        return new ObjectId(ID);
    }

}
