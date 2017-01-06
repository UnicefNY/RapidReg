package org.unicef.rapidreg.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.db.UserDao;

import static junit.framework.Assert.assertFalse;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
public class UserServiceImplTest {
    @Mock
    UserDao userDao;

    @InjectMocks
    UserServiceImpl userService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void should_verify_whether_password_is_valid() throws Exception {
        boolean actual = userService.isPasswordValid("123");

        assertFalse(actual);
    }
}