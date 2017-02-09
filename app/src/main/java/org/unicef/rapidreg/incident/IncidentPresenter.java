package org.unicef.rapidreg.incident;

import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.base.record.RecordPresenter;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.model.IncidentForm;
import org.unicef.rapidreg.service.FormRemoteService;
import org.unicef.rapidreg.service.IncidentFormService;

import javax.inject.Inject;

import rx.Observable;

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
