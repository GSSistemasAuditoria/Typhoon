package com.elektra.typhoon.encryption;

import android.util.Base64;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Proyecto: TYPHOON
 * Autor: Francis Susana Carreto Espinoza
 * Fecha: 11/01/2019.
 * Empresa: Elektra
 * Area: Auditoria Sistemas y Monitoreo de Alarmas
 */

public class Encryption {
    private Key publicKey = null;
    private Key privateKey = null;
    private final static String alg = "AES";
    private final static String cI = "AES/CBC/PKCS5Padding";
    String key="8080808080808080";
    String iv="8080808080808080";

    public Encryption(){
    }

    public static byte[] HexStringToByteArray(String s){
        byte data[] = new byte[s.length()/2];
        for(int i=0;i < s.length();i+=2) {
            data[i/2] = (Integer.decode("0x"+s.charAt(i)+s.charAt(i+1))).byteValue();
        }
        return data;
    }

    public String encryptAES(String cleartext){
        byte[] encrypted = new byte[0];
        try {
            Cipher cipher = Cipher.getInstance(cI);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), alg);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);
            encrypted = cipher.doFinal(cleartext.getBytes());;
        }catch (Exception e){
        }
        return new String(Base64.encode(encrypted,Base64.DEFAULT));
    }

    public String decryptAES(String encrypted){
        byte[] decrypted = new byte[0];
        try {
            Cipher cipher = Cipher.getInstance(cI);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), alg);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
            byte[] enc = Base64.decode(encrypted.getBytes(),Base64.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);
            decrypted = cipher.doFinal(enc);
        }catch (Exception e){
        }
        return new String(decrypted);
    }
}
