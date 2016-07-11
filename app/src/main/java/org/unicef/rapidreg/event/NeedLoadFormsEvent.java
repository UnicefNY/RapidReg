package org.unicef.rapidreg.event;

public class NeedLoadFormsEvent {
    private String cookie;

    public NeedLoadFormsEvent(String cookie) {
        this.cookie = cookie;
    }

    public String getCookie() {
        return cookie;
    }

}
