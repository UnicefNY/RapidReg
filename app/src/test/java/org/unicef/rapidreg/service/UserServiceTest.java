package org.unicef.rapidreg.service;

import org.junit.BeforeClass;
import org.junit.Test;
import org.unicef.rapidreg.db.helper.UserDao;
import org.unicef.rapidreg.db.helper.impl.UserDaoImpl;
import org.unicef.rapidreg.model.User;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    private static UserDao userDBHelper;
    private static UserService userVerifier;

    private String username = "Jack";
    private String password = "123456";
    private User jack = new User(username, password);

    @BeforeClass
    public static void setup() {
        userDBHelper = mock(UserDaoImpl.class);
        userVerifier = new UserService(userDBHelper);
    }

    @Test
    public void should_be_truthy_when_user_ever_login_successfully() {
        when(userDBHelper.countUser()).thenReturn(1L);

        assertThat(userVerifier.isUserEverLoginSuccessfully(), is(true));
    }

    @Test
    public void should_be_false_when_user_never_login_successfully() {
        when(userDBHelper.countUser()).thenReturn(0L);

        assertThat(userVerifier.isUserEverLoginSuccessfully(), is(false));
    }

    @Test
    public void should_verify_when_user_does_not_exist() {
        when(userDBHelper.getUser(anyString())).thenReturn(null);

        UserService.VerifiedCode verifiedCode = userVerifier.verify(username, password);

        assertThat(verifiedCode, is(UserService.VerifiedCode.USER_DOES_NOT_EXIST));
    }

    @Test
    public void should_verify_when_user_password_is_incorrect() {
        when(userDBHelper.getUser(username)).thenReturn(jack);

        UserService.VerifiedCode verifiedCode = userVerifier.verify(username, "654321");

        assertThat(verifiedCode, is(UserService.VerifiedCode.PASSWORD_INCORRECT));
    }

    @Test
    public void should_verify_when_both_username_and_password_are_correct() {
        when(userDBHelper.getUser(username)).thenReturn(jack);

        UserService.VerifiedCode verifiedCode = userVerifier.verify(username, password);

        assertThat(verifiedCode, is(UserService.VerifiedCode.OK));
    }
}
