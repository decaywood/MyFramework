package DecayMongo.resolver;

import java.lang.reflect.Field;

import org.decaywood.resolver.decoder.AbstractDecoder;
import org.decaywood.resolver.decoder.DecoderDef;
import org.decaywood.resolver.decoder.AbstractDecoder.Initiator;
import org.decaywood.resolver.decoder.DefaultDecoder;
import org.decaywood.resolver.decoder.EmbedDecoder;
import org.decaywood.resolver.decoder.GroupEmbedDecoder;
import org.decaywood.resolver.decoder.GroupReferenceDecoder;
import org.decaywood.resolver.decoder.IDDecoder;
import org.decaywood.resolver.decoder.PropertyDecoder;
import org.decaywood.resolver.decoder.ReferenceDecoder;
import org.decaywood.resolver.encoder.AbstractEncoder;
import org.decaywood.resolver.encoder.EmbedEncoder;
import org.decaywood.resolver.encoder.GroupEmbedEncoder;
import org.decaywood.resolver.encoder.GroupReferenceEncoder;
import org.decaywood.resolver.encoder.IDEncoder;
import org.decaywood.resolver.encoder.PropertyEncoder;
import org.decaywood.resolver.encoder.ReferenceEncoder;

import com.mongodb.DBObject;

/**
 * 2014年11月3日
 * @author decaywood
 *
 */
public class DecodeManager implements Initiator {

    
    private AbstractDecoder decoderStart;
    
    /**
     * 
     */
    public DecodeManager() {
        decoderStart = new DefaultDecoder();
        decoderStart.setNextDecoder(new IDDecoder())
                    .setNextDecoder(new EmbedDecoder())
                    .setNextDecoder(new ReferenceDecoder())
                    .setNextDecoder(new GroupEmbedDecoder())
                    .setNextDecoder(new GroupReferenceDecoder())
                    .setNextDecoder(new PropertyDecoder());
        
    }
    
 
    public void addEncoder(AbstractDecoder decoder){
        if(decoder == null) return;
        decoder.setNextDecoder(decoderStart);
        decoderStart = decoder;
    }
    
    @Override
    public AbstractDecoder initDecoder(Field field, DBObject dbObject) {
        AbstractDecoder decoder = decoderStart;
        AbstractDecoder pointer = decoderStart;
        decoder.initDecoder(field, dbObject);
        while(pointer.isNext()){
            pointer = decoder;
            decoder = decoder.initDecoder(field, dbObject);
        }
        return decoder;
    }

    

}
