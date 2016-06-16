package org.unicef.rapidreg.event;

import static org.unicef.rapidreg.service.CaseFormService.FormLoadStateMachine;

public class NeedLoadFormsEvent {
    private String cookie;
    private FormLoadStateMachine stateMachine;

    public NeedLoadFormsEvent(String cookie, FormLoadStateMachine stateMachine) {
        this.cookie = cookie;
        this.stateMachine = stateMachine;
    }

    public String getCookie() {
        return cookie;
    }

    public FormLoadStateMachine getStateMachine() {
        return stateMachine;
    }
}
