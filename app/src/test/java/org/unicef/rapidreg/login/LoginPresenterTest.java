package org.unicef.rapidreg.login;

import android.content.Context;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.unicef.rapidreg.BuildConfig;
import org.unicef.rapidreg.service.LoginService;
import org.unicef.rapidreg.service.SystemSettingsService;

import javax.inject.Inject;

import dagger.Lazy;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class LoginPresenterTest {

    @Inject
    LoginPresenter loginPresenter;

    @Mock
    private Context context;

    @Mock
    private LoginService loginService;

    @Mock
    private SystemSettingsService systemSettingService;

    @Mock
    private LoginView loginView;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        loginPresenter = new LoginPresenter(() -> loginService, () -> systemSettingService);
        loginPresenter.attachView(loginView);
    }

    @Test
    public void should_show_error_when_validate_empty_password_and_password() {
        when(loginService.isUsernameValid("")).thenReturn(false);
        when(loginService.isPasswordValid("")).thenReturn(false);
        when(loginService.isUrlValid("http://10.29.3.184:3000")).thenReturn(true);

        boolean valid = loginPresenter.validate("", "", "http://10.29.3.184:3000");
        assertEquals(valid, false);
    }

    @Test
    public void should_show_error_when_invalid_user_format() {
        when(loginService.isUsernameValid("pri mero")).thenReturn(false);
        when(loginService.isPasswordValid("password")).thenReturn(true);
        when(loginService.isUrlValid("http://10.29.3.184:3000")).thenReturn(true);

        boolean valid = loginPresenter.validate("pri mero", "password", "http://10.29.3.184:3000");
        verify(loginView).showUserNameInvalid();
        assertEquals(valid, false);
    }
}
