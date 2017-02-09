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
    private CaseFormService caseFormService;
    private IncidentFormService incidentFormService;

    @Inject
    public CasePresenter(CaseFormService caseFormService, IncidentFormService incidentFormService) {
        this.caseFormService = caseFormService;
        this.incidentFormService = incidentFormService;
    }

    @Override
    public void saveForm(RecordForm recordForm, String moduleId) {
        Blob caseFormBlob = new Blob(gson.toJson(recordForm).getBytes());
        CaseForm caseForm = new CaseForm(caseFormBlob);
        caseForm.setModuleId(moduleId);
        caseFormService.saveOrUpdate(caseForm);
    }

    public boolean isIncidentFormReady() {
        return incidentFormService.isReady();
    }
}
