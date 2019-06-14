package com.elektra.typhoon.encryption;

import android.util.Base64;

import com.elektra.typhoon.utils.Utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
    private String key="8080808080808080";
    private String iv="8080808080808080";

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
        String encryptedText = "";
        try {
            Cipher cipher = Cipher.getInstance(cI);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), alg);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);
            encrypted = cipher.doFinal(cleartext.getBytes());
            encryptedText = new String(Base64.encode(encrypted,Base64.NO_WRAP)).trim();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return encryptedText;
    }

    public String decryptAES(String encrypted){
        byte[] decrypted = new byte[0];
        try {
            Cipher cipher = Cipher.getInstance(cI);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), alg);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
            byte[] enc = Base64.decode(encrypted.getBytes(),Base64.NO_WRAP);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);
            decrypted = cipher.doFinal(enc);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return new String(decrypted);
    }
}
