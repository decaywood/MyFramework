package org.decaywood.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * 2014年10月29日
 * @author decaywood
 *
 */
public class StringUtil {

    
    
    public static boolean isEmpty(String s){
        return s == null || s.trim().length() == 0;
    }
    
    
    public static String getExtention(String filename){
        String ext = null;
        int index = filename.lastIndexOf(".");
        if(index > 0){
            ext = filename.substring(index + 1);
        }
        return ext;
    }
    
     
    public static String encodeMD5(String s){
        if(isEmpty(s)){
            return null;
        }
        MessageDigest md = null;
        try{
            md = MessageDigest.getInstance("MD5");
        }catch (NoSuchAlgorithmException ex) {
            
            return null;
        }
        char[] hexDigits = { '0', '1', '2', '3', '4',
                             '5', '6', '7', '8', '9',
                             'A', 'B', 'C', 'D', 'E', 'F' };
        md.update(s.getBytes());
        byte[] datas = md.digest();
        int len = datas.length;
        char str[] = new char[len * 2];
        int k = 0;
        for (int i = 0; i < len; i++) {
            byte byte0 = datas[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }
    
}
