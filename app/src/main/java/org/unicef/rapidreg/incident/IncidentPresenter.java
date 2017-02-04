package org.unicef.rapidreg.incident;

import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.base.record.RecordPresenter;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.model.IncidentForm;
import org.unicef.rapidreg.service.FormRemoteService;
import org.unicef.rapidreg.service.IncidentFormService;

import javax.inject.Inject;

public class IncidentPresenter extends RecordPresenter {
    private IncidentFormService incidentFormService;
    private FormRemoteService formRemoteService;

    @Inject
    public IncidentPresenter(FormRemoteService authService,
                             IncidentFormService incidentFormService) {
        this.incidentFormService = incidentFormService;
        this.formRemoteService = authService;
    }

    @Override
    public void saveForm(RecordForm recordForm, String moduleId) {
        IncidentForm incidentForm = new IncidentForm(new Blob(gson.toJson(recordForm).getBytes()));
        incidentForm.setModuleId(moduleId);
        incidentFormService.saveOrUpdate(incidentForm);
    }

    public boolean isFormReady() {
        return incidentFormService.isReady();
    }

    public void loadIncidentForm(final String moduleId) {
        formRemoteService.getIncidentForm(PrimeroAppConfiguration.getCookie(),
                PrimeroAppConfiguration.getDefaultLanguage(), true, PrimeroAppConfiguration.PARENT_INCIDENT, moduleId)
                .subscribe(incidentForm -> {
                    saveForm(incidentForm, moduleId);
                    setFormSyncFail(false);
                }, throwable -> {
                    if (isViewAttached()) {
                        getView().promoteSyncFormsError();
                    }
                    setFormSyncFail(true);
                });
    }
}
