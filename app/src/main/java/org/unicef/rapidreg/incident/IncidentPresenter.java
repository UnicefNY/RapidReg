package org.unicef.rapidreg.incident;

import org.unicef.rapidreg.base.record.RecordPresenter;
import org.unicef.rapidreg.service.IncidentFormService;

import javax.inject.Inject;

public class IncidentPresenter extends RecordPresenter {
    private IncidentFormService incidentFormService;

    @Inject
    public IncidentPresenter(IncidentFormService incidentFormService) {
        this.incidentFormService = incidentFormService;
    }

    public boolean isFormReady() {
        return incidentFormService.isReady();
    }
}
