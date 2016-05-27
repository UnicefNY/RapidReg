package org.unicef.rapidreg.event;

public class NeedLoadFormSectionsEvent {
    public String cookie;

    public NeedLoadFormSectionsEvent(String cookie) {
        this.cookie = cookie;
    }
}
