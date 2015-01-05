package org.decaywood.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 * 2014年11月21日
 * @author decaywood
 *
 */
public class BasicTypeChecker {
    
    private ArrayList<Class<?>> basicTypes;
 
    
 
    private BasicTypeChecker() {
 
        /**
         * integer 、Long、Short、Byte、Character、Double、Float、Boolean、BigInteger、BigDecmail
         */
        basicTypes = new ArrayList<Class<?>>();
        basicTypes.add(char.class);
        basicTypes.add(byte.class);
        basicTypes.add(short.class);
        basicTypes.add(int.class);
        basicTypes.add(long.class);
        basicTypes.add(float.class);
        basicTypes.add(double.class);
        basicTypes.add(boolean.class);
        basicTypes.add(Integer.class);
        basicTypes.add(Long.class);
        basicTypes.add(Short.class);
        basicTypes.add(Byte.class);
        basicTypes.add(Character.class);
        basicTypes.add(Double.class);
        basicTypes.add(Float.class);
        basicTypes.add(Boolean.class);
        basicTypes.add(BigInteger.class);
        basicTypes.add(BigDecimal.class);
    }
    
    private static class InnerClass {
        final static BasicTypeChecker instance = new BasicTypeChecker();
    } 
    
    public static BasicTypeChecker getInstance(){
        return InnerClass.instance;
    }
    
    public boolean isBasicType(Class<?> clazz){
        return basicTypes.contains(clazz);
    }
}
