package org.unicef.rapidreg.utils;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ValidatesUtilsTest {

    @Test
    public void should_validate_if_name_contains_special_characters() {
        assertThat(ValidatesUtils.containsSpecialCharacter("Jack"), is(false));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja(ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja)ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja*ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja@ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja!ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja#ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja$ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja%ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja?ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja&ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja=ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja\\ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja;ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja:ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja{ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja}ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja[ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja]ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja|ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja<ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja>ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja,ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja.ck"), is(true));
        assertThat(ValidatesUtils.containsSpecialCharacter("Ja`ck"), is(true));
    }
}
