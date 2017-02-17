package org.unicef.rapidreg.utils;

import org.mindrot.jbcrypt.BCrypt;

public class EncryptHelper {

    private static final String DES_KEY = "primero";

    public static String encrypt(String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt(12));
    }

    public static boolean isMatched(String plainText, String hashedText) {
        return BCrypt.checkpw(plainText, hashedText);
    }

    public static String desEncrypt(String plainText) throws Exception {
            return new DesUtils(DES_KEY).encrypt(plainText);
    }

    public static String desDecrypt(String encryptedText) throws Exception {
        return new DesUtils(DES_KEY).decrypt(encryptedText);
    }
}

