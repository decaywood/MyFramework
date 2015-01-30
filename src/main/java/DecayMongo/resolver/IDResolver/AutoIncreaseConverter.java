/**
 * 2014年10月29日
 */
package DecayMongo.resolver.IDResolver;

import org.decaywood.utils.StringUtil;

/**
 * 2014年10月29日
 * @author decaywood
 *
 */
public class AutoIncreaseConverter implements IDConverterDef<Long>{

    @Override
    public Long convert(String ID) {
        if(StringUtil.isEmpty(ID)) return null;
        return Long.parseLong(ID);
    }

}
