package org.unicef.rapidreg.utils;

import org.mindrot.jbcrypt.BCrypt;

public class EncryptHelper {
    private static DesUtils desUtils = new DesUtils("primero");

    public static String encrypt(String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt(12));
    }

    public static boolean isMatched(String plainText, String hashedText) {
        return BCrypt.checkpw(plainText, hashedText);
    }

    public static String desEncrypt(String plainText) throws Exception {
            return desUtils.encrypt(plainText);
    }

    public static String desDecrypt(String encryptedText) throws Exception {
        return desUtils.decrypt(encryptedText);
    }
}

