package org.unicef.rapidreg.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.raizlabs.android.dbflow.data.Blob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.base.PhotoConfig;
import org.unicef.rapidreg.event.PullCasesEvent;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.network.SyncService;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValues;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SyncPresenter extends MvpBasePresenter<SyncView> {
    private static final String TAG = SyncPresenter.class.getSimpleName();

    private static final String FORM_DATA_KEY_AUDIO = "child[audio]";
    private static final String FORM_DATA_KEY_PHOTO = "child[photo][0]";

    private Context context;
    private SyncService syncService;
    private CaseService caseService;
    private CasePhotoService casePhotoService;

    private int numberOfSuccessfulUploadedCases = 0;
    private int totalNumberOfCases = 0;

    public SyncPresenter(Context context) {
        this.context = context;
        try {
            syncService = new SyncService(context);
            caseService = CaseService.getInstance();
            casePhotoService = CasePhotoService.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void attachView(SyncView view) {
        super.attachView(view);
        EventBus.getDefault().register(this);
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        EventBus.getDefault().unregister(this);
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
            upLoadCases();
        } catch (Exception e) {
            syncFail();
        }
    }


    private void upLoadCases() {
        final List<Case> caseList = CaseService.getInstance().getAll();
        totalNumberOfCases = caseList.size();
        getView().showSyncProgressDialog();
        getView().setProgressMax(totalNumberOfCases);

        Observable.from(caseList)
                .filter(new Func1<Case, Boolean>() {
                    @Override
                    public Boolean call(Case item) {
                        return true;
                    }
                })
                .map(new Func1<Case, Pair<Case, Response<JsonElement>>>() {
                    @Override
                    public Pair<Case, Response<JsonElement>> call(Case item) {
                        ItemValues values = ItemValues.fromJson(new String(item.getContent().getBlob()));
                        values.addStringItem("case_id", item.getUniqueId());
                        values.addStringItem("short_id", caseService.getShortUUID(item.getUniqueId()));

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.add("child", values.getValues());

                        Observable<Response<JsonElement>> responseObservable;
                        if (!TextUtils.isEmpty(item.getInternalId())) {
                            responseObservable = syncService.putCase(PrimeroConfiguration.getCookie(), item
                                            .getInternalId(), true,
                                    jsonObject);
                        } else {
                            responseObservable = syncService.postCaseExcludeMediaData(PrimeroConfiguration.getCookie(),
                                    true, jsonObject);
                        }
                        Response<JsonElement> response = responseObservable.toBlocking().first();

                        verifyResponse(response);

                        JsonObject responseJsonObject = response.body().getAsJsonObject();
                        String caseId = responseJsonObject.get("case_id").getAsString();
                        Case item2 = CaseService.getInstance().getByUniqueId(caseId);
                        if (item2 != null) {
                            item2.setInternalId(responseJsonObject.get("_id").getAsString());
                            item2.setInternalRev(responseJsonObject.get("_rev").getAsString());
                            item2.setContent(new Blob(responseJsonObject.toString().getBytes()));
                            item2.update();
                        }

                        return new Pair<>(item, response);
                    }
                })
                .map(new Func1<Pair<Case, Response<JsonElement>>, Pair<Case, Response<JsonElement>>>() {
                    @Override
                    public Pair<Case, Response<JsonElement>> call(Pair<Case, Response<JsonElement>> pair) {
                        Case item = pair.first;
                        if (item.getAudio() != null) {
                            RequestBody requestFile = RequestBody.create(MediaType.parse(
                                    PhotoConfig.CONTENT_TYPE_AUDIO), item.getAudio().getBlob());
                            MultipartBody.Part body = MultipartBody.Part.createFormData(FORM_DATA_KEY_AUDIO, "audioFile.amr", requestFile);
                            Observable<Response<JsonElement>> observable = syncService.postCaseMediaData(
                                    PrimeroConfiguration.getCookie(), item.getInternalId(), body);

                            Response<JsonElement> response = observable.toBlocking().first();

                            verifyResponse(response);

                            item.setAudioSynced(true);
                            updateCaseRev(item, response.body().getAsJsonObject().get("_rev").getAsString());
                        }
                        return pair;
                    }
                })
                .map(new Func1<Pair<Case, Response<JsonElement>>, Pair<Case, Response<JsonElement>>>() {
                    @Override
                    public Pair<Case, Response<JsonElement>> call(Pair<Case, Response<JsonElement>> caseResponsePair) {
                        uploadCasePhotos(caseResponsePair.first);
                        return caseResponsePair;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Pair<Case, Response<JsonElement>>>() {
                    @Override
                    public void call(Pair<Case, Response<JsonElement>> pair) {
                        Log.i(TAG, pair.second.toString());

                        getView().setProgressIncrease();
                        increaseSyncNumber();
                        updateCaseSyncStatus(pair.first);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        syncFail();
                        throwable.printStackTrace();
                        Log.i(TAG, throwable.toString());
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        syncSuccessfully();
                    }
                });
    }

    void verifyResponse(Response<JsonElement> response) {
        if (!response.isSuccessful()) {
            throw new RuntimeException();
        }
    }


    private void uploadCasePhotos(final Case aCase) {
        List<CasePhoto> casePhotos = casePhotoService.getByCaseId(aCase.getId());
        Observable.from(casePhotos)
                .filter(new Func1<CasePhoto, Boolean>() {
                    @Override
                    public Boolean call(CasePhoto casePhoto) {
                        return !casePhoto.isSynced();
                    }
                })
                .flatMap(new Func1<CasePhoto, Observable<Pair<CasePhoto, Response<JsonElement>>>>() {
                    @Override
                    public Observable<Pair<CasePhoto, Response<JsonElement>>> call(final CasePhoto casePhoto) {
                        return Observable.create(new Observable.OnSubscribe<Pair<CasePhoto, Response<JsonElement>>>() {
                            @Override
                            public void call(Subscriber<? super Pair<CasePhoto, Response<JsonElement>>> subscriber) {
                                RequestBody requestFile = RequestBody.create(MediaType.parse(PhotoConfig.CONTENT_TYPE_IMAGE),
                                        casePhoto.getPhoto().getBlob());
                                MultipartBody.Part body = MultipartBody.Part.createFormData(FORM_DATA_KEY_PHOTO, casePhoto.getKey() + ".jpg",
                                        requestFile);
                                Observable<Response<JsonElement>> observable = syncService.postCaseMediaData(PrimeroConfiguration.getCookie(), aCase.getInternalId(), body);
                                Response<JsonElement> response = observable.toBlocking().first();
                                verifyResponse(response);
                                subscriber.onNext(new Pair<>(casePhoto, response));
                                subscriber.onCompleted();
                            }
                        });
                    }
                })
                .map(new Func1<Pair<CasePhoto, Response<JsonElement>>, Object>() {
                    @Override
                    public Object call(Pair<CasePhoto, Response<JsonElement>> casePhotoResponsePair) {

                        Response<JsonElement> response = casePhotoResponsePair.second;
                        CasePhoto casePhoto = casePhotoResponsePair.first;
                        updateCaseRev(aCase, response.body().getAsJsonObject().get("_rev").getAsString());
                        updateCasePhotoSyncStatus(casePhoto, true);
                        return null;
                    }
                }).toList().toBlocking().first();
    }

    private void increaseSyncNumber() {
        numberOfSuccessfulUploadedCases += 1;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pullCases(PullCasesEvent event) {
        getView().showSyncProgressDialog();
        getView().setProgressMax(100);

        GregorianCalendar cal = new GregorianCalendar(2015, 1, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        final String time = sdf.format(cal.getTime());

        syncService.getCasesIds(PrimeroConfiguration.getCookie(), time, true)
                .map(new Func1<Response<JsonElement>, List<JsonObject>>() {
                    @Override
                    public List<JsonObject> call(Response<JsonElement> jsonElementResponse) {
                        List<JsonObject> objects = new ArrayList<>();

                        if (jsonElementResponse.isSuccessful()) {
                            JsonElement jsonElement = jsonElementResponse.body();
                            JsonArray jsonArray = jsonElement.getAsJsonArray();

                            for (JsonElement element : jsonArray) {
                                JsonObject jsonObject = element.getAsJsonObject();
                                objects.add(jsonObject);
                            }
                        }
                        return objects;
                    }
                })
                .flatMap(new Func1<List<JsonObject>, Observable<JsonObject>>() {
                    @Override
                    public Observable<JsonObject> call(List<JsonObject> jsonObjects) {
                        return Observable.from(jsonObjects);
                    }
                })
                .filter(new Func1<JsonObject, Boolean>() {
                    @Override
                    public Boolean call(JsonObject jsonObject) {
                        return true;
                    }
                })
                .concatMap(new Func1<JsonObject, Observable<Response<JsonElement>>>() {
                    @Override
                    public Observable<Response<JsonElement>> call(JsonObject jsonObject) {
                        return syncService.getCase(PrimeroConfiguration.getCookie(), jsonObject.get("_id").getAsString(), "en", true);
                    }
                })
                .map(new Func1<Response<JsonElement>, List<JsonObject>>() {
                    @Override
                    public List<JsonObject> call(Response<JsonElement> jsonElementResponse) {
                        //parse case json
                        JsonObject responseJsonObject = jsonElementResponse.body().getAsJsonObject();
                        List<JsonObject> objects = new ArrayList<>();

                        if (responseJsonObject.has("photo_keys")) {
                            JsonArray jsonArray = responseJsonObject.get("photo_keys").getAsJsonArray();
                            for (JsonElement element : jsonArray) {
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("photo_key", element.getAsString());
                                jsonObject.addProperty("_id", responseJsonObject.get("_id").getAsString());
                                objects.add(jsonObject);
                            }
                        }

                        if (responseJsonObject.has("audio_key")) {
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("audio_key", responseJsonObject.get("audio_key").getAsString());
                            jsonObject.addProperty("_id", responseJsonObject.get("_id").getAsString());
                            objects.add(jsonObject);
                        }

                        return objects;
                    }
                })
                .flatMap(new Func1<List<JsonObject>, Observable<JsonObject>>() {
                    @Override
                    public Observable<JsonObject> call(List<JsonObject> jsonObjects) {
                        return Observable.from(jsonObjects);
                    }
                })
                .concatMap(new Func1<JsonObject, Observable<Response<ResponseBody>>>() {
                    @Override
                    public Observable<Response<ResponseBody>> call(JsonObject jsonObject) {
                        String id = jsonObject.get("_id").getAsString();
                        if (jsonObject.has("photo_key")) {
                            String photoKey = jsonObject.get("photo_key").getAsString();
                            return syncService.getCasePhoto(PrimeroConfiguration.getCookie(), id, photoKey, "1080");
                        }
                        return syncService.getCaseAudio(PrimeroConfiguration.getCookie(), id);
                    }
                })
                .map(new Func1<Response<ResponseBody>, byte[]>() {
                    @Override
                    public byte[] call(Response<ResponseBody> responseBodyResponse) {
                        if (responseBodyResponse.isSuccessful()) {
                            try {
                                return responseBodyResponse.body().bytes();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<byte[]>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(byte[] bytes) {

                    }
                });
    }


    private void postPullCases(JsonObject casesJsonObject) throws ParseException {
        String caseId = casesJsonObject.get("case_id").getAsString();
        Case item = CaseService.getInstance().getByUniqueId(caseId);
        String newRev = casesJsonObject.get("_rev").getAsString();
        if (item != null) {
            String previousRev = item.getInternalRev();
            item.setInternalId(casesJsonObject.get("_id").getAsString());
            item.setInternalRev(newRev);
            item.setSynced(true);
            item.setContent(new Blob(casesJsonObject.toString().getBytes()));
            item.update();
            if (!previousRev.equalsIgnoreCase(newRev)) {
                updateAudio();
                updateCasePhotos();
            }
        } else {
            item = new Case();
            item.setUniqueId(CaseService.getInstance().createUniqueId());
            item.setName(casesJsonObject.get("name").getAsString());
            item.setAge(casesJsonObject.get("age").getAsInt());
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

    private void updateCaseRev(Case aCase, String revId) {
        aCase.setInternalRev(revId);
        aCase.update();
    }

    private void updateCasePhotoSyncStatus(CasePhoto casePhoto, boolean status) {
        casePhoto.setSynced(status);
        casePhoto.update();
    }

    void updateCaseSyncStatus(Case item) {
        item.setSynced(item.isAudioSynced() && !CasePhotoService.getInstance().hasUnSynced(item.getId()));
        item.update();
    }

    private void updateCasePhotos() {

    }

    private void updateAudio() {

    }

    private void syncSuccessfully() {
        updateDataViews();
        getView().showSyncSuccessMessage();
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
        int numberOfFailedUploadedCases = totalNumberOfCases - numberOfSuccessfulUploadedCases;

        getView().setDataViews(currentDateTime, String.valueOf(numberOfSuccessfulUploadedCases), String.valueOf
                (numberOfFailedUploadedCases));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString("syncStatisticData", new Gson().toJson(new SyncStatisticData
                (currentDateTime, numberOfSuccessfulUploadedCases, numberOfFailedUploadedCases)));
    }

    public void attemptCancelSync() {
        getView().showSyncCancelConfirmDialog();
    }

    public void cancelSync() {

    }
}
