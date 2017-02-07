package org.unicef.rapidreg.event;

public class RedirectIncidentEvent implements Event {

    private final String incidentVal;

    public RedirectIncidentEvent(String incidentVal) {
        this.incidentVal = incidentVal;
    }

    public String getIncidentInfo() {
        return incidentVal;
    }
}
