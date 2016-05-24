package org.unicef.rapidreg.login;

import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.unicef.rapidreg.BuildConfig;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.JELLY_BEAN)
public class LoginPresenterTest {
    LoginPresenter loginPresenter;
    private LoginView loginView;


    @Before
    public void setUp() throws Exception {
        loginPresenter = new LoginPresenter();
        loginView = mock(LoginView.class);
        loginPresenter.attachView(loginView);
    }

    @Test
    public void testValidateEmptyPasswordAndPassordShouldShowError() {
        boolean valid = loginPresenter.validate(RuntimeEnvironment.application, "", "", "http://10.29.3.184:3000");
        verify(loginView).showUserNameError("Enter a valid username!");
        verify(loginView).showPasswordError("Enter a valid password!");
        assertEquals(valid, false);
    }

    @Test
    public void testValidateInvalidUserFormatShouldShowError() {
        boolean valid = loginPresenter.validate(RuntimeEnvironment.application, "primer@##/", "password", "http://10.29.3.184:3000");
        verify(loginView).showUserNameError("Enter a valid username!");
        assertEquals(valid, false);
    }

    @Test
    public void testValidateValidUserNameAndPassowrdShouldNotShowError() {
        boolean valid = loginPresenter.validate(RuntimeEnvironment.application, "primero", "password", "http://10.29.3.184:3000");
        assertEquals(valid, true);
    }
}
