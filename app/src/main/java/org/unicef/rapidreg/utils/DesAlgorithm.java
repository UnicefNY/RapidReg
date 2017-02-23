package org.unicef.rapidreg.utils;

import java.security.Key;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class DesAlgorithm {
    private static final String DES_KEY = "primero";

    private Cipher encryptCipher = null;
    private Cipher decryptCipher = null;

    private static DesAlgorithm instance;

    public static DesAlgorithm getInstance() {
        if (instance == null) {
            synchronized (DesAlgorithm.class) {
                if (instance == null) {
                    instance = new DesAlgorithm(DES_KEY);
                }
            }
        }
        return instance;
    }

    private DesAlgorithm(String secretKey) {
        Key key;
        try {
            key = getKey(secretKey.getBytes());
            encryptCipher = Cipher.getInstance("DES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            decryptCipher = Cipher.getInstance("DES");
            decryptCipher.init(Cipher.DECRYPT_MODE, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String desEncrypt(String plainText) throws Exception {
        return encrypt(plainText);
    }

    private String encrypt(String strIn) {
        return byteArr2HexStr(encrypt(strIn.getBytes()));
    }

    private byte[] encrypt(byte[] arrB) {
        try {
            return encryptCipher.doFinal(arrB);
        } catch (IllegalBlockSizeException e) {
            return new byte[]{};
        } catch (BadPaddingException e) {
            return new byte[]{};
        }
    }

    private String byteArr2HexStr(byte[] arrB) {
        int iLen = arrB.length;
        StringBuffer sb = new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++) {
            int intTmp = arrB[i];
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16));
        }
        return sb.toString();
    }

    private Key getKey(byte[] arrBTmp) {
        byte[] arrB = new byte[8];
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }
        Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");
        return key;
    }
}
