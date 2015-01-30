package DecayMongo.resolver.encoder;

import org.decaywood.Annotations;
import org.decaywood.annotations.Embed;
import org.decaywood.utils.MappingUtil;

/**
 * 2014年10月30日
 * @author decaywood
 *
 */
public class EmbedEncoder extends AbstractEncoder {

    @Override
    protected void confirmMarker() {
        Embed annotation = field.getAnnotation(Embed.class);
        next = annotation == null;
    }

    @Override
    public String getFieldName() {
        Embed annotation = field.getAnnotation(Embed.class);
        String name = annotation != null ? annotation.name() : Annotations.DEFAULT_NAME;
        String fieldName = field.getName();
        fieldName = Annotations.DEFAULT_NAME.equals(name) ? fieldName : name;
        return fieldName;
    }

    @Override
    public Object encode() {
        return MappingUtil.toDBObject(value);
    }

}
