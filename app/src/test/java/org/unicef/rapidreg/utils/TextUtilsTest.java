package org.unicef.rapidreg.utils;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.unicef.rapidreg.PrimeroAppConfiguration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
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

    @Test
    public void should_return_null_when_unique_id_is_null() throws Exception {
        assertThat("Should return null", TextUtils.getLastSevenNumbers(null), is(nullValue()));
    }

    @Test
    public void should_return_unique_id_when_unique_id_length_less_7() throws Exception {
        String uniqueId = "123456";

        assertThat("Should return uniqueId", TextUtils.getLastSevenNumbers(uniqueId), is(uniqueId));
    }

    @Test
    public void should_return_last_seven_numbers_when_unique_id_more_7() throws Exception {
        String uniqueId = "1234567890";

        assertThat("Should return last 7 numbers", TextUtils.getLastSevenNumbers(uniqueId), is("4567890"));
    }

    @Test
    public void should_return_expected_url_when_lint_url() throws Exception {
        String url = "http://35.61.65.113:8443";

        assertThat("Should return expected url", TextUtils.lintUrl(url), is(expectedUrl));
    }

    @Test
    public void should_return_url_when_lint_url() throws Exception {
        String url = "http://35.61.65.113:8443/";

        assertThat("Should return url", TextUtils.lintUrl(url), is(url));
    }

    @Test
    public void should_truncate_string_by_double_colon_when_source_level_smaller_than_2() {
        String result =  TextUtils.truncateByDoubleColons("L0", 2);

        assertThat(result, is("L0"));
    }

    @Test
    public void should_truncate_string_by_double_colon_when_source_level_equals_2() {
        String result = TextUtils.truncateByDoubleColons("L0::L1", 2);

        assertThat(result, is("L0::L1"));
    }

    @Test
    public void should_truncate_string_by_double_colon_when_source_level_larger_than_2() {
        String result = TextUtils.truncateByDoubleColons("L0::L1::L2::L3", 2);

        assertThat(result, is("L2::L3"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_truncate_string_by_double_colon_when_target_level_smaller_than_0() {
        String result = TextUtils.truncateByDoubleColons("L0::L1::L2::L3", -1);

        assertThat(result, is("L2::L3"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_truncate_string_by_double_colon_when_target_level_equals_0() {
        String result = TextUtils.truncateByDoubleColons("L0::L1::L2::L3", 0);

        assertThat(result, is("L2::L3"));
    }
}