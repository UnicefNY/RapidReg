package org.unicef.rapidreg.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.model.User;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest(UserVerifier.class)
public class UserVerifierTest {
    private String username = "Jack";
    private String password = "123456";

    @Test
    public void should_get_user() {
        User jack = new User(username, password);
        PowerMockito.mockStatic(UserVerifier.class);
        Mockito.when(UserVerifier.getUser(username)).thenReturn(jack);

        User user = UserVerifier.getUser(username);

        assertThat(user.getUsername(), is(jack.getUsername()));
    }
}
