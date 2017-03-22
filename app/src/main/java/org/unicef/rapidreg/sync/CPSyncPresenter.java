package org.unicef.rapidreg.sync;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.util.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.base.record.recordphoto.PhotoConfig;
import org.unicef.rapidreg.injection.ActivityContext;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.model.TracingForm;
import org.unicef.rapidreg.model.TracingPhoto;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.FormRemoteService;
import org.unicef.rapidreg.service.SyncCaseService;
import org.unicef.rapidreg.service.SyncTracingService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.TracingPhotoService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.utils.TextUtils;
import org.unicef.rapidreg.utils.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.Lazy;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static org.unicef.rapidreg.PrimeroAppConfiguration.MODULE_ID_CP;
import static org.unicef.rapidreg.service.RecordService.CAREGIVER_NAME;

public class CPSyncPresenter extends BaseSyncPresenter {
    private static final String TAG = CPSyncPresenter.class.getSimpleName();

    private SyncCaseService syncCaseService;
    private SyncTracingService syncTracingService;
    private TracingService tracingService;
    private CasePhotoService casePhotoService;
    private TracingPhotoService tracingPhotoService;
    private TracingFormService tracingFormService;

    private List<Tracing> tracings;

    @Inject
    public CPSyncPresenter(@ActivityContext Context context,
                           Lazy<SyncCaseService> syncCaseService,
                           Lazy<SyncTracingService> syncTracingService,
                           CaseService caseService,
                           CasePhotoService casePhotoService,
                           TracingPhotoService tracingPhotoService,
                           TracingService tracingService,
                           CaseFormService caseFormService,
                           TracingFormService tracingFormService,
                           Lazy<FormRemoteService> formRemoteService) {
        super(context, caseService, caseFormService, formRemoteService.get());
        this.syncCaseService = syncCaseService.get();
        this.syncTracingService = syncTracingService.get();
        this.casePhotoService = casePhotoService;
        this.tracingService = tracingService;
        this.tracingPhotoService = tracingPhotoService;
        this.tracingFormService = tracingFormService;

        initSyncRecordNumber();
    }

