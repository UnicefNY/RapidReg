package org.unicef.rapidreg.utils;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TextUtilsTest {
    @Test
    public void should_be_true_when_url_is_null() throws Exception {
        assertThat(TextUtils.isEmpty(null), is(true));
    }

    @Test
    public void should_be_true_when_url_is_empty() throws Exception {
        assertThat(TextUtils.isEmpty("  "), is(true));
    }

    @Test
    public void should_be_false_when_url_is_not_empty() throws Exception {
        assertThat(TextUtils.isEmpty("primero"), is(false));
    }
}