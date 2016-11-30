package org.unicef.rapidreg.event;

public class LoginSuccessEvent {
    private String userName;

    public LoginSuccessEvent(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
