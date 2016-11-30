package org.unicef.rapidreg.event;

public class LoadTracingFormEvent {
    private String cookie;

    public LoadTracingFormEvent(String cookie) {
        this.cookie = cookie;
    }

    public String getCookie() {
        return cookie;
    }
}
