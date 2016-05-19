package org.unicef.rapidreg.login;

import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.unicef.rapidreg.BuildConfig;
import org.unicef.rapidreg.R;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.robolectric.Shadows.shadowOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
/**
 * Created by wyqin on 5/19/16.
 */
public class LoginActivityTest {

    private EditText usernameEditview;
    private EditText passwordEditview;
    private Button loginButton;
    private TextView urlEditview;

    @Before
    public void setup() {
        Activity activity = Robolectric.setupActivity(LoginActivity.class);
        usernameEditview = (EditText) activity.findViewById(R.id.editview_username);
        passwordEditview = (EditText) activity.findViewById(R.id.editview_password);
        loginButton = (Button) activity.findViewById(R.id.button_login);
        urlEditview = (TextView) activity.findViewById(R.id.editview_url);
    }

    @Test
    public void emptyUserAndPasswordShouldShowError() {
        loginButton.performClick();

        ShadowApplication application = shadowOf(RuntimeEnvironment.application);
        assertThat("Next activity should not started", application.getNextStartedActivity(), is(nullValue()));
        assertThat("Show error for user field ", usernameEditview.getError(), is(CoreMatchers.notNullValue()));
        assertThat("Show error for Password field ", passwordEditview.getError(), is(CoreMatchers.notNullValue()));
    }

    @Test
    public void invalidUserNameFormatShouldShowError() {


    }

}
