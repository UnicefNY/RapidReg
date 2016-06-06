package org.unicef.rapidreg.event;

public class NeedLoadFormSectionsEvent {
    private String cookie;

    public NeedLoadFormSectionsEvent(String cookie) {
        this.cookie = cookie;
    }

    public String getCookie() {
        return cookie;
    }
}
