package DecayMongo.resolver;

import java.lang.reflect.Field;

import DecayMongo.resolver.decoder.AbstractDecoder;
import DecayMongo.resolver.decoder.AbstractDecoder.Initiator;
import DecayMongo.resolver.decoder.DefaultDecoder;
import DecayMongo.resolver.decoder.EmbedDecoder;
import DecayMongo.resolver.decoder.GroupEmbedDecoder;
import DecayMongo.resolver.decoder.GroupReferenceDecoder;
import DecayMongo.resolver.decoder.IDDecoder;
import DecayMongo.resolver.decoder.PropertyDecoder;
import DecayMongo.resolver.decoder.ReferenceDecoder;

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
