package org.unicef.rapidreg.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatesUtils {
     public static boolean containsSpecialCharacter(String username) {
        Matcher matcher;
        matcher = Pattern.compile("[^@!#$%\\^?&*()=\\\\/;:'\"\\{\\}\\[\\]\\|<>,.`]{1,254}")
                .matcher(username);

        return !matcher.matches();
    }
}
