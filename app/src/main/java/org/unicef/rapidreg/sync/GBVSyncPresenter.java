package org.unicef.rapidreg.sync;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.util.Pair;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.injection.ActivityContext;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.model.IncidentForm;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.FormRemoteService;
import org.unicef.rapidreg.service.IncidentFormService;
import org.unicef.rapidreg.service.IncidentService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.SyncCaseService;
import org.unicef.rapidreg.service.SyncIncidentService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.TextUtils;
import org.unicef.rapidreg.utils.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.Lazy;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static org.unicef.rapidreg.PrimeroAppConfiguration.MODULE_ID_GBV;
import static org.unicef.rapidreg.model.Incident.COLUMN_INCIDENT_CASE_ID;

public class GBVSyncPresenter extends BaseSyncPresenter {
    private static final String TAG = GBVSyncPresenter.class.getSimpleName();

    private SyncCaseService syncCaseService;
    private SyncIncidentService syncIncidentService;
    private IncidentService incidentService;
    private IncidentFormService incidentFormService;

    private List<Incident> incidents;

    @Inject
    public GBVSyncPresenter(@ActivityContext Context context,
                            Lazy<SyncCaseService> syncCaseService,
                            Lazy<SyncIncidentService> syncIncidentService,
                            CaseService caseService,
                            CaseFormService caseFormService,
                            FormRemoteService formRemoteService,
                            IncidentService incidentService, Lazy<IncidentFormService> incidentFormService) {
        super(context, caseService, caseFormService, formRemoteService);
        this.incidentService = incidentService;
        this.incidentFormService = incidentFormService.get();
        this.syncIncidentService = syncIncidentService.get();
        this.syncCaseService = syncCaseService.get();

        initSyncRecordNumber();
    }

    @Override
    public List<Incident> getIncidents() {
        incidents = incidentService.getAll();
        return incidents;
    }

