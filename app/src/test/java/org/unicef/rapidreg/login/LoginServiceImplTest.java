package org.unicef.rapidreg.login;

import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.network.AuthService;
import org.unicef.rapidreg.service.UserService;

import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
public class LoginServiceImplTest {
    @Mock
    ConnectivityManager connectivityManager;

    @Mock
    TelephonyManager telephonyManager;

    @Mock
    UserService userService;

    @Mock
    AuthService authService;

    @InjectMocks
    LoginServiceImpl loginService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void should_return_empty_string_when_no_user_exist() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.EMPTY_LIST);

        assertThat(loginService.getServerUrl(), is(""));
    }
}