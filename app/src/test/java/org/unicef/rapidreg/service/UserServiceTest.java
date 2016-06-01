package org.unicef.rapidreg.service;

import org.junit.BeforeClass;
import org.junit.Test;
import org.unicef.rapidreg.db.UserDao;
import org.unicef.rapidreg.db.impl.UserDaoImpl;
import org.unicef.rapidreg.model.User;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    private static UserDao userDao;
    private static UserService userVerifier;

    private String username = "Jack";
    private String password = "123456";
    private User jack = new User(username, password);

    @BeforeClass
    public static void setup() {
        userDao = mock(UserDaoImpl.class);
        userVerifier = new UserService(userDao);
    }

    @Test
    public void should_be_truthy_when_user_ever_login_successfully() {
        when(userDao.countUser()).thenReturn(1L);

        assertThat(userVerifier.isUserEverLoginSuccessfully(), is(true));
    }

    @Test
    public void should_be_false_when_user_never_login_successfully() {
        when(userDao.countUser()).thenReturn(0L);

        assertThat(userVerifier.isUserEverLoginSuccessfully(), is(false));
    }

    @Test
    public void should_verify_when_user_does_not_exist() {
        when(userDao.getUser(anyString())).thenReturn(null);

        UserService.VerifiedCode verifiedCode = userVerifier.verify(username, password);

        assertThat(verifiedCode, is(UserService.VerifiedCode.USER_DOES_NOT_EXIST));
    }

    @Test
    public void should_verify_when_user_password_is_incorrect() {
        when(userDao.getUser(username)).thenReturn(jack);

        UserService.VerifiedCode verifiedCode = userVerifier.verify(username, "654321");

        assertThat(verifiedCode, is(UserService.VerifiedCode.PASSWORD_INCORRECT));
    }

    @Test
    public void should_verify_when_both_username_and_password_are_correct() {
        when(userDao.getUser(username)).thenReturn(jack);

        UserService.VerifiedCode verifiedCode = userVerifier.verify(username, password);

        assertThat(verifiedCode, is(UserService.VerifiedCode.OK));
    }
}
