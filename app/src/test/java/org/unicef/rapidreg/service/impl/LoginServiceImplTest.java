package org.unicef.rapidreg.service.impl;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.repository.UserDao;
import org.unicef.rapidreg.service.FormRemoteService;
import org.unicef.rapidreg.service.LoginService;
import org.unicef.rapidreg.utils.EncryptHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class LoginServiceImplTest {
    @Mock
    ConnectivityManager connectivityManager;

    @Mock
    TelephonyManager telephonyManager;

    @Mock
    UserDao userDao;

    @Mock
    FormRemoteService authService;

    @InjectMocks
    LoginServiceImpl loginService;

    private String username = "Jack";
    private String password = "123456";
    private String expectedUrl = "http://35.61.65.113:8443/";
    private User jack = new User(username, EncryptHelper.encrypt(password));

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void should_return_is_not_online_when_network_is_null() throws Exception {
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(null);
        assertThat("Should return false", loginService.isOnline(), is(false));
    }

    @Test
    public void should_return_is_not_online_when_network_is_not_available() throws Exception {
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(networkInfo.isAvailable()).thenReturn(false);
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        assertThat("Should return false", loginService.isOnline(), is(false));
    }

    @Test
    public void should_return_is_not_online_when_network_is_not_connected() throws Exception {
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(networkInfo.isConnected()).thenReturn(false);
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        assertThat("Should return false", loginService.isOnline(), is(false));
    }

    @Test
    public void should_return_is_online_when_network_is_ok() throws Exception {
        NetworkInfo networkInfo = mock(NetworkInfo.class);
        when(networkInfo.isAvailable()).thenReturn(true);
        when(networkInfo.isConnected()).thenReturn(true);
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        assertThat("Should return true", loginService.isOnline(), is(true));
    }

    @Test
    public void should_login_offline_when_fail() {
        LoginService.LoginCallback loginCallback = mock(LoginService.LoginCallback.class);
        when(userDao.getUser(anyString(), anyString())).thenReturn(null);

        loginService.loginOffline(username, password, expectedUrl, loginCallback);

        verify(loginCallback, times(1)).onFailed(null);
        verify(userDao, times(1)).getUser(username, expectedUrl);
    }

    @Test
    public void should_login_offline_when_error() throws Exception {
        LoginService.LoginCallback loginCallback = mock(LoginService.LoginCallback.class);
        when(userDao.getUser(anyString(), anyString())).thenReturn(jack);

        loginService.loginOffline(username, "wrong pass workd", expectedUrl, loginCallback);

        verify(loginCallback, times(1)).onError();
        verify(userDao, times(1)).getUser(username, expectedUrl);
    }

    @Test
    public void should_login_offline_successfully() throws Exception {
        LoginService.LoginCallback loginCallback = mock(LoginService.LoginCallback.class);
        when(userDao.getUser(anyString(), anyString())).thenReturn(jack);

        loginService.loginOffline(username, password, expectedUrl, loginCallback);

        verify(loginCallback, times(1)).onSuccessful("", jack);
        verify(userDao, times(1)).getUser(username, expectedUrl);
    }

    @Test
    public void should_return_true_when_username_is_valid() {
        assertThat(loginService.isUsernameValid("Jack"), CoreMatchers.is(true));
    }

    @Test
    public void should_return_false_when_username_is_empty() {
        assertThat(loginService.isUsernameValid(null), CoreMatchers.is(false));
        assertThat(loginService.isUsernameValid(""), CoreMatchers.is(false));
    }

    @Test
    public void should_return_false_when_username_is_invalid() {
        assertThat(loginService.isUsernameValid("Ja ck"), CoreMatchers.is(false));
    }

    @Test
    public void should_return_false_when_password_is_empty() {
        assertThat(loginService.isPasswordValid(null), CoreMatchers.is(false));
        assertThat(loginService.isPasswordValid(""), CoreMatchers.is(false));
    }

    @Test
    public void should_verify_whether_password_is_valid() throws Exception {
        boolean actual = loginService.isPasswordValid("ab12c34*");
        assertThat("Should return password is true", actual, is(true));
    }

    @Test
    public void should_return_false_when_password_is_invalid() throws Exception {
        assertThat("Should return password is false", loginService.isPasswordValid("123"), is(false));
    }

    @Test
    public void should_return_false_when_url_is_invalid() {
        assertThat(loginService.isUrlValid(null), CoreMatchers.is(false));
        assertThat(loginService.isUrlValid(""), CoreMatchers.is(false));
        assertThat(loginService.isUrlValid("http//10.23.0.1"), CoreMatchers.is(false));
        assertThat(loginService.isUrlValid("http://10.23.0"), CoreMatchers.is(false));
    }

    @Test
    public void should_return_true_when_url_is_valid() throws Exception {
        assertThat("should return url is true", loginService.isUrlValid("http://10.29.2.190:3000"), is(true));
    }
}