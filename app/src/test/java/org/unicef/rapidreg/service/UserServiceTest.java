package org.unicef.rapidreg.service;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.unicef.rapidreg.db.UserDao;
import org.unicef.rapidreg.db.impl.UserDaoImpl;
import org.unicef.rapidreg.model.User;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class UserServiceTest {
    private static UserDao userDao;
    private static UserService userService;

    private String username = "Jack";
    private String password = "123456";
    private User jack = new User(username, password);

    @BeforeClass
    public static void setup() {
        userDao = mock(UserDaoImpl.class);
        userService = new UserService(userDao);
    }

    @Test
    public void should_be_truthy_when_user_ever_login_successfully() {
        when(userDao.countUser()).thenReturn(1L);

        assertThat(userService.isUserEverLoginSuccessfully(), is(true));
    }

    @Test
    public void should_be_false_when_user_never_login_successfully() {
        when(userDao.countUser()).thenReturn(0L);

        assertThat(userService.isUserEverLoginSuccessfully(), is(false));
    }

    @Test
    public void should_verify_when_user_does_not_exist() {
        when(userDao.getUser(anyString())).thenReturn(null);

        UserService.VerifiedCode verifiedCode = userService.verify(username, password);

        assertThat(verifiedCode, is(UserService.VerifiedCode.USER_DOES_NOT_EXIST));
    }

    @Test
    public void should_verify_when_user_password_is_incorrect() {
        when(userDao.getUser(username)).thenReturn(jack);

        UserService.VerifiedCode verifiedCode = userService.verify(username, "654321");

        assertThat(verifiedCode, is(UserService.VerifiedCode.PASSWORD_INCORRECT));
    }

    @Test
    public void should_verify_when_both_username_and_password_are_correct() {
        when(userDao.getUser(username)).thenReturn(jack);

        UserService.VerifiedCode verifiedCode = userService.verify(username, password);

        assertThat(verifiedCode, is(UserService.VerifiedCode.OK));
    }

    @Test
    public void should_return_true_when_username_is_valid() {
        assertThat(userService.isNameValid("Jack"), is(true));
    }

    @Test
    public void should_return_false_when_username_is_empty() {
        assertThat(userService.isNameValid(null), is(false));
        assertThat(userService.isNameValid(""), is(false));
    }

    @Test
    public void should_return_false_when_username_is_too_long() {
        String name = "111111111111111111111111111111111111111111111111111111111111111111111111"
                + "2222222222222222222222222222222222222222222222222222222222222222222222222222"
                + "3333333333333333333333333333333333333333333333333333333333333333333333333333"
                + "4444444444444444444444444444444444444444444444444444444444444444444444444444";

        assertThat(userService.isNameValid(name), is(false));

    }

    @Test
    public void should_return_false_when_username_is_invalid() {
        assertThat(userService.isNameValid("Ja(ck"), is(false));
        assertThat(userService.isNameValid("Ja)ck"), is(false));
        assertThat(userService.isNameValid("Ja*ck"), is(false));
        assertThat(userService.isNameValid("Ja@ck"), is(false));
        assertThat(userService.isNameValid("Ja!ck"), is(false));
        assertThat(userService.isNameValid("Ja#ck"), is(false));
        assertThat(userService.isNameValid("Ja$ck"), is(false));
        assertThat(userService.isNameValid("Ja%ck"), is(false));
        assertThat(userService.isNameValid("Ja?ck"), is(false));
        assertThat(userService.isNameValid("Ja&ck"), is(false));
        assertThat(userService.isNameValid("Ja=ck"), is(false));
        assertThat(userService.isNameValid("Ja;ck"), is(false));
        assertThat(userService.isNameValid("Ja:ck"), is(false));
        assertThat(userService.isNameValid("Ja{ck"), is(false));
        assertThat(userService.isNameValid("Ja}ck"), is(false));
        assertThat(userService.isNameValid("Ja[ck"), is(false));
        assertThat(userService.isNameValid("Ja]ck"), is(false));
        assertThat(userService.isNameValid("Ja|ck"), is(false));
        assertThat(userService.isNameValid("Ja<ck"), is(false));
        assertThat(userService.isNameValid("Ja>ck"), is(false));
        assertThat(userService.isNameValid("Ja,ck"), is(false));
        assertThat(userService.isNameValid("Ja.ck"), is(false));
        assertThat(userService.isNameValid("Ja`ck"), is(false));
        assertThat(userService.isNameValid("Ja\\ck"), is(false));
    }

    @Test
    public void should_return_false_when_password_is_empty() {
        assertThat(userService.isPasswordValid(null), is(false));
        assertThat(userService.isPasswordValid(""), is(false));
    }

    @Test
    public void should_return_false_when_url_is_invalid() {
        assertThat(userService.isUrlValid(null), is(false));
        assertThat(userService.isUrlValid(""), is(false));
        assertThat(userService.isUrlValid("http//10.23.0.1"), is(false));
        assertThat(userService.isUrlValid("http://10.23.0"), is(false));
    }
}