    @Override
    public List<Tracing> getTracings() {
        tracings = tracingService.getAll();
        return tracings;
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
                .map(pair -> {
                    syncCaseService.uploadAudio(pair.first);
                    return pair;
                })
                .map(caseResponsePair -> {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                    return caseResponsePair;
                })
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
                }, () -> upLoadTracing(tracings));
    }

    private void upLoadTracing(List<Tracing> tracingList) {
        isSyncing = true;
        Observable.from(tracingList)
                .filter(item -> isSyncing && !item.isSynced())
                .map(item -> new Pair<>(item, syncTracingService.uploadJsonProfile(item)))
                .map(pair -> {
                    syncTracingService.uploadAudio(pair.first);
                    return pair;
                })
                .buffer(4)
                .flatMap(pairs -> Observable.from(pairs))
                .map(tracingResponsePair -> {
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
                    } catch (Exception e) {
                        e.printStackTrace();
//                        throw new RuntimeException(e);
                    }
                    return tracingResponsePair;
                })
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
                    if (getView() != null) {
                        syncUploadSuccessfully();
                        preDownloadCases();
                    }
                });
    }

    public void preDownloadCases() {
        isSyncing = true;
        GregorianCalendar cal = new GregorianCalendar(2015, 1, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        final String time = sdf.format(cal.getTime());
        final List<JsonObject> downList = new ArrayList<>();
        final ProgressDialog loadingDialog = getView().showFetchingCaseAmountLoadingDialog();

        syncCaseService.getCasesIds(PrimeroAppConfiguration.MODULE_ID_CP, time, true)
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
                .map(response -> {
                    JsonObject responseJsonObject = response.body().getAsJsonObject();
                    if (responseJsonObject.has("recorded_audio")) {
                        String id = responseJsonObject.get("_id").getAsString();
                        Response<ResponseBody> audioResponse = syncCaseService.getCaseAudio(id)
                                .toBlocking().first();
                        if (!audioResponse.isSuccessful()) {
                            throw new RuntimeException();
                        }
                        try {
                            updateCaseAudio(id, audioResponse.body().bytes());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return response;
                })
                .map(response -> {
                    JsonObject responseJsonObject = response.body().getAsJsonObject();
                    List<JsonObject> photoKeys = new ArrayList<>();

                    if (responseJsonObject.has("photo_keys")) {
                        JsonArray jsonArray = responseJsonObject.get("photo_keys")
                                .getAsJsonArray();
                        for (JsonElement element : jsonArray) {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("photo_key", element.getAsString());
                            jsonObject.addProperty("_id", responseJsonObject.get("_id")
                                    .getAsString());
                            photoKeys.add(jsonObject);
                        }
                    }
                    return photoKeys;
                })
                .flatMap(new Func1<List<JsonObject>, Observable<JsonObject>>() {
                    @Override
                    public Observable<JsonObject> call(List<JsonObject> jsonObjects) {
                        return Observable.from(jsonObjects);
                    }
                })
                .map(jsonObject -> {
                    String id = jsonObject.get("_id").getAsString();
                    String photoKey = jsonObject.get("photo_key").getAsString();
                    Response<ResponseBody> response = syncCaseService.getCasePhoto(id, photoKey,
                            PhotoConfig.RESIZE_FOR_WEB)
                            .toBlocking()
                            .first();
                    try {
                        if (response.isSuccessful()) {
                            updateCasePhotos(id, response.body().bytes());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
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
                                preDownloadTracings();
                            }
                        });
    }

    private void saveDownloadedCases(JsonObject casesJsonObject) {
        String internalId = casesJsonObject.get("_id").getAsString();

        Case item = caseService.getByInternalId(internalId);

        if (item != null) {
            setCaseProperties(casesJsonObject, item);
            item.update();
            casePhotoService.deleteByCaseId(item.getId());
        } else {
            item = new Case();
            item.setUniqueId(casesJsonObject.get("case_id").getAsString());
            item.setShortId(casesJsonObject.get("short_id").getAsString());
            item.setInternalId(casesJsonObject.get("_id").getAsString());

            setCaseProperties(casesJsonObject, item);
            item.save();
        }
    }

    private void setCaseProperties(JsonObject casesJsonObject, Case item) {
        casesJsonObject.remove("histories");
        item.setInternalRev(casesJsonObject.get("_rev").getAsString());
        item.setOwnedBy(casesJsonObject.get("owned_by").getAsString());
        item.setCreatedBy(casesJsonObject.get("created_by").getAsString());
        item.setServerUrl(PrimeroAppConfiguration.getApiBaseUrl());
        item.setSynced(true);
        item.setLastSyncedDate(Calendar.getInstance().getTime());
        item.setLastUpdatedDate(Calendar.getInstance().getTime());
        item.setName(casesJsonObject.get("name").getAsString());
        item.setContent(new Blob(casesJsonObject.toString().getBytes()));
        item.setCaregiver(casesJsonObject.has(CAREGIVER_NAME) ? casesJsonObject.get(CAREGIVER_NAME).getAsString() :
                null);
        item.setRegistrationDate(Utils.getRegisterDateByYyyyMmDd(casesJsonObject.get("registration_date")
                .getAsString()));
        setAgeIfExists(item, casesJsonObject);
    }

    private void updateCasePhotos(String id, byte[] photoBytes) {
        Case aCase = caseService.getByInternalId(id);
        CasePhoto casePhoto = new CasePhoto();
        casePhoto.setCase(aCase);
        casePhoto.setOrder(casePhotoService.getIdsByCaseId(aCase.getId()).size() + 1);
        casePhoto.setPhoto(new Blob(photoBytes));
        casePhoto.save();
    }

    private void updateCaseAudio(String id, byte[] audio) {
        Case aCase = caseService.getByInternalId(id);
        aCase.setAudio(new Blob(audio));
        aCase.update();
    }

    public void preDownloadTracings() {
        isSyncing = true;
        GregorianCalendar cal = new GregorianCalendar(2015, 1, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        final String time = sdf.format(cal.getTime());
        final List<JsonObject> objects = new ArrayList<>();
        final ProgressDialog loadingDialog = getView().showFetchingTracingAmountLoadingDialog();
        syncTracingService.getIds(time, true)
                .map(jsonElementResponse -> {
                    if (jsonElementResponse.isSuccessful()) {
                        JsonElement jsonElement = jsonElementResponse.body();
                        JsonArray jsonArray = jsonElement.getAsJsonArray();

                        for (JsonElement element : jsonArray) {
                            JsonObject jsonObject = element.getAsJsonObject();
                            boolean hasSameRev = tracingService.hasSameRev(jsonObject.get
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
                        getView().showDownloadingTracingsSyncProgressDialog();
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
                }, () -> downloadTracings(objects));
    }

    private void downloadTracings(List<JsonObject> objects) {
        Observable.from(objects)
                .filter(jsonObject -> isSyncing)
                .map(jsonObject -> {
                    Observable<Response<JsonElement>> responseObservable = syncTracingService
                            .get(jsonObject.get("_id")
                                    .getAsString(), "en", true);
                    Response<JsonElement> response = responseObservable.toBlocking().first();
                    if (!response.isSuccessful()) {
                        throw new RuntimeException();
                    }
                    JsonObject responseJsonObject = response.body().getAsJsonObject();
                    saveDownloadedTracings(responseJsonObject);
                    return response;
                })
                .map(response -> {
                    JsonObject responseJsonObject = response.body().getAsJsonObject();
                    if (responseJsonObject.has("recorded_audio")) {
                        String id = responseJsonObject.get("_id").getAsString();
                        Response<ResponseBody> audioResponse = syncTracingService.getAudio
                                (id).toBlocking().first();
                        if (!audioResponse.isSuccessful()) {
                            throw new RuntimeException();
                        }
                        try {
                            updateTracingAudio(id, audioResponse.body().bytes());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return response;
                })
                .map(response -> {
                    JsonObject responseJsonObject = response.body().getAsJsonObject();
                    List<JsonObject> photoKeys = new ArrayList<>();
                    if (responseJsonObject.has("photo_keys")) {
                        JsonArray jsonArray = responseJsonObject.get("photo_keys")
                                .getAsJsonArray();
                        for (JsonElement element : jsonArray) {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("photo_key", element.getAsString());
                            jsonObject.addProperty("_id", responseJsonObject.get("_id")
                                    .getAsString());
                            photoKeys.add(jsonObject);
                        }
                    }
                    return photoKeys;
                })
                .flatMap(new Func1<List<JsonObject>, Observable<JsonObject>>() {
                    @Override
                    public Observable<JsonObject> call(List<JsonObject> jsonObjects) {
                        return Observable.from(jsonObjects);
                    }
                })
                .map(jsonObject -> {
                    String id = jsonObject.get("_id").getAsString();
                    String photoKey = jsonObject.get("photo_key").getAsString();
                    Response<ResponseBody> response = syncTracingService.getPhoto(id,
                            photoKey, "1080").toBlocking().first();
                    try {
                        updateTracingPhotos(id, response.body().bytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
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

    private void saveDownloadedTracings(JsonObject tracingsJsonObject) {
        String internalId = tracingsJsonObject.get("_id").getAsString();
        Tracing item = tracingService.getByInternalId(internalId);
        if (item != null) {
            setTracingRequestProperties(tracingsJsonObject, item);
            item.update();
            tracingPhotoService.deleteByTracingId(item.getId());
        } else {
            item = new Tracing();
            item.setUniqueId(tracingsJsonObject.get("tracing_request_id").getAsString());
            item.setShortId(tracingsJsonObject.get("short_id").getAsString());
            item.setInternalId(tracingsJsonObject.get("_id").getAsString());

            setTracingRequestProperties(tracingsJsonObject, item);
            item.save();
        }
    }

    private void setTracingRequestProperties(JsonObject tracingsJsonObject, Tracing item) {
        tracingsJsonObject.remove("histories");
        String newRev = tracingsJsonObject.get("_rev").getAsString();
        String registrationDate = tracingsJsonObject.get("inquiry_date").getAsString();
        item.setInternalRev(newRev);
        item.setRegistrationDate(Utils.getRegisterDateAsDdMmYyyy(registrationDate));
        item.setCreatedBy(tracingsJsonObject.get("created_by").getAsString());
        item.setOwnedBy(tracingsJsonObject.get("owned_by").getAsString());
        item.setServerUrl(TextUtils.lintUrl(PrimeroAppConfiguration.getApiBaseUrl()));
        item.setLastSyncedDate(Calendar.getInstance().getTime());
        item.setLastUpdatedDate(Calendar.getInstance().getTime());
        item.setSynced(true);
        item.setContent(new Blob(tracingsJsonObject.toString().getBytes()));
        setAgeIfExists(item, tracingsJsonObject);
    }

    private void updateTracingPhotos(String id, byte[] photoBytes) {
        Tracing aTracing = tracingService.getByInternalId(id);
        TracingPhoto TracingPhoto = new TracingPhoto();
        TracingPhoto.setTracing(aTracing);
        TracingPhoto.setOrder(tracingPhotoService.getIdsByTracingId(aTracing.getId()).size() + 1);
        TracingPhoto.setPhoto(new Blob(photoBytes));
        TracingPhoto.save();
    }

    private void updateTracingAudio(String id, byte[] audio) {
        Tracing aTracing = tracingService.getByInternalId(id);
        aTracing.setAudio(new Blob(audio));
        aTracing.update();
    }

    private void downloadCaseForm() {
        downloadCaseForm(getView().showFetchingFormLoadingDialog(), MODULE_ID_CP);
    }

    @Override
    protected void downloadSecondFormByModule() {
        formRemoteService.getTracingForm(PrimeroAppConfiguration.getCookie(),
                PrimeroAppConfiguration.getDefaultLanguage(), true, PrimeroAppConfiguration.PARENT_TRACING_REQUEST,
                MODULE_ID_CP)
                .subscribe(tracingFormJson -> {
                            TracingForm tracingForm = new TracingForm(new Blob(new Gson().toJson(tracingFormJson)
                                    .getBytes()));
                            tracingForm.setModuleId(MODULE_ID_CP);
                            tracingFormService.saveOrUpdate(tracingForm);
                        },
                        throwable -> syncFail(throwable),
                        () -> syncPullFormSuccessfully());
    }
}
