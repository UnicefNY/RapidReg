package org.unicef.rapidreg.utils;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;


public class EncryptHelperTest {
    private String plainText = "123456";
    @Test
    public void should_encrypt_plain_text() {
        String encryptedText = EncryptHelper.encrypt(plainText);

        assertThat(encryptedText, not(plainText));
    }

    @Test
    public void should_get_different_encrypted_text_every_time_for_same_plain_text() {
        String encryptedText1 = EncryptHelper.encrypt(plainText);
        String encryptedText2 = EncryptHelper.encrypt(plainText);
        String encryptedText3 = EncryptHelper.encrypt(plainText);

        assertThat(encryptedText1, not(encryptedText2));
        assertThat(encryptedText1, not(encryptedText3));
        assertThat(encryptedText2, not(encryptedText3));
    }

    @Test
    public void should_verify_plain_text_with_encrypted_text() {
        String encryptedText = EncryptHelper.encrypt(plainText);
        boolean isMatched = EncryptHelper.isMatched(plainText, encryptedText);

        assertThat(isMatched, is(true));
    }
}
