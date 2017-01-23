package org.unicef.rapidreg;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class PrimeroAppConfigurationTest {
    private String expectedUrl = "http://35.61.65.113:8443";

    @Test
    public void should_get_base_api_when_ends_port() throws Exception {
        String url = "http://35.61.65.113:8443";
        PrimeroAppConfiguration.setApiBaseUrl(url);
        assertThat(PrimeroAppConfiguration.getApiBaseUrl(),is(expectedUrl));
    }

    @Test
    public void should_get_base_api_when_ends_with_slash() throws Exception {
        String url = "http://35.61.65.113:8443/";
        PrimeroAppConfiguration.setApiBaseUrl(url);
        assertThat(PrimeroAppConfiguration.getApiBaseUrl(),is(expectedUrl));
    }

    @Test
    public void should_get_base_api_when_ends_with_login_and_slash() throws Exception {
        String url = "http://35.61.65.113:8443/login/";
        PrimeroAppConfiguration.setApiBaseUrl(url);
        assertThat(PrimeroAppConfiguration.getApiBaseUrl(),is(expectedUrl));
    }

    @Test
    public void should_get_base_api_when_ends_with_login() throws Exception {
        String url = "http://35.61.65.113:8443/login";
        PrimeroAppConfiguration.setApiBaseUrl(url);
        assertThat(PrimeroAppConfiguration.getApiBaseUrl(),is(expectedUrl));
    }
}