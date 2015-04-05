package org.onebrick.android.core;

import android.util.Log;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class OneBrickCrypt {

    private IvParameterSpec ivspec;
    private SecretKeySpec keyspec;
    private Cipher cipher;

    private String iv = "";
    private String secretKey = "";

    public OneBrickCrypt() {
        ivspec = new IvParameterSpec(iv.getBytes());
        keyspec = new SecretKeySpec(secretKey.getBytes(), "AES");

        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            Log.e("error in ciper: ", e.getMessage());
        } catch (NoSuchPaddingException e) {
            Log.e("error in ciper: ", e.getMessage());
        }
    }

    public byte[] encrypt(String email, String pwd) throws Exception {
        if(email == null || email.length() == 0 || pwd == null || pwd.length() == 0)
            throw new Exception("Empty input");

        byte[] encrypted = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            encrypted = cipher.doFinal(padString(createSeed(email, pwd)).getBytes());
        } catch (Exception e) {
            throw new Exception("[encrypt] " + e.getMessage());
        }

        return encrypted;
    }

    public byte[] decrypt(String code) throws Exception {
        if(code == null || code.length() == 0)
            throw new Exception("Empty string");

        byte[] decrypted = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            decrypted = cipher.doFinal(hexToBytes(code));
        } catch (Exception e) {
            throw new Exception("[decrypt] " + e.getMessage());
        }
        return decrypted;
    }

    public static String bytesToHex(byte[] data) {
        if (data==null) {
            return null;
        }

        int len = data.length;
        String str = "";
        for (int i=0; i<len; i++) {
            if ((data[i]&0xFF)<16)
                str = str + "0" + java.lang.Integer.toHexString(data[i]&0xFF);
            else
                str = str + java.lang.Integer.toHexString(data[i]&0xFF);
        }
        return str;
    }

    public static byte[] hexToBytes(String str) {
        if (str==null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i=0; i<len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i*2,i*2+2),16);
            }
            return buffer;
        }
    }

    private static String padString(String source)
    {
        char paddingChar = 0;
        int size = 16;
        int x = source.length() % size;
        int padLength = size - x;

        for (int i = 0; i < padLength; i++)
        {
            source += paddingChar;
        }

        return source;
    }

    private static String createSeed(String email, String pwd) {
        int EMAIL_LENGTH_PAD = 5;
        int paddingLen = 0;
        String EMPTY_STRING = " ";

        String strEmailLen = String.valueOf(email.length());
        int emailLen = strEmailLen.length();

        if (emailLen < EMAIL_LENGTH_PAD){
            paddingLen = EMAIL_LENGTH_PAD - emailLen;
        }
        return String.format("%1s%2$-" + paddingLen + "s", strEmailLen, EMPTY_STRING).concat(email).concat(pwd);
    }

//    public static void main(String args[]) {
//
//        String encrypted = "";
//        String decrypted = "";
//        String email = "";
//        String pwd = "";
//        try {
//            OneBrickCrypt onebrickCrypt = new OneBrickCrypt();
//            encrypted = OneBrickCrypt.bytesToHex( onebrickCrypt.encrypt(email, pwd) );
//            decrypted = new String(onebrickCrypt.decrypt( encrypted ));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println("encrypted: " + encrypted + " --- decrypted: " + decrypted);
//    }

}
