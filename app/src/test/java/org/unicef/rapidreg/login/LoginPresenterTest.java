package org.unicef.rapidreg.login;

import android.app.Application;
import android.os.Build;
import android.os.Environment;
import android.security.NetworkSecurityPolicy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.unicef.rapidreg.BuildConfig;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.JELLY_BEAN)
public class LoginPresenterTest {
    LoginPresenter loginPresenter;
    private LoginView loginView;


    @Before
    public void setUp() throws Exception {
        loginPresenter = new LoginPresenter(RuntimeEnvironment.application);
        loginView = mock(LoginView.class);
        loginPresenter.attachView(loginView);
    }

    @Test
    public void should_show_error_when_validate_empty_password_and_passord() {
        boolean valid = loginPresenter.validate(RuntimeEnvironment.application, "", "", "http://10.29.3.184:3000");
        verify(loginView).showUserNameError("Enter a valid username!");
        verify(loginView).showPasswordError("Enter a valid password!");
        assertEquals(valid, false);
    }

    @Test
    public void should_show_error_when_invalid_user_format() {
        boolean valid = loginPresenter.validate(RuntimeEnvironment.application, "primer@##/", "password", "http://10.29.3.184:3000");
        verify(loginView).showUserNameError("Enter a valid username!");
        assertEquals(valid, false);
    }

    @Test
    public void should_show_nothing_when_valid_username_and_password() {
        boolean valid = loginPresenter.validate(RuntimeEnvironment.application, "primero", "password", "http://10.29.3.184:3000");
        assertEquals(valid, true);
    }
}
