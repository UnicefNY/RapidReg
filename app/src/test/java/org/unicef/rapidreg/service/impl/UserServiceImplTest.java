package org.unicef.rapidreg.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.unicef.rapidreg.repository.UserDao;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.service.UserService;
import org.unicef.rapidreg.service.impl.UserServiceImpl;
import org.unicef.rapidreg.utils.EncryptHelper;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class UserServiceImplTest {
    @Mock
    private UserDao userDao;

    private UserService userService;

    private String username = "Jack";
    private String password = "123456";
    private User jack = new User(username, EncryptHelper.encrypt(password));

    @Before
    public void setup() {
        initMocks(this);
        userService = new UserServiceImpl(userDao);
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


}
