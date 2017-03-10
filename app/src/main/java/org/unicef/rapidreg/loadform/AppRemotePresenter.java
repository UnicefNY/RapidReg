package org.unicef.rapidreg.loadform;

import android.util.Log;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.model.IncidentForm;
import org.unicef.rapidreg.model.TracingForm;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.FormRemoteService;
import org.unicef.rapidreg.service.IncidentFormService;
import org.unicef.rapidreg.service.SystemSettingsService;
import org.unicef.rapidreg.service.TracingFormService;

import javax.inject.Inject;

import dagger.Lazy;

import static org.unicef.rapidreg.PrimeroAppConfiguration.MODULE_ID_CP;
import static org.unicef.rapidreg.PrimeroAppConfiguration.MODULE_ID_GBV;

public class AppRemotePresenter {
    private static final String TAG = AppRemotePresenter.class.getSimpleName();

    private FormRemoteService formRemoteService;
    private CaseFormService caseFormService;
    private TracingFormService tracingFormService;
    private IncidentFormService incidentFormService;
    private SystemSettingsService systemSettingsService;

    protected final Gson gson = new Gson();

    @Inject
    public AppRemotePresenter(Lazy<FormRemoteService> formRemoteService,
                              CaseFormService caseFormService,
                              TracingFormService tracingFormService,
                              IncidentFormService incidentFormService,
                              Lazy<SystemSettingsService> systemSettingsService) {
        this(formRemoteService.get(), caseFormService, tracingFormService, incidentFormService, systemSettingsService.get());
    }

    public AppRemotePresenter(FormRemoteService formRemoteService,
                              CaseFormService caseFormService,
                              TracingFormService tracingFormService,
                              IncidentFormService incidentFormService,
                              SystemSettingsService systemSettingsService) {
        this.formRemoteService = formRemoteService;
        this.caseFormService = caseFormService;
        this.tracingFormService = tracingFormService;
        this.incidentFormService = incidentFormService;
        this.systemSettingsService = systemSettingsService;
    }

    public void loadCaseForm(String moduleId, AppRemoteService.LoadCallback callback) {
        formRemoteService.getCaseForm(PrimeroAppConfiguration.getCookie(), PrimeroAppConfiguration.getDefaultLanguage
                (), true, PrimeroAppConfiguration.PARENT_CASE, moduleId)
                .subscribe(caseForm -> {
                    saveCaseForm(caseForm, moduleId);
                    callback.onSuccess();
                }, throwable -> {
                    callback.onFailure();
                });
    }

    public void saveCaseForm(RecordForm recordForm, String moduleId) {
        Blob caseFormBlob = new Blob(gson.toJson(recordForm).getBytes());
        CaseForm caseForm = new CaseForm(caseFormBlob);
        caseForm.setModuleId(moduleId);
        caseFormService.saveOrUpdate(caseForm);
    }

    public void loadTracingForm(AppRemoteService.LoadCallback callback) {
        formRemoteService.getTracingForm(PrimeroAppConfiguration.getCookie(), PrimeroAppConfiguration.getDefaultLanguage
                (), true, PrimeroAppConfiguration.PARENT_TRACING_REQUEST, MODULE_ID_CP)
                .subscribe(tracingForm -> {
                    saveTracingForm(tracingForm);
                    callback.onSuccess();
                }, throwable -> {
                    callback.onFailure();
                });
    }

    public void saveTracingForm(RecordForm recordForm) {
        Blob tracingFormBlob = new Blob(gson.toJson(recordForm).getBytes());
        TracingForm tracingForm = new TracingForm(tracingFormBlob);
        tracingForm.setModuleId(MODULE_ID_CP);
        tracingFormService.saveOrUpdate(tracingForm);
    }

    public void loadIncidentForm(AppRemoteService.LoadCallback callback) {
        formRemoteService.getIncidentForm(PrimeroAppConfiguration.getCookie(), PrimeroAppConfiguration.getDefaultLanguage
                (), true, PrimeroAppConfiguration.PARENT_INCIDENT, MODULE_ID_GBV)
                .subscribe(incidentForm -> {
                    saveIncidentForm(incidentForm);
                    callback.onSuccess();
                }, throwable -> {
                    callback.onFailure();
                });
    }

    public void saveIncidentForm(RecordForm recordForm) {
        Blob incidentFormBlob = new Blob(gson.toJson(recordForm).getBytes());
        IncidentForm incidentForm = new IncidentForm(incidentFormBlob);
        incidentForm.setModuleId(MODULE_ID_GBV);
        incidentFormService.saveOrUpdate(incidentForm);
    }

    public void loadSystemSettings() {
        systemSettingsService.getSystemSettings()
                .subscribe(systemSettings -> systemSettingsService.saveOrUpdateSystemSettings(systemSettings),
                        throwable -> Log.e(TAG, "Init system settings error->" + throwable.getMessage()),
                        () -> systemSettingsService.setGlobalSystemSettings());
    }
}
