package org.unicef.rapidreg.childcase;

import org.unicef.rapidreg.base.record.RecordPresenter;
import org.unicef.rapidreg.service.IncidentFormService;

import javax.inject.Inject;

public class CasePresenter extends RecordPresenter {
    private IncidentFormService incidentFormService;

    @Inject
    public CasePresenter(IncidentFormService incidentFormService) {
        this.incidentFormService = incidentFormService;
    }

    public boolean isIncidentFormReady() {
        return incidentFormService.isReady();
    }
}
