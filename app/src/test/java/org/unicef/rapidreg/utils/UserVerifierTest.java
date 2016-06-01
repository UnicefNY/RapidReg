package org.unicef.rapidreg.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.db.helper.UserDBHelper;
import org.unicef.rapidreg.model.User;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(UserDBHelper.class)
public class UserVerifierTest {
    private String username = "Jack";
    private String password = "123456";
    private User jack = new User(username, password);

    @Test
    public void should_verify_when_user_does_not_exist() {
        PowerMockito.mockStatic(UserDBHelper.class);
        when(UserDBHelper.getUser(anyString())).thenReturn(null);

        UserVerifier.VerifiedCode verifiedCode = UserVerifier.verify(username, password);

        assertThat(verifiedCode, is(UserVerifier.VerifiedCode.USER_DOES_NOT_EXIST));
    }

    @Test
    public void should_verify_when_user_password_is_incorrect() {
        PowerMockito.mockStatic(UserDBHelper.class);
        when(UserDBHelper.getUser(username)).thenReturn(jack);

        UserVerifier.VerifiedCode verifiedCode = UserVerifier.verify(username, "654321");

        assertThat(verifiedCode, is(UserVerifier.VerifiedCode.PASSWORD_INCORRECT));
    }

    @Test
    public void should_verify_when_both_username_and_password_are_correct() {
        PowerMockito.mockStatic(UserDBHelper.class);
        when(UserDBHelper.getUser(username)).thenReturn(jack);

        UserVerifier.VerifiedCode verifiedCode = UserVerifier.verify(username, password);

        assertThat(verifiedCode, is(UserVerifier.VerifiedCode.OK));
    }
}
