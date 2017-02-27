package org.unicef.rapidreg.event;

public class RecordsUndeletableEvent {
    private final boolean isEnabled;

    public RecordsUndeletableEvent(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
