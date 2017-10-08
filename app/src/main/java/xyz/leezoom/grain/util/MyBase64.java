/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 9/5/17 4:28 PM
 */

package xyz.leezoom.grain.util;

import java.io.IOException;
import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * @Author lee
 * @Time 9/4/17.
 */
public class MyBase64 {
    public static String stringToBASE64(String origin){
        if (origin==null) return null;
        return (new BASE64Encoder()).encode(origin.getBytes());
    }

    public static String BASE64ToString(String cipherText){
        String text=cipherText;
        if (cipherText==null) return null;
        //remove no use space
        if (cipherText.contains(" ")){
            text=cipherText.replace(" ","");
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[]bytes=decoder.decodeBuffer(text);
            return new String(bytes,"utf-8");
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
