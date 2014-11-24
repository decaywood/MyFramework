package org.decaywood.resolver;

import java.lang.reflect.Field;

import org.decaywood.resolver.encoder.AbstractEncoder;
import org.decaywood.resolver.encoder.AbstractEncoder.Initiator;
import org.decaywood.resolver.encoder.DefaultEncoder;
import org.decaywood.resolver.encoder.EmbedEncoder;
import org.decaywood.resolver.encoder.GroupEmbedEncoder;
import org.decaywood.resolver.encoder.GroupReferenceEncoder;
import org.decaywood.resolver.encoder.IDEncoder;
import org.decaywood.resolver.encoder.PropertyEncoder;
import org.decaywood.resolver.encoder.ReferenceEncoder;


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
