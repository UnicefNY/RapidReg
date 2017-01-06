package org.unicef.rapidreg.login;

import android.content.Context;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.unicef.rapidreg.BuildConfig;
import org.unicef.rapidreg.network.AuthService;
import org.unicef.rapidreg.service.UserService;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.JELLY_BEAN)
public class LoginPresenterTest {

    @Inject
    LoginPresenter loginPresenter;

    @Mock
    private Context context;

    @Mock
    private LoginView loginView;

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        loginPresenter = new LoginPresenter(userService, authService);
        loginPresenter.attachView(loginView);
    }

    @Test
    public void should_show_error_when_validate_empty_password_and_passord() {
        when(userService.isNameValid("")).thenReturn(false);
        when(userService.isPasswordValid("")).thenReturn(false);
        when(userService.isUrlValid("http://10.29.3.184:3000")).thenReturn(true);

        boolean valid = loginPresenter.validate("", "", "http://10.29.3.184:3000");
        verify(loginView).showUserNameInvalid();
        verify(loginView).showPasswordInvalid();
        assertEquals(valid, false);
    }

    @Test
    public void should_show_error_when_invalid_user_format() {
        when(userService.isNameValid("pri mero")).thenReturn(false);
        when(userService.isPasswordValid("password")).thenReturn(true);
        when(userService.isUrlValid("http://10.29.3.184:3000")).thenReturn(true);

        boolean valid = loginPresenter.validate("pri mero", "password", "http://10.29.3.184:3000");
        verify(loginView).showUserNameInvalid();
        assertEquals(valid, false);
    }
}
