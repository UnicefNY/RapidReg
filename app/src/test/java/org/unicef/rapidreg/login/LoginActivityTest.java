package org.unicef.rapidreg.login;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.maven.artifact.ant.shaded.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Ignore;
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

import static org.hamcrest.CoreMatchers.nullValue;
import static org.robolectric.Shadows.shadowOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.JELLY_BEAN)
public class LoginActivityTest {

    private EditText usernameEditView;
    private EditText passwordEditView;
    private Button loginButton;
    private EditText urlEditView;

    @Before
    public void setup() {
        Activity activity = Robolectric.setupActivity(LoginActivity.class);
        usernameEditView = (EditText) activity.findViewById(R.id.editview_username);
        passwordEditView = (EditText) activity.findViewById(R.id.editview_password);
        loginButton = (Button) activity.findViewById(R.id.button_login);
        urlEditView = (EditText) activity.findViewById(R.id.editview_url);
    }

//    @Test // TODO: loginButton.performClick() Not work, need mock to solve
//    public void should_login_success_when_correct_credential() {
//        usernameEditView.setText("primero");
//        passwordEditView.setText("qu01n23!");
//        if (urlEditView != null) {
//            urlEditView.setText("http://10.29.3.184:3000");
//        }
//
//        loginButton.performClick();
//        ShadowApplication application = shadowOf(RuntimeEnvironment.application);
//        assertThat("Next activity has started", application.getNextStartedActivity(), is(notNullValue()));
//    }

    @Test
    public void should_show_error_empty_user_and_password() {

        usernameEditView.setText("");
        passwordEditView.setText("");

        loginButton.performClick();

        ShadowApplication application = shadowOf(RuntimeEnvironment.application);
        assertThat("Next activity should not started", application.getNextStartedActivity(), is(nullValue()));
        assertThat("Show error for username field ", usernameEditView.getError(), is(CoreMatchers.notNullValue()));
        assertThat("Show error for Password field ", passwordEditView.getError(), is(CoreMatchers.notNullValue()));
    }

    @Test
    public void should_show_error_when_invalid_user_name_format() {

        Map<String, String> nameList = new HashMap<String, String>();
        nameList.put("NAME_CONTAINS_SPECAIL", "primer@/?");
        nameList.put("NAME_TOO_LONG", StringUtils.leftPad("", 255, "a"));

        assertAllInvalidUserNameFormatShouldFail(nameList);


    }
//    @Test
//    public void should_login_fail_when_invalid_account() {
//        usernameEditView.setText("invalidUserName");
//        passwordEditView.setText("invalidPassword");
//        if (urlEditView == null)
//            urlEditView.setText("http://10.29.3.184:3000");
//
//        loginButton.performClick();
//        ShadowApplication application = shadowOf(RuntimeEnvironment.application);
//        assertThat("Next activity should not started", application.getNextStartedActivity(), is(nullValue()));
//    }

    private void assertAllInvalidUserNameFormatShouldFail(Map<String, String> nameList) {
        for (Map.Entry<String, String> nameEntry : nameList.entrySet()) {
            usernameEditView.setText(nameEntry.getValue());
            passwordEditView.setText("qu01n23!");
            if (urlEditView == null) {
                urlEditView.setText("http://10.29.3.184:3000");
            }

            loginButton.performClick();
            ShadowApplication application = shadowOf(RuntimeEnvironment.application);
            assertThat("Next activity should not started", application.getNextStartedActivity(), is(nullValue()));
            assertThat("Show error for " + nameEntry.getKey(), usernameEditView.getError(), is(CoreMatchers.notNullValue()));
        }

    }
}
