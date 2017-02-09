package org.unicef.rapidreg.childcase;

import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.base.record.RecordPresenter;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.model.IncidentForm;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.FormRemoteService;
import org.unicef.rapidreg.service.IncidentFormService;

import javax.inject.Inject;

import rx.Observable;

import static org.unicef.rapidreg.PrimeroAppConfiguration.MODULE_ID_GBV;

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
