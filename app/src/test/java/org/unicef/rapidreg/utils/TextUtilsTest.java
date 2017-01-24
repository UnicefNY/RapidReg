package org.unicef.rapidreg.utils;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.unicef.rapidreg.PrimeroAppConfiguration;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TextUtilsTest {
    private String expectedUrl = "http://35.61.65.113:8443/";

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

    @Test
    public void should_get_base_api_when_ends_port() throws Exception {
        String url = "http://35.61.65.113:8443";
        PrimeroAppConfiguration.setApiBaseUrl(url);
        assertThat(PrimeroAppConfiguration.getApiBaseUrl(), Is.is(expectedUrl));
    }

    @Test
    public void should_get_base_api_when_ends_with_slash() throws Exception {
        String url = "http://35.61.65.113:8443/";
        PrimeroAppConfiguration.setApiBaseUrl(url);
        assertThat(PrimeroAppConfiguration.getApiBaseUrl(), Is.is(expectedUrl));
    }
}