    public void upLoadCases(List<Case> caseList) {
        if (totalNumberOfUploadRecords != 0) {
            getView().showUploadCasesSyncProgressDialog();
            getView().setProgressMax(totalNumberOfUploadRecords);
        }
        isSyncing = true;
        Observable.from(caseList)
                .filter(item -> isSyncing && !item.isSynced())
                .map(item -> new Pair<>(item, syncCaseService.uploadCaseJsonProfile(item)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pair -> {
                    if (getView() != null) {
                        getView().setProgressIncrease();
                        increaseSyncNumber();
                        updateRecordSynced(pair.first, true);
                    }
                }, throwable -> {
                    try {
                        throwable.printStackTrace();
                        syncFail(throwable);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, () -> {
                    preUploadIncidents(incidents).subscribe(incidents -> upLoadIncidents(incidents));
                });
    }

    private Observable<List<Incident>> preUploadIncidents(List<Incident> incidents) {
        isSyncing = true;
        return Observable.from(incidents)
                .filter(incident -> isSyncing && !incident.isSynced())
                .map(incident -> {
                    if (!TextUtils.isEmpty(incident.getCaseUniqueId()) && TextUtils.isEmpty(incident
                            .getIncidentCaseId())) {
                        String incidentCaseId = caseService.getByUniqueId(incident.getCaseUniqueId()).getInternalId();
                        incident.setIncidentCaseId(incidentCaseId);
                        incident.save();
                    }
                    return incident;
                })
                .toList();
    }

    private void upLoadIncidents(List<Incident> incidents) {
        isSyncing = true;
        Observable.from(incidents)
                .filter(item -> isSyncing && !item.isSynced())
                .map(item -> new Pair<>(item, syncIncidentService.uploadIncidentJsonProfile(item)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pair -> {
                    if (getView() != null) {
                        Log.d(TAG, "onNext()...: " + pair.first.getIncidentCaseId());
                        getView().setProgressIncrease();
                        increaseSyncNumber();
                        updateRecordSynced(pair.first, true);
                    }
                }, throwable -> {
                    try {
                        Log.e(TAG, "onError: " + throwable.getMessage());
                        throwable.printStackTrace();
                        syncFail(throwable);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, () -> {
                    if (getView() != null) {
                        syncUploadSuccessfully();
                        preDownloadCases();
                    }
                });
    }

    public void preDownloadCases() {
        isSyncing = true;

        //TODO will change to real time
        GregorianCalendar cal = new GregorianCalendar(2015, 1, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        final String time = sdf.format(cal.getTime());
        final List<JsonObject> downList = new ArrayList<>();
        final ProgressDialog loadingDialog = getView().showFetchingCaseAmountLoadingDialog();

        syncCaseService.getCasesIds(PrimeroAppConfiguration.MODULE_ID_GBV, time, true)
                .map(jsonElementResponse -> {
                    if (jsonElementResponse.isSuccessful()) {
                        JsonElement jsonElement = jsonElementResponse.body();
                        JsonArray jsonArray = jsonElement.getAsJsonArray();

                        for (JsonElement element : jsonArray) {
                            JsonObject jsonObject = element.getAsJsonObject();
                            boolean hasSameRev = caseService.hasSameRev(jsonObject.get("_id")
                                            .getAsString(),
                                    jsonObject.get("_rev").getAsString());
                            if (!hasSameRev) {
                                downList.add(jsonObject);
                            }
                        }
                    }
                    return downList;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonObjects -> {
                    loadingDialog.dismiss();
                    if (jsonObjects.size() != 0 && getView() != null) {
                        getView().showDownloadingCasesSyncProgressDialog();
                        getView().setProgressMax(jsonObjects.size());
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    try {
                        loadingDialog.dismiss();
                        syncFail(throwable);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, () -> downloadCases(downList));
    }

    private void downloadCases(List<JsonObject> objects) {
        Observable.from(objects)
                .filter(jsonObject -> isSyncing)
                .map(jsonObject -> {
                    Observable<Response<JsonElement>> responseObservable = syncCaseService
                            .getCase(jsonObject.get("_id")
                                    .getAsString(), "en", true);
                    Response<JsonElement> response = responseObservable.toBlocking().first();
                    if (!response.isSuccessful()) {
                        throw new RuntimeException();
                    }
                    JsonObject responseJsonObject = response.body().getAsJsonObject();
                    saveDownloadedCases(responseJsonObject);
                    return response;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBodyResponse -> setProgressIncrease(),
                        throwable -> {
                            try {
                                syncFail(throwable);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, () -> {
                            if (getView() != null) {
                                getView().hideSyncProgressDialog();
                                preDownloadIncidents();
                            }
                        });
    }

    private void saveDownloadedCases(JsonObject casesJsonObject) {
        String internalId = casesJsonObject.get("_id").getAsString();

        Case item = caseService.getByInternalId(internalId);
        if (item != null) {
            setCaseProperties(casesJsonObject, item);
            item.update();
        } else {
            item = new Case();
            item.setUniqueId(casesJsonObject.get("case_id").getAsString());
            item.setShortId(casesJsonObject.get("short_id").getAsString());
            item.setInternalId(casesJsonObject.get("_id").getAsString());
            item.setRegistrationDate(
                    Utils.getRegisterDateAsDdMmYyyy(casesJsonObject.get("registration_date").getAsString()));
            item.setCreatedBy(casesJsonObject.get("created_by").getAsString());

            item.setLastSyncedDate(Calendar.getInstance().getTime());
            item.setLastUpdatedDate(Calendar.getInstance().getTime());

            setCaseProperties(casesJsonObject, item);
            item.save();
        }
    }

    private void setCaseProperties(JsonObject casesJsonObject, Case item) {
        casesJsonObject.remove("histories");
        item.setInternalRev(casesJsonObject.get("_rev").getAsString());
        item.setSynced(true);
        item.setContent(new Blob(casesJsonObject.toString().getBytes()));
        item.setServerUrl(TextUtils.lintUrl(PrimeroAppConfiguration.getApiBaseUrl()));
        item.setOwnedBy(casesJsonObject.get("owned_by").getAsString());
        item.setName(casesJsonObject.get("name").getAsString());
        item.setCaregiver(casesJsonObject.has("caregiver") ? casesJsonObject.get("caregiver").getAsString() : null);
        setAgeIfExists(item, casesJsonObject);
    }

    public void preDownloadIncidents() {
        isSyncing = true;
        GregorianCalendar cal = new GregorianCalendar(2015, 1, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        final String time = sdf.format(cal.getTime());
        final List<JsonObject> objects = new ArrayList<>();
        final ProgressDialog loadingDialog = getView().showFetchingIncidentAmountLoadingDialog();
        syncIncidentService.getIncidentIds(time, true)
                .map(jsonElementResponse -> {
                    if (jsonElementResponse.isSuccessful()) {
                        JsonElement jsonElement = jsonElementResponse.body();
                        JsonArray jsonArray = jsonElement.getAsJsonArray();

                        for (JsonElement element : jsonArray) {
                            JsonObject jsonObject = element.getAsJsonObject();
                            boolean hasSameRev = incidentService.hasSameRev(jsonObject.get
                                            ("_id").getAsString(),
                                    jsonObject.get("_rev").getAsString());
                            if (!hasSameRev) {
                                objects.add(jsonObject);
                            }
                        }
                    }
                    return objects;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonObjects -> {
                    loadingDialog.dismiss();
                    if (jsonObjects.size() != 0 && getView() != null) {
                        getView().showDownloadingIncidentsSyncProgressDialog();
                        getView().setProgressMax(jsonObjects.size());
                    }
                }, throwable -> {
                    try {
                        throwable.printStackTrace();
                        loadingDialog.dismiss();
                        syncFail(throwable);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, () -> downloadIncidents(objects));
    }

    private void downloadIncidents(List<JsonObject> objects) {
        Observable.from(objects)
                .filter(jsonObject -> isSyncing)
                .map(jsonObject -> {
                    Observable<Response<JsonElement>> responseObservable = syncIncidentService
                            .getIncident(jsonObject.get("_id")
                                    .getAsString(), "en", true);
                    Response<JsonElement> response = responseObservable.toBlocking().first();
                    if (!response.isSuccessful()) {
                        throw new RuntimeException();
                    }
                    JsonObject responseJsonObject = response.body().getAsJsonObject();
                    saveDownloadedIncidents(responseJsonObject);
                    return response;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBodyResponse -> setProgressIncrease(),
                        throwable -> {
                            try {
                                throwable.printStackTrace();
                                syncFail(throwable);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, () -> {
                            syncDownloadSuccessfully();
                            downloadCaseForm();
                        });
    }

    private void saveDownloadedIncidents(JsonObject incidentsJsonObject) {
        String internalId = incidentsJsonObject.get("_id").getAsString();
        Incident item = incidentService.getByInternalId(internalId);
        String registrationDate = incidentsJsonObject.get("registration_date").getAsString();

        if (item != null) {
            item.setIncidentCaseId(incidentsJsonObject.get(COLUMN_INCIDENT_CASE_ID).getAsString());
            setIncidentProperties(item, incidentsJsonObject);
            item.update();
        } else {
            item = new Incident();
            item.setUniqueId(incidentsJsonObject.get("incident_id").getAsString());
            item.setShortId(incidentsJsonObject.get("short_id").getAsString());
            item.setInternalId(incidentsJsonObject.get("_id").getAsString());
            item.setCreatedBy(incidentsJsonObject.get("created_by").getAsString());
            item.setOwnedBy(incidentsJsonObject.get("owned_by").getAsString());
            item.setRegistrationDate(Utils.getRegisterDateAsDdMmYyyy(registrationDate));
            setIncidentProperties(item, incidentsJsonObject);
            item.save();
        }
    }

    private void setIncidentProperties(Incident item, JsonObject incidentsJsonObject) {
        incidentsJsonObject.remove("histories");
        item.setLastSyncedDate(Calendar.getInstance().getTime());
        item.setLastUpdatedDate(Calendar.getInstance().getTime());
        String newRev = incidentsJsonObject.get("_rev").getAsString();
        item.setInternalRev(newRev);
        item.setServerUrl(TextUtils.lintUrl(PrimeroAppConfiguration.getApiBaseUrl()));
        item.setSynced(true);
        item.setContent(new Blob(incidentsJsonObject.toString().getBytes()));
        item.setLocation(incidentsJsonObject.has(RecordService.LOCATION) ? incidentsJsonObject.get(RecordService
                .LOCATION).getAsString() : null);
        if (incidentsJsonObject.has(COLUMN_INCIDENT_CASE_ID)) {
            String incidentCaseId = incidentsJsonObject.get(COLUMN_INCIDENT_CASE_ID).getAsString();
            item.setIncidentCaseId(incidentCaseId);

            Case incidentCase = caseService.getByInternalId(incidentCaseId);
            if (incidentCase != null) {
                item.setCaseUniqueId(incidentCase.getUniqueId());
            }
        }
        setAgeIfExists(item, incidentsJsonObject);
    }

    private void downloadCaseForm() {
        downloadCaseForm(getView().showFetchingFormLoadingDialog(), MODULE_ID_GBV);
    }

    @Override
    protected void downloadSecondFormByModule() {
        formRemoteService.getIncidentForm(PrimeroAppConfiguration.getCookie(),
                PrimeroAppConfiguration.getDefaultLanguage(), true, PrimeroAppConfiguration.PARENT_INCIDENT,
                MODULE_ID_GBV)
                .subscribe(incidentFormJson -> {
                            IncidentForm incidentForm = new IncidentForm(new Blob(new Gson().toJson(incidentFormJson)
                                    .getBytes()));
                            incidentForm.setModuleId(MODULE_ID_GBV);
                            incidentFormService.saveOrUpdate(incidentForm);
                        },
                        throwable -> syncFail(throwable)
                        , () -> syncPullFormSuccessfully());
    }
}
