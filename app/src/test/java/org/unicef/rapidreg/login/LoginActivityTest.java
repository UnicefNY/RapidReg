package org.unicef.rapidreg.login;

import android.app.Activity;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.unicef.rapidreg.BuildConfig;
import org.unicef.rapidreg.R;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.robolectric.Shadows.shadowOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,sdk = Build.VERSION_CODES.JELLY_BEAN)

public class LoginActivityTest {

    private EditText usernameEditview;
    private EditText passwordEditview;
    private Button loginButton;
    private EditText urlEditview;

    @Before
    public void setup() {
        Activity activity = Robolectric.setupActivity(LoginActivity.class);
        usernameEditview = (EditText) activity.findViewById(R.id.editview_username);
        passwordEditview = (EditText) activity.findViewById(R.id.editview_password);
        loginButton = (Button) activity.findViewById(R.id.button_login);
        urlEditview = (EditText) activity.findViewById(R.id.editview_url);
    }


    @Test
    public void invalidUserNameFormatShouldShowError() {

    }
}
