package org.unicef.rapidreg.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatesUtils {
    static public boolean containsSpecialCharacter(String username) {
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile("[^@!#$%\\^?&*()=\\\\/;:'\"\\{\\}\\[\\]\\|<>,.`]{1,254}");
        matcher = pattern.matcher(username);
        return !matcher.matches();
    }
}
