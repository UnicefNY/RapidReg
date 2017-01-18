package org.unicef.rapidreg.service.impl;

import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.repository.UserDao;
import org.unicef.rapidreg.service.FormRemoteService;
import org.unicef.rapidreg.service.LoginService;
import org.unicef.rapidreg.utils.EncryptHelper;

import java.util.Collections;

import static junit.framework.Assert.assertFalse;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
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
    private User jack = new User(username, EncryptHelper.encrypt(password));

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void should_return_empty_string_when_no_user_exist() throws Exception {
        when(userDao.getAllUsers()).thenReturn(Collections.EMPTY_LIST);

        assertThat(loginService.getServerUrl(), is(""));
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
    public void should_return_false_when_url_is_invalid() {
        assertThat(loginService.isUrlValid(null), CoreMatchers.is(false));
        assertThat(loginService.isUrlValid(""), CoreMatchers.is(false));
        assertThat(loginService.isUrlValid("http//10.23.0.1"), CoreMatchers.is(false));
        assertThat(loginService.isUrlValid("http://10.23.0"), CoreMatchers.is(false));
    }

    @Test
    public void should_verify_whether_password_is_valid() throws Exception {
        boolean actual = loginService.isPasswordValid("123");

        assertFalse(actual);
    }

    @Test
    public void should_verify_when_user_does_not_exist() {
        when(userDao.getUser(anyString())).thenReturn(null);

        LoginService.VerifiedCode verifiedCode = loginService.verify(username, password, false);

        assertThat(verifiedCode, is(LoginService.VerifiedCode.USER_DOES_NOT_EXIST));
    }

    @Test
    public void should_verify_when_user_password_is_incorrect() {
        when(userDao.getUser(username)).thenReturn(jack);

        LoginService.VerifiedCode verifiedCode = loginService.verify(username, "654321", false);

        assertThat(verifiedCode, is(LoginService.VerifiedCode.PASSWORD_INCORRECT));
    }

    @Test
    public void should_verify_when_both_username_and_password_are_correct() {
        when(userDao.getUser(username)).thenReturn(jack);

        LoginService.VerifiedCode verifiedCode = loginService.verify(username, password, false);

        assertThat(verifiedCode, is(LoginService.VerifiedCode.OK));
    }

}