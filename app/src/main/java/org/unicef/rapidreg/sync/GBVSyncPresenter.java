package org.unicef.rapidreg.sync;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.injection.ActivityContext;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.IncidentService;
import org.unicef.rapidreg.service.SyncCaseService;
import org.unicef.rapidreg.service.SyncTracingService;
import org.unicef.rapidreg.utils.Utils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GBVSyncPresenter extends BaseSyncPresenter {
    private static final String TAG = GBVSyncPresenter.class.getSimpleName();

    private Context context;
    private SyncCaseService syncCaseService;
    private SyncTracingService syncTracingService;
    private IncidentService incidentService;
    private CaseService caseService;

    private List<Case> cases;
    private List<Incident> incidents;

    private int numberOfSuccessfulUploadedRecords;
    private int totalNumberOfUploadRecords;

    private boolean isSyncing;

    @Override
    public void attachView(SyncView view) {
        super.attachView(view);
        if (isViewAttached()) {
            getView().setNotSyncedRecordNumber(totalNumberOfUploadRecords);
        }
    }

    @Inject
    public GBVSyncPresenter(@ActivityContext Context context, SyncCaseService syncService, SyncTracingService
            syncTracingService, CaseService caseService, IncidentService incidentService) {
        this.context = context;
        this.syncCaseService = syncService;
        this.caseService = caseService;
        this.syncTracingService = syncTracingService;
        this.incidentService = incidentService;

        cases = caseService.getAll();
        incidents = incidentService.getAll();

        initSyncRecordNumber();
    }

    public void tryToSync() {
        if (isViewAttached()) {
            getView().showAttemptSyncDialog();
        }
    }

    public void execSync() {
        if (!isViewAttached()) {
            return;
        }
        try {
            getView().disableSyncButton();
            initSyncRecordNumber();
            upLoadCases(cases);
        } catch (Throwable t) {
            syncFail(t);
        }
    }

    private void initSyncRecordNumber() {
        numberOfSuccessfulUploadedRecords = 0;
        totalNumberOfUploadRecords = 0;
        for (Incident incident : incidents) {
            if (!incident.isSynced()) {
                totalNumberOfUploadRecords++;
            }
        }
        for (Case aCase : cases) {
            if (!aCase.isSynced()) {
                totalNumberOfUploadRecords++;
            }
        }
    }

    private void upLoadCases(List<Case> caseList) {
        if (totalNumberOfUploadRecords != 0) {
            getView().showSyncProgressDialog("Uploading...Please wait a moment.");
            getView().setProgressMax(totalNumberOfUploadRecords);
        }
        isSyncing = true;
        Observable.from(caseList)
                .filter(new Func1<Case, Boolean>() {
                    @Override
                    public Boolean call(Case item) {
                        return isSyncing && !item.isSynced();
                    }
                })
                .map(new Func1<Case, Pair<Case, Response<JsonElement>>>() {
                    @Override
                    public Pair<Case, Response<JsonElement>> call(Case item) {
                        return new Pair<>(item, syncCaseService.uploadCaseJsonProfile(item));
                    }
                })
                .map(new Func1<Pair<Case, Response<JsonElement>>, Pair<Case,
                        Response<JsonElement>>>() {
                    @Override
                    public Pair<Case, Response<JsonElement>> call(Pair<Case,
                            Response<JsonElement>> pair) {
                        syncCaseService.uploadAudio(pair.first);
                        return pair;
                    }
                })
                .map(new Func1<Pair<Case, Response<JsonElement>>, Pair<Case,
                        Response<JsonElement>>>() {
                    @Override
                    public Pair<Case, Response<JsonElement>> call(Pair<Case,
                            Response<JsonElement>> caseResponsePair) {
                        try {
                            Response<JsonElement> jsonElementResponse = caseResponsePair.second;
                            JsonArray photoKeys = jsonElementResponse.body().getAsJsonObject()
                                    .get("photo_keys")
                                    .getAsJsonArray();
                            String id = jsonElementResponse.body().getAsJsonObject().get("_id")
                                    .getAsString();
                            okhttp3.Response response = null;
                            if (photoKeys.size() != 0) {
                                Call<Response<JsonElement>> call = syncCaseService.deleteCasePhotos
                                        (id, photoKeys);
                                response = call.execute().raw();
                            }

                            if (response == null || response.isSuccessful()) {
                                syncCaseService.uploadCasePhotos(caseResponsePair.first);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                        return caseResponsePair;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Pair<Case, Response<JsonElement>>>() {
                    @Override
                    public void call(Pair<Case, Response<JsonElement>> pair) {
                        if (getView() != null) {
                            getView().setProgressIncrease();
                            increaseSyncNumber();
                            updateRecordSynced(pair.first, true);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        try {
                            throwable.printStackTrace();
                            syncFail(throwable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        upLoadIncident(incidents);
                    }
                });
    }

    private void upLoadIncident(List<Incident> incidents) {
        isSyncing = true;
        Observable.from(incidents)
                .filter(new Func1<Incident, Boolean>() {
                    @Override
                    public Boolean call(Incident item) {
                        return isSyncing && !item.isSynced();
                    }
                })
                .map(new Func1<Incident, Pair<Incident, Response<JsonElement>>>() {
                    @Override
                    public Pair<Incident, Response<JsonElement>> call(Incident item) {
                        return new Pair<>(item, syncTracingService.uploadJsonProfile(item));
                    }
                })
                .map(new Func1<Pair<Incident, Response<JsonElement>>, Pair<Incident,
                        Response<JsonElement>>>() {
                    @Override
                    public Pair<Incident, Response<JsonElement>> call(Pair<Incident,
                            Response<JsonElement>> pair) {
                        syncTracingService.uploadAudio(pair.first);
                        return pair;
                    }
                })
                .map(new Func1<Pair<Incident, Response<JsonElement>>, Pair<Incident,
                        Response<JsonElement>>>() {
                    @Override
                    public Pair<Incident, Response<JsonElement>> call(Pair<Incident,
                            Response<JsonElement>> tracingResponsePair) {
                        try {
                            Response<JsonElement> jsonElementResponse = tracingResponsePair.second;
                            JsonArray photoKeys = jsonElementResponse.body().getAsJsonObject()
                                    .get("photo_keys")
                                    .getAsJsonArray();
                            String id = jsonElementResponse.body().getAsJsonObject().get("_id")
                                    .getAsString();
                            okhttp3.Response response = null;
                            if (photoKeys.size() != 0) {
                                Call<Response<JsonElement>> call = syncTracingService
                                        .deletePhotos(id, photoKeys);
                                response = call.execute().raw();
                            }

                            if (response == null || response.isSuccessful()) {
                                syncTracingService.uploadPhotos(tracingResponsePair.first);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                        return tracingResponsePair;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Pair<Incident, Response<JsonElement>>>() {
                    @Override
                    public void call(Pair<Incident, Response<JsonElement>> pair) {
                        if (getView() != null) {
                            getView().setProgressIncrease();
                            increaseSyncNumber();
                            updateRecordSynced(pair.first, true);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        try {
                            throwable.printStackTrace();
                            syncFail(throwable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if (getView() != null) {
                            syncUploadSuccessfully();
                            pullCases();
                        }
                    }
                });
    }

    private void increaseSyncNumber() {
        numberOfSuccessfulUploadedRecords += 1;
    }


    public void pullCases() {
        isSyncing = true;
        GregorianCalendar cal = new GregorianCalendar(2015, 1, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        final String time = sdf.format(cal.getTime());
        final List<JsonObject> objects = new ArrayList<>();
        final ProgressDialog loadingDialog = ProgressDialog.show(context, "", "Fetching case " +
                "amount from web " +
                "server...", true);
        syncCaseService.getCasesIds(time, true)
                .map(new Func1<Response<JsonElement>, List<JsonObject>>() {
                    @Override
                    public List<JsonObject> call(Response<JsonElement> jsonElementResponse) {
                        if (jsonElementResponse.isSuccessful()) {
                            JsonElement jsonElement = jsonElementResponse.body();
                            JsonArray jsonArray = jsonElement.getAsJsonArray();

                            for (JsonElement element : jsonArray) {
                                JsonObject jsonObject = element.getAsJsonObject();
                                boolean hasSameRev = caseService.hasSameRev(jsonObject.get("_id")
                                                .getAsString(),
                                        jsonObject.get("_rev").getAsString());
                                if (!hasSameRev) {
                                    objects.add(jsonObject);
                                }
                            }
                        }
                        return objects;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<JsonObject>>() {
                    @Override
                    public void call(List<JsonObject> jsonObjects) {
                        loadingDialog.dismiss();
                        if (jsonObjects.size() != 0 && getView() != null) {
                            getView().showSyncProgressDialog("Downloading Cases...Please wait a " +
                                    "moment.");
                            getView().setProgressMax(jsonObjects.size());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        try {
                            loadingDialog.dismiss();
                            syncFail(throwable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        downloadCases(objects);
                    }
                });
    }

    private void downloadCases(List<JsonObject> objects) {
        Observable.from(objects)
                .filter(new Func1<JsonObject, Boolean>() {
                    @Override
                    public Boolean call(JsonObject jsonObject) {
                        return isSyncing;
                    }
                })
                .map(new Func1<JsonObject, Response<JsonElement>>() {
                    @Override
                    public Response<JsonElement> call(JsonObject jsonObject) {
                        Observable<Response<JsonElement>> responseObservable = syncCaseService
                                .getCase(jsonObject.get("_id")
                                        .getAsString(), "en", true);
                        Response<JsonElement> response = responseObservable.toBlocking().first();
                        if (!response.isSuccessful()) {
                            throw new RuntimeException();
                        }
                        JsonObject responseJsonObject = response.body().getAsJsonObject();
                        postPullCases(responseJsonObject);
                        return response;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object responseBodyResponse) {
                        setProgressIncrease();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        try {
                            syncFail(throwable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if (getView() != null) {
                            getView().hideSyncProgressDialog();
                            pullIncidents();
                        }
                    }
                });
    }

    private void postPullCases(JsonObject casesJsonObject) {
        String internalId = casesJsonObject.get("_id").getAsString();
        Case item = caseService.getByInternalId(internalId);
        String newRev = casesJsonObject.get("_rev").getAsString();
        if (item != null) {
            item.setInternalRev(newRev);
            item.setSynced(true);
            item.setContent(new Blob(casesJsonObject.toString().getBytes()));
            item.setName(casesJsonObject.get("name").getAsString());
            item.setAge(casesJsonObject.get("age").getAsInt());
            //TODO set caregiver
            if (casesJsonObject.get("caregiver") != null) {
                item.setCaregiver(casesJsonObject.get("caregiver").getAsString());
            }
            item.update();
        } else {
            item = new Case();
            item.setUniqueId(casesJsonObject.get("case_id").getAsString());
            item.setShortId(casesJsonObject.get("short_id").getAsString());
            item.setInternalId(casesJsonObject.get("_id").getAsString());
            item.setInternalRev(newRev);
            item.setRegistrationDate(
                    Utils.getRegisterDate(casesJsonObject.get("registration_date").getAsString()));
            item.setCreatedBy(casesJsonObject.get("created_by").getAsString());
            item.setLastSyncedDate(Calendar.getInstance().getTime());
            item.setLastUpdatedDate(Calendar.getInstance().getTime());
            item.setSynced(true);
            item.setContent(new Blob(casesJsonObject.toString().getBytes()));
            item.setName(casesJsonObject.get("name").getAsString());
            item.setAge(casesJsonObject.get("age").getAsInt());
            //TODO set caregiver
            if (casesJsonObject.get("caregiver") != null) {
                item.setCaregiver(casesJsonObject.get("caregiver").getAsString());
            }
            item.save();
        }
    }

    public void pullIncidents() {
        isSyncing = true;
        GregorianCalendar cal = new GregorianCalendar(2015, 1, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        final String time = sdf.format(cal.getTime());
        final List<JsonObject> objects = new ArrayList<>();
        final ProgressDialog loadingDialog = ProgressDialog.show(context, "", "Fetching tracing " +
                "amount from web " +
                "server...", true);
        syncTracingService.getIds(time, true)
                .map(new Func1<Response<JsonElement>, List<JsonObject>>() {
                    @Override
                    public List<JsonObject> call(Response<JsonElement> jsonElementResponse) {
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
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<JsonObject>>() {
                    @Override
                    public void call(List<JsonObject> jsonObjects) {
                        loadingDialog.dismiss();
                        if (jsonObjects.size() != 0 && getView() != null) {
                            getView().showSyncProgressDialog("Downloading Tracing " +
                                    "Request...Please wait a moment.");
                            getView().setProgressMax(jsonObjects.size());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        try {
                            throwable.printStackTrace();
                            loadingDialog.dismiss();
                            syncFail(throwable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        downloadIncidents(objects);
                    }
                });
    }

    private void downloadIncidents(List<JsonObject> objects) {
        Observable.from(objects)
                .filter(new Func1<JsonObject, Boolean>() {
                    @Override
                    public Boolean call(JsonObject jsonObject) {
                        return isSyncing;
                    }
                })
                .map(new Func1<JsonObject, Response<JsonElement>>() {
                    @Override
                    public Response<JsonElement> call(JsonObject jsonObject) {

                        Observable<Response<JsonElement>> responseObservable = syncTracingService
                                .get(jsonObject.get("_id")
                                        .getAsString(), "en", true);
                        Response<JsonElement> response = responseObservable.toBlocking().first();
                        if (!response.isSuccessful()) {
                            throw new RuntimeException();
                        }
                        JsonObject responseJsonObject = response.body().getAsJsonObject();
                        postPullIncidents(responseJsonObject);
                        return response;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object responseBodyResponse) {
                        setProgressIncrease();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        try {
                            throwable.printStackTrace();
                            syncFail(throwable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        syncDownloadSuccessfully();
                    }
                });
    }

    private void setProgressIncrease() {
        if (getView() != null) {
            getView().setProgressIncrease();
        }
    }

    private void postPullIncidents(JsonObject incidentJsonObject) {
        String internalId = incidentJsonObject.get("_id").getAsString();
        Incident item = incidentService.getByInternalId(internalId);
        String newRev = incidentJsonObject.get("_rev").getAsString();
        String registrationDate = incidentJsonObject.get("inquiry_date").getAsString();
        if (item != null) {
            item.setInternalRev(newRev);
            item.setSynced(true);
            item.setContent(new Blob(incidentJsonObject.toString().getBytes()));
            item.update();
        } else {
            item = new Incident();
            item.setUniqueId(incidentJsonObject.get("tracing_request_id").getAsString());
            item.setInternalId(incidentJsonObject.get("_id").getAsString());
            item.setInternalRev(newRev);
            item.setRegistrationDate(Utils.getRegisterDate(registrationDate));
            item.setCreatedBy(incidentJsonObject.get("created_by").getAsString());
            item.setLastSyncedDate(Calendar.getInstance().getTime());
            item.setLastUpdatedDate(Calendar.getInstance().getTime());
            item.setSynced(true);
            item.setContent(new Blob(incidentJsonObject.toString().getBytes()));
            item.save();
        }
    }

    private void updateRecordSynced(RecordModel record, boolean synced) {
        record.setSynced(synced);
        record.update();
    }

    private void syncUploadSuccessfully() {
        if (getView() != null) {
            updateDataViews();
            getView().showSyncUploadSuccessMessage();
            getView().hideSyncProgressDialog();
        }
    }

    private void syncDownloadSuccessfully() {
        if (getView() != null) {
            updateDataViews();
            getView().showSyncDownloadSuccessMessage();
            getView().hideSyncProgressDialog();
            getView().enableSyncButton();
        }
    }

    private void syncFail(Throwable throwable) {
        if (getView() == null) {
            return;
        }
        Throwable cause = throwable.getCause();
        if (throwable instanceof SocketTimeoutException || cause instanceof
                SocketTimeoutException) {
            getView().showSyncErrorMessage(R.string.sync_request_time_out_error_message);
        } else if (throwable instanceof ConnectException || cause instanceof ConnectException
                || throwable instanceof IOException || cause instanceof IOException) {
            getView().showSyncErrorMessage(R.string.sync_server_not_reachable_error_message);
        } else {
            getView().showSyncErrorMessage(R.string.sync_error_message);
        }
        getView().hideSyncProgressDialog();
        updateDataViews();
        getView().enableSyncButton();
    }

    private void updateDataViews() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        String currentDateTime = sdf.format(new Date());
        int numberOfFailedUploadedCases = totalNumberOfUploadRecords -
                numberOfSuccessfulUploadedRecords;

        getView().setDataViews(currentDateTime, String.valueOf(numberOfSuccessfulUploadedRecords)
                , String.valueOf
                        (numberOfFailedUploadedCases));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (context);
        sharedPreferences.edit().putString("syncStatisticData", new Gson().toJson(new
                SyncStatisticData
                (currentDateTime, numberOfSuccessfulUploadedRecords, numberOfFailedUploadedCases)
        )).apply();
    }

    public void attemptCancelSync() {
        getView().showSyncCancelConfirmDialog();
    }

    public void cancelSync() {
        isSyncing = false;
    }
}
