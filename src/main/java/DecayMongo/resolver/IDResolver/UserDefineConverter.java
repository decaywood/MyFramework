package DecayMongo.resolver.IDResolver;

import org.decaywood.utils.StringUtil;

/**
 * 2014年10月29日
 * @author decaywood
 *
 */
public class UserDefineConverter implements IDConverterDef<String> {

    @Override
    public String convert(String ID) {
        if(StringUtil.isEmpty(ID)) return null;
        return ID;
    }

}
