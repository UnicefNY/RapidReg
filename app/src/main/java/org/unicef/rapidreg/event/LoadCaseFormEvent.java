package org.unicef.rapidreg.event;

public class LoadCaseFormEvent {
    private String cookie;

    public LoadCaseFormEvent(String cookie) {
        this.cookie = cookie;
    }

    public String getCookie() {
        return cookie;
    }
}
