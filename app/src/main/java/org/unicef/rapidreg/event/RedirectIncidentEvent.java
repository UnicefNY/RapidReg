package org.unicef.rapidreg.event;

public class RedirectIncidentEvent {

    private final String incidentVal;

    public RedirectIncidentEvent(String incidentVal) {
        this.incidentVal = incidentVal;
    }

    public String getIncidentInfo() {
        return incidentVal;
    }
}
