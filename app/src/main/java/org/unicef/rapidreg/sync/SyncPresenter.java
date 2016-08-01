package org.unicef.rapidreg.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.model.TracingPhoto;
import org.unicef.rapidreg.network.SyncService;
import org.unicef.rapidreg.network.SyncTracingService;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingPhotoService;
import org.unicef.rapidreg.service.TracingService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SyncPresenter extends MvpBasePresenter<SyncView> {
    private static final String TAG = SyncPresenter.class.getSimpleName();

    private Context context;
    private SyncService syncService;
    private SyncTracingService syncTracingService;
    private TracingService tracingService;
    private CaseService caseService;
    private CasePhotoService casePhotoService;
    private TracingPhotoService tracingPhotoService;

    final List<Case> caseList = CaseService.getInstance().getAll();
    final List<Tracing> tracingList = TracingService.getInstance().getAll();

    private int numberOfSuccessfulUploadedRecords;
    private int totalNumberOfUploadRecords;

    private boolean isSyncing;

    public SyncPresenter(Context context) {
        this.context = context;
        try {
            syncService = new SyncService(context);
            caseService = CaseService.getInstance();
            casePhotoService = CasePhotoService.getInstance();
            syncTracingService = new SyncTracingService(context);
            tracingService = TracingService.getInstance();
            tracingPhotoService = tracingPhotoService.getInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
            initUploadProgressBarUI(caseList, tracingList);
            upLoadCases(caseList);
        } catch (Exception e) {
            syncFail();
        }
    }

    private void initUploadProgressBarUI(List<Case> caseList, List<Tracing> tracingList) {
        numberOfSuccessfulUploadedRecords = 0;
        totalNumberOfUploadRecords = 0;
        setCasesNumbers(caseList);
        setTracingsNumbers(tracingList);
        if(totalNumberOfUploadRecords != 0) {
            getView().showSyncProgressDialog("Uploading...Pls wait a moment.");
            getView().setProgressMax(totalNumberOfUploadRecords);
        }
    }

    private void setTracingsNumbers(List<Tracing> tracingList) {
        for (Tracing aTracing : tracingList) {
            if (!aTracing.isSynced()) {
                totalNumberOfUploadRecords++;
            }
        }
    }

    private void setCasesNumbers(List<Case> caseList) {
        for (Case aCase : caseList) {
            if (!aCase.isSynced()) {
                totalNumberOfUploadRecords++;
            }
        }
    }


    private void upLoadCases(List<Case> caseList) {
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
                        return new Pair<>(item, syncService.uploadCaseJsonProfile(item));
                    }
                })
                .map(new Func1<Pair<Case, Response<JsonElement>>, Pair<Case, Response<JsonElement>>>() {
                    @Override
                    public Pair<Case, Response<JsonElement>> call(Pair<Case, Response<JsonElement>> pair) {
                        syncService.uploadAudio(pair.first);
                        return pair;
                    }
                })
                .map(new Func1<Pair<Case, Response<JsonElement>>, Pair<Case, Response<JsonElement>>>() {
                    @Override
                    public Pair<Case, Response<JsonElement>> call(Pair<Case, Response<JsonElement>> caseResponsePair) {
                        try {
                            Response<JsonElement> jsonElementResponse = caseResponsePair.second;
                            JsonArray photoKeys = jsonElementResponse.body().getAsJsonObject().get("photo_keys")
                                    .getAsJsonArray();
                            String id = jsonElementResponse.body().getAsJsonObject().get("_id").getAsString();
                            okhttp3.Response response = null;
                            if (photoKeys.size() != 0) {
                                Call<Response<JsonElement>> call = syncService.deleteCasePhotos(id, photoKeys);
                                response = call.execute().raw();
                            }

                            if (response == null || response.isSuccessful()) {
                                syncService.uploadCasePhotos(caseResponsePair.first);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return caseResponsePair;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Pair<Case, Response<JsonElement>>>() {
                    @Override
                    public void call(Pair<Case, Response<JsonElement>> pair) {
                        getView().setProgressIncrease();
                        increaseSyncNumber();
                        updateRecordSynced(pair.first, true);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        syncFail();
                        throwable.printStackTrace();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        upLoadTracing(tracingList);
                    }
                });
    }

    private void upLoadTracing(List<Tracing> tracingList) {
        isSyncing = true;
        Observable.from(tracingList)
                .filter(new Func1<Tracing, Boolean>() {
                    @Override
                    public Boolean call(Tracing item) {
                        return isSyncing && !item.isSynced();
                    }
                })
                .map(new Func1<Tracing, Pair<Tracing, Response<JsonElement>>>() {
                    @Override
                    public Pair<Tracing, Response<JsonElement>> call(Tracing item) {
                        return new Pair<>(item, syncTracingService.uploadJsonProfile(item));
                    }
                })
                .map(new Func1<Pair<Tracing, Response<JsonElement>>, Pair<Tracing, Response<JsonElement>>>() {
                    @Override
                    public Pair<Tracing, Response<JsonElement>> call(Pair<Tracing, Response<JsonElement>> pair) {
                        syncTracingService.uploadAudio(pair.first);
                        return pair;
                    }
                })
                .map(new Func1<Pair<Tracing, Response<JsonElement>>, Pair<Tracing, Response<JsonElement>>>() {
                    @Override
                    public Pair<Tracing, Response<JsonElement>> call(Pair<Tracing, Response<JsonElement>> tracingResponsePair) {
                        try {
                            Response<JsonElement> jsonElementResponse = tracingResponsePair.second;
                            JsonArray photoKeys = jsonElementResponse.body().getAsJsonObject().get("photo_keys")
                                    .getAsJsonArray();
                            String id = jsonElementResponse.body().getAsJsonObject().get("_id").getAsString();
                            okhttp3.Response response = null;
                            if (photoKeys.size() != 0) {
                                Call<Response<JsonElement>> call = syncTracingService.deletePhotos(id, photoKeys);
                                response = call.execute().raw();
                            }

                            if (response == null || response.isSuccessful()) {
                                syncTracingService.uploadPhotos(tracingResponsePair.first);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return tracingResponsePair;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Pair<Tracing, Response<JsonElement>>>() {
                    @Override
                    public void call(Pair<Tracing, Response<JsonElement>> pair) {
                        getView().setProgressIncrease();
                        increaseSyncNumber();
                        updateRecordSynced(pair.first, true);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        syncFail();
                        throwable.printStackTrace();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        syncUploadSuccessfully();
                        pullCases();
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
        syncService.getCasesIds(time, true)
                .map(new Func1<Response<JsonElement>, List<JsonObject>>() {
                    @Override
                    public List<JsonObject> call(Response<JsonElement> jsonElementResponse) {
                        if (jsonElementResponse.isSuccessful()) {
                            JsonElement jsonElement = jsonElementResponse.body();
                            JsonArray jsonArray = jsonElement.getAsJsonArray();

                            for (JsonElement element : jsonArray) {
                                JsonObject jsonObject = element.getAsJsonObject();
                                boolean hasSameRev = caseService.hasSameRev(jsonObject.get("_id").getAsString(),
                                        jsonObject.get("_rev").getAsString());
                                if (!hasSameRev){
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
                        if (jsonObjects.size() != 0) {
                            getView().showSyncProgressDialog("Downloading Cases...Pls wait a moment.");
                            getView().setProgressMax(jsonObjects.size());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        syncFail();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        downloadCases(objects);
                    }
                });
    }

    public void pullTracings() {
        isSyncing = true;
        GregorianCalendar cal = new GregorianCalendar(2015, 1, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        final String time = sdf.format(cal.getTime());
        final List<JsonObject> objects = new ArrayList<>();
        syncTracingService.getIds(time, true)
                .map(new Func1<Response<JsonElement>, List<JsonObject>>() {
                    @Override
                    public List<JsonObject> call(Response<JsonElement> jsonElementResponse) {
                        if (jsonElementResponse.isSuccessful()) {
                            JsonElement jsonElement = jsonElementResponse.body();
                            JsonArray jsonArray = jsonElement.getAsJsonArray();

                            for (JsonElement element : jsonArray) {
                                JsonObject jsonObject = element.getAsJsonObject();
                                boolean hasSameRev = tracingService.hasSameRev(jsonObject.get("_id").getAsString(),
                                        jsonObject.get("_rev").getAsString());
                                if (!hasSameRev){
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
                        if (jsonObjects.size() != 0) {
                            getView().showSyncProgressDialog("Downloading Tracing Request...Pls wait a moment.");
                            getView().setProgressMax(jsonObjects.size());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        syncFail();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        downloadTracings(objects);
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

                        Observable<Response<JsonElement>> responseObservable = syncService.getCase(jsonObject.get("_id")
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
                .map(new Func1<Response<JsonElement>, Response<JsonElement>>() {
                    @Override
                    public Response<JsonElement> call(Response<JsonElement> response) {
                        JsonObject responseJsonObject = response.body().getAsJsonObject();
                        if (responseJsonObject.has("recorded_audio")) {
                            String id = responseJsonObject.get("_id").getAsString();
                            Response<ResponseBody> audioResponse = syncService.getCaseAudio(id).toBlocking().first();
                            if (!audioResponse.isSuccessful()) {
                                throw new RuntimeException();
                            }
                            try {
                                updateAudio(id, audioResponse.body().bytes());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        return response;
                    }
                })
                .map(new Func1<Response<JsonElement>, List<JsonObject>>() {
                    @Override
                    public List<JsonObject> call(Response<JsonElement> response) {
                        JsonObject responseJsonObject = response.body().getAsJsonObject();
                        List<JsonObject> photoKeys = new ArrayList<>();
                        if (responseJsonObject.has("photo_keys")) {
                            JsonArray jsonArray = responseJsonObject.get("photo_keys").getAsJsonArray();
                            for (JsonElement element : jsonArray) {
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("photo_key", element.getAsString());
                                jsonObject.addProperty("_id", responseJsonObject.get("_id").getAsString());
                                photoKeys.add(jsonObject);
                            }
                        }
                        return photoKeys;
                    }
                })
                .flatMap(new Func1<List<JsonObject>, Observable<JsonObject>>() {
                    @Override
                    public Observable<JsonObject> call(List<JsonObject> jsonObjects) {
                        return Observable.from(jsonObjects);
                    }
                })
                .map(new Func1<JsonObject, Object>() {
                    @Override
                    public Object call(JsonObject jsonObject) {
                        String id = jsonObject.get("_id").getAsString();
                        String photoKey = jsonObject.get("photo_key").getAsString();
                        Response<ResponseBody> response = syncService.getCasePhoto(id, photoKey, "1080").toBlocking().first();
                        try {
                            updateCasePhotos(id, response.body().bytes());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object responseBodyResponse) {
                        getView().setProgressIncrease();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        syncFail();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        getView().hideSyncProgressDialog();
                        pullTracings();
                    }
                });
    }


    private void downloadTracings(List<JsonObject> objects) {
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

                        Observable<Response<JsonElement>> responseObservable = syncTracingService.get(jsonObject.get("_id")
                                .getAsString(), "en", true);
                        Response<JsonElement> response = responseObservable.toBlocking().first();
                        if (!response.isSuccessful()) {
                            throw new RuntimeException();
                        }
                        JsonObject responseJsonObject = response.body().getAsJsonObject();
                        postPullTracings(responseJsonObject);
                        return response;
                    }
                })
                .map(new Func1<Response<JsonElement>, Response<JsonElement>>() {
                    @Override
                    public Response<JsonElement> call(Response<JsonElement> response) {
                        JsonObject responseJsonObject = response.body().getAsJsonObject();
                        if (responseJsonObject.has("recorded_audio")) {
                            String id = responseJsonObject.get("_id").getAsString();
                            Response<ResponseBody> audioResponse = syncTracingService.getAudio(id).toBlocking().first();
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
                    }
                })
                .map(new Func1<Response<JsonElement>, List<JsonObject>>() {
                    @Override
                    public List<JsonObject> call(Response<JsonElement> response) {
                        JsonObject responseJsonObject = response.body().getAsJsonObject();
                        List<JsonObject> photoKeys = new ArrayList<>();
                        if (responseJsonObject.has("photo_keys")) {
                            JsonArray jsonArray = responseJsonObject.get("photo_keys").getAsJsonArray();
                            for (JsonElement element : jsonArray) {
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("photo_key", element.getAsString());
                                jsonObject.addProperty("_id", responseJsonObject.get("_id").getAsString());
                                photoKeys.add(jsonObject);
                            }
                        }
                        return photoKeys;
                    }
                })
                .flatMap(new Func1<List<JsonObject>, Observable<JsonObject>>() {
                    @Override
                    public Observable<JsonObject> call(List<JsonObject> jsonObjects) {
                        return Observable.from(jsonObjects);
                    }
                })
                .map(new Func1<JsonObject, Object>() {
                    @Override
                    public Object call(JsonObject jsonObject) {
                        String id = jsonObject.get("_id").getAsString();
                        String photoKey = jsonObject.get("photo_key").getAsString();
                        Response<ResponseBody> response = syncTracingService.getPhoto(id, photoKey, "1080").toBlocking().first();
                        try {
                            updateTracingPhotos(id, response.body().bytes());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object responseBodyResponse) {
                        getView().setProgressIncrease();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        syncFail();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        syncDownloadSuccessfully();
                    }
                });
    }


    private void postPullCases(JsonObject casesJsonObject) {
        String internalId = casesJsonObject.get("_id").getAsString();
        Case item = CaseService.getInstance().getByInternalId(internalId);
        String newRev = casesJsonObject.get("_rev").getAsString();
        if (item != null) {
            item.setInternalRev(newRev);
            item.setSynced(true);
            item.setContent(new Blob(casesJsonObject.toString().getBytes()));
            item.update();
            casePhotoService.deleteByCaseId(item.getId());
        } else {
            item = new Case();
            item.setUniqueId(casesJsonObject.get("case_id").getAsString());
            item.setInternalId(casesJsonObject.get("_id").getAsString());
            item.setInternalRev(newRev);
            item.setRegistrationDate(
                    RecordService.getRegisterDate(casesJsonObject.get("registration_date").getAsString()));
            item.setCreatedBy(casesJsonObject.get("created_by").getAsString());
            item.setLastSyncedDate(Calendar.getInstance().getTime());
            item.setLastUpdatedDate(Calendar.getInstance().getTime());
            item.setSynced(true);
            item.setContent(new Blob(casesJsonObject.toString().getBytes()));
            item.save();
        }
    }

    private void postPullTracings(JsonObject tracingsJsonObject) {
        String internalId = tracingsJsonObject.get("_id").getAsString();
        Tracing item = TracingService.getInstance().getByInternalId(internalId);
        String newRev = tracingsJsonObject.get("_rev").getAsString();
        String registrationDate = tracingsJsonObject.get("inquiry_date").getAsString();
        if (item != null) {
            item.setInternalRev(newRev);
            item.setSynced(true);
            item.setContent(new Blob(tracingsJsonObject.toString().getBytes()));
            item.update();
            tracingPhotoService.deleteByTracingId(item.getId());
        } else {
            item = new Tracing();
            item.setUniqueId(tracingsJsonObject.get("tracing_request_id").getAsString());
            item.setInternalId(tracingsJsonObject.get("_id").getAsString());
            item.setInternalRev(newRev);
            item.setRegistrationDate(RecordService.getRegisterDate(registrationDate));
            item.setCreatedBy(tracingsJsonObject.get("created_by").getAsString());
            item.setLastSyncedDate(Calendar.getInstance().getTime());
            item.setLastUpdatedDate(Calendar.getInstance().getTime());
            item.setSynced(true);
            item.setContent(new Blob(tracingsJsonObject.toString().getBytes()));
            item.save();
        }
    }

    private void updateRecordSynced(RecordModel record, boolean synced) {
        record.setSynced(synced);
        record.update();
    }

    private void updateCasePhotos(String id, byte[] photoBytes) {
        Case aCase = caseService.getByInternalId(id);
        CasePhoto casePhoto = new CasePhoto();
        casePhoto.setCase(aCase);
        casePhoto.setOrder(casePhotoService.getByCaseId(aCase.getId()).size() + 1);
        casePhoto.setPhoto(new Blob(photoBytes));
        casePhoto.save();
    }

    private void updateTracingPhotos(String id, byte[] photoBytes) {
        Tracing aTracing = tracingService.getByInternalId(id);
        TracingPhoto TracingPhoto = new TracingPhoto();
        TracingPhoto.setTracing(aTracing);
        TracingPhoto.setOrder(tracingPhotoService.getByTracingId(aTracing.getId()).size() + 1);
        TracingPhoto.setPhoto(new Blob(photoBytes));
        TracingPhoto.save();
    }

    private void updateAudio(String id, byte[] audio) {
        Case aCase = caseService.getByInternalId(id);
        aCase.setAudio(new Blob(audio));
        aCase.update();
    }

    private void updateTracingAudio(String id, byte[] audio) {
        Tracing aTracing = tracingService.getByInternalId(id);
        aTracing.setAudio(new Blob(audio));
        aTracing.update();
    }

    private void syncUploadSuccessfully() {
        updateDataViews();
        getView().showSyncUploadSuccessMessage();
        getView().hideSyncProgressDialog();
    }

    private void syncDownloadSuccessfully() {
        updateDataViews();
        getView().showSyncDownloadSuccessMessage();
        getView().hideSyncProgressDialog();
    }

    private void syncFail() {
        updateDataViews();
        getView().showSyncErrorMessage();
        getView().hideSyncProgressDialog();
    }

    private void updateDataViews() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        String currentDateTime = sdf.format(new Date());
        int numberOfFailedUploadedCases = totalNumberOfUploadRecords - numberOfSuccessfulUploadedRecords;

        getView().setDataViews(currentDateTime, String.valueOf(numberOfSuccessfulUploadedRecords), String.valueOf
                (numberOfFailedUploadedCases));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString("syncStatisticData", new Gson().toJson(new SyncStatisticData
                (currentDateTime, numberOfSuccessfulUploadedRecords, numberOfFailedUploadedCases))).apply();
    }

    public void attemptCancelSync() {
        getView().showSyncCancelConfirmDialog();
    }

    public void cancelSync() {
        isSyncing = false;
    }
}
