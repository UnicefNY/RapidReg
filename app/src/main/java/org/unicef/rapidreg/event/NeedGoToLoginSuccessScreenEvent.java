package org.unicef.rapidreg.event;

public class NeedGoToLoginSuccessScreenEvent {
    private String userName;

    public NeedGoToLoginSuccessScreenEvent(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
