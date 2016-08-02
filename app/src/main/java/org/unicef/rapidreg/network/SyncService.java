package org.unicef.rapidreg.network;


import android.content.Context;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.base.PhotoConfig;
import org.unicef.rapidreg.db.impl.CasePhotoDaoImpl;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValues;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;


public class SyncService extends BaseRetrofitService {
    private SyncServiceInterface serviceInterface;

    @Override
    String getBaseUrl() {
        return PrimeroConfiguration.getApiBaseUrl();
    }

    public SyncService(Context context) throws Exception {
        createRetrofit(context);
        serviceInterface = getRetrofit().create(SyncServiceInterface.class);
    }

    public Observable<Response<ResponseBody>> getCasePhoto(String id, String photoKey, String photoSize) {
        return serviceInterface.getCasePhoto(PrimeroConfiguration.getCookie(), id, photoKey, photoSize);
    }

    public Observable<Response<ResponseBody>> getCaseAudio(String id) {
        return serviceInterface.getCaseAudio(PrimeroConfiguration.getCookie(), id);
    }

    public Observable<Response<JsonElement>> getCase(String id, String locale, Boolean isMobile) {
        return serviceInterface.getCase(PrimeroConfiguration.getCookie(), id, locale, isMobile);
    }

    public Observable<Response<JsonElement>> getCasesIds(String lastUpdate, Boolean isMobile) {
        return serviceInterface.getCasesIds(PrimeroConfiguration.getCookie(), lastUpdate, isMobile);
    }

    public Response<JsonElement> uploadCaseJsonProfile(RecordModel item) {
        ItemValues values = ItemValues.fromJson(new String(item.getContent().getBlob()));
        String shortUUID = RecordService.getShortUUID(item.getUniqueId());
        values.addStringItem("short_id", shortUUID);
        values.removeItem("_attachments");

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("child", values.getValues());

        Observable<Response<JsonElement>> responseObservable;
        if (!TextUtils.isEmpty(item.getInternalId())) {
            responseObservable = serviceInterface.putCase(PrimeroConfiguration.getCookie(), item
                            .getInternalId(),
                    jsonObject);
        } else {
            responseObservable = serviceInterface.postCaseExcludeMediaData(PrimeroConfiguration.getCookie(), jsonObject);
        }
        Response<JsonElement> response = responseObservable.toBlocking().first();
        if (!response.isSuccessful()) {
            throw new RuntimeException(response.errorBody().toString());
        }

        JsonObject responseJsonObject = response.body().getAsJsonObject();

        item.setInternalId(responseJsonObject.get("_id").getAsString());
        item.setInternalRev(responseJsonObject.get("_rev").getAsString());
        item.setContent(new Blob(responseJsonObject.toString().getBytes()));
        item.update();

        return response;
    }

    public void uploadAudio(RecordModel item) {
        if (item.getAudio() != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse(
                    PhotoConfig.CONTENT_TYPE_AUDIO), item.getAudio().getBlob());
            MultipartBody.Part body = MultipartBody.Part.createFormData(FORM_DATA_KEY_AUDIO, "audioFile.amr", requestFile);
            Observable<Response<JsonElement>> observable = serviceInterface.postCaseMediaData(
                    PrimeroConfiguration.getCookie(), item.getInternalId(), body);

            Response<JsonElement> response = observable.toBlocking().first();
            verifyResponse(response);
            updateRecordRev(item, response.body().getAsJsonObject().get("_rev").getAsString());
        }
    }

    public Call<Response<JsonElement>> deleteCasePhotos(String id, JsonArray photoKeys) {
        JsonObject requestBody = new JsonObject();
        JsonObject requestPhotoKeys = new JsonObject();
        for (JsonElement photoKey : photoKeys) {
            requestPhotoKeys.addProperty(photoKey.getAsString(), 1);
        }
        requestBody.add("delete_child_photo", requestPhotoKeys);
        return serviceInterface.deleteCasePhoto(PrimeroConfiguration.getCookie(), id, requestBody);
    }

    public void uploadCasePhotos(final RecordModel record) {
        List<CasePhoto> casePhotos = new CasePhotoDaoImpl().getByCaseId(record.getId());
        Observable.from(casePhotos)
                .filter(new Func1<CasePhoto, Boolean>() {
                    @Override
                    public Boolean call(CasePhoto casePhoto) {
                        return true;
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
                                Observable<Response<JsonElement>> observable = serviceInterface.postCaseMediaData(PrimeroConfiguration.getCookie(), record.getInternalId(), body);
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
                        updateRecordRev(record, response.body().getAsJsonObject().get("_rev").getAsString());
                        updateCasePhotoSyncStatus(casePhoto, true);
                        return null;
                    }
                }).toList().toBlocking().first();
    }

    private void updateRecordRev(RecordModel record, String revId) {
        record.setInternalRev(revId);
        record.update();
    }

    private void updateCasePhotoSyncStatus(CasePhoto casePhoto, boolean status) {
        casePhoto.setSynced(status);
        casePhoto.update();
    }

    private static final String FORM_DATA_KEY_AUDIO = "child[audio]";

    private static final String FORM_DATA_KEY_PHOTO = "child[photo][0]";

    private void verifyResponse(Response<JsonElement> response) {
        if (!response.isSuccessful()) {
            throw new RuntimeException();
        }
    }
}


