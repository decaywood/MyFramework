package DecayMongo.resolver;

import java.lang.reflect.Field;

import DecayMongo.resolver.encoder.AbstractEncoder;
import DecayMongo.resolver.encoder.AbstractEncoder.Initiator;
import DecayMongo.resolver.encoder.DefaultEncoder;
import DecayMongo.resolver.encoder.EmbedEncoder;
import DecayMongo.resolver.encoder.GroupEmbedEncoder;
import DecayMongo.resolver.encoder.GroupReferenceEncoder;
import DecayMongo.resolver.encoder.IDEncoder;
import DecayMongo.resolver.encoder.PropertyEncoder;
import DecayMongo.resolver.encoder.ReferenceEncoder;


/**
 * 2014年11月1日
 * @author decaywood
 *
 */
public class EncodeManager implements Initiator{
    
    private AbstractEncoder encoderStart;
    
    public EncodeManager() {
        encoderStart = new DefaultEncoder();
        encoderStart.setNextEncoder(new IDEncoder())
                    .setNextEncoder(new EmbedEncoder())
                    .setNextEncoder(new ReferenceEncoder())
                    .setNextEncoder(new GroupEmbedEncoder())
                    .setNextEncoder(new GroupReferenceEncoder())
                    .setNextEncoder(new PropertyEncoder());
        
    }
    
 
    public void addEncoder(AbstractEncoder encoder){
        if(encoder == null) return;
        encoder.setNextEncoder(encoderStart);
        encoderStart = encoder;
    }
    
    @Override
    public AbstractEncoder initEncoder(Field field, Object object) {
        AbstractEncoder encoder = encoderStart;
        AbstractEncoder pointer = encoderStart;
        encoder.initEncoder(field, object);
        while(pointer.isNext()){
            pointer = encoder;
            encoder = encoder.initEncoder(field, object);
        }
        
        return encoder;
    }

    

}
