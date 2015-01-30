package DecayMongo.annotations;

import java.lang.reflect.Field;

import DecayMongo.resolver.IDResolver.AutoGenerateConverter;
import DecayMongo.resolver.IDResolver.AutoGenerator;
import DecayMongo.resolver.IDResolver.AutoIncreaseConverter;
import DecayMongo.resolver.IDResolver.AutoIncreaseGenerator;
import DecayMongo.resolver.IDResolver.IDConverterDef;
import DecayMongo.resolver.IDResolver.IDGeneratorDef;
import DecayMongo.resolver.IDResolver.UserDefineConverter;
import DecayMongo.resolver.IDResolver.UserDefineGenerator;
import DecayMongo.utils.StringUtil;

/**
 * 
 * 2014年10月29日
 * @author decaywood
 *
 */
public enum IDSequence {
    
    
    AUTO_GENERATE(new AutoGenerator() ,new AutoGenerateConverter()),
    
    
    AUTO_INCREASE(new AutoIncreaseGenerator(), new AutoIncreaseConverter()),
    
    
    USER_DEFINE(new UserDefineGenerator(), new UserDefineConverter());
    
    
    private IDConverterDef<?> converter;
    
    private IDGeneratorDef<?> generator;
    
    
    private IDSequence(IDGeneratorDef<?> generator, IDConverterDef<?> converter) {
        
        this.generator = generator;
        
        this.converter = converter;
        
    }
    
    public void setConverter(IDConverterDef<?> converter) {
        
        this.converter = converter;
        
    }
    
    public void setGenerator(IDGeneratorDef<?> generator) {
        
        this.generator = generator;
        
    }
    
    public Object getConvertedID(String ID){
        
        if(StringUtil.isEmpty(ID)) return null;
        
        return this.converter.convert(ID);
        
    }
    
    public Object getGeneratedID(Class<?> clazz, Object value, Field field){
        
        return this.generator.generateID(clazz, value, field);
        
    }
}
