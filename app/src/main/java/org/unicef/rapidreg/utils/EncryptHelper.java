package org.unicef.rapidreg.utils;

import org.mindrot.jbcrypt.BCrypt;

public class EncryptHelper {

    public static String encrypt(String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt(12));
    }

    public static boolean isMatched(String plainText, String hashedText) {
        return BCrypt.checkpw(plainText, hashedText);
    }
}
