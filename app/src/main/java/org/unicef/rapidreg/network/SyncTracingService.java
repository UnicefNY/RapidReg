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
import org.unicef.rapidreg.db.impl.TracingPhotoDaoImpl;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.model.TracingPhoto;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingService;
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


public class SyncTracingService extends BaseRetrofitService {

    private SyncTracingsServiceInterface serviceInterface;

    @Override
    String getBaseUrl() {
        return PrimeroConfiguration.getApiBaseUrl();
    }

    public SyncTracingService(Context context) throws Exception {
        createRetrofit(context);
        serviceInterface = getRetrofit().create(SyncTracingsServiceInterface.class);
    }

    public Observable<Response<ResponseBody>> getPhoto(String id, String photoKey, String photoSize) {
        return serviceInterface.getPhoto(PrimeroConfiguration.getCookie(), id, photoKey, photoSize);
    }

    public Observable<Response<ResponseBody>> getAudio(String id) {
        return serviceInterface.getAudio(PrimeroConfiguration.getCookie(), id);
    }

    public Observable<Response<JsonElement>> get(String id, String locale, Boolean isMobile) {
        return serviceInterface.get(PrimeroConfiguration.getCookie(), id, locale, isMobile);
    }

    public Observable<Response<JsonElement>> getsIds(String lastUpdate, Boolean isMobile) {
        return serviceInterface.getsIds(PrimeroConfiguration.getCookie(), lastUpdate, isMobile);
    }

    public Response<JsonElement> uploadJsonProfile(RecordModel item) {
        ItemValues values = ItemValues.fromJson(new String(item.getContent().getBlob()));
        String shortUUID = RecordService.getShortUUID(item.getUniqueId());
        values.addStringItem("short_id", shortUUID);
        values.addStringItem(TracingService.TRACING_ID, shortUUID);
        values.addStringItem("tracing_request_id", item.getUniqueId());
        values.removeItem("_attachments");

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("tracing_request", values.getValues());

        Observable<Response<JsonElement>> responseObservable;
        if (!TextUtils.isEmpty(item.getInternalId())) {
            responseObservable = serviceInterface.put(PrimeroConfiguration.getCookie(), item
                            .getInternalId(),
                    jsonObject);
        } else {
            responseObservable = serviceInterface.postExcludeMediaData(PrimeroConfiguration.getCookie(), jsonObject);
        }
        Response<JsonElement> response = responseObservable.toBlocking().first();
        if (!response.isSuccessful()) {
            throw new RuntimeException();
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
            Observable<Response<JsonElement>> observable = serviceInterface.postMediaData(
                    PrimeroConfiguration.getCookie(), item.getInternalId(), body);

            Response<JsonElement> response = observable.toBlocking().first();

            verifyResponse(response);

            item.setAudioSynced(true);
            updateRecordRev(item, response.body().getAsJsonObject().get("_rev").getAsString());
        }
    }

    public Call<Response<JsonElement>> deletePhotos(String id, JsonArray photoKeys) {
        JsonObject requestBody = new JsonObject();
        JsonObject requestPhotoKeys = new JsonObject();
        for (JsonElement photoKey : photoKeys) {
            requestPhotoKeys.addProperty(photoKey.getAsString(), 1);
        }
        requestBody.add("delete_tracing_request_photo", requestPhotoKeys);
        return serviceInterface.deletePhoto(PrimeroConfiguration.getCookie(), id, requestBody);
    }

    public void uploadPhotos(final RecordModel record) {
        List<TracingPhoto> tracingPhotos = new TracingPhotoDaoImpl().getByTracingId(record.getId());
        Observable.from(tracingPhotos)
                .filter(new Func1<TracingPhoto, Boolean>() {
                    @Override
                    public Boolean call(TracingPhoto tracingPhoto) {
                        return true;
                    }
                })
                .flatMap(new Func1<TracingPhoto, Observable<Pair<TracingPhoto, Response<JsonElement>>>>() {
                    @Override
                    public Observable<Pair<TracingPhoto, Response<JsonElement>>> call(final TracingPhoto tracingPhoto) {
                        return Observable.create(new Observable.OnSubscribe<Pair<TracingPhoto, Response<JsonElement>>>() {
                            @Override
                            public void call(Subscriber<? super Pair<TracingPhoto, Response<JsonElement>>> subscriber) {
                                RequestBody requestFile = RequestBody.create(MediaType.parse(PhotoConfig.CONTENT_TYPE_IMAGE),
                                        tracingPhoto.getPhoto().getBlob());
                                MultipartBody.Part body = MultipartBody.Part.createFormData(FORM_DATA_KEY_PHOTO, tracingPhoto.getKey() + ".jpg",
                                        requestFile);
                                Observable<Response<JsonElement>> observable = serviceInterface.postMediaData(PrimeroConfiguration.getCookie(), record.getInternalId(), body);
                                Response<JsonElement> response = observable.toBlocking().first();
                                verifyResponse(response);
                                subscriber.onNext(new Pair<>(tracingPhoto, response));
                                subscriber.onCompleted();
                            }
                        });
                    }
                })
                .map(new Func1<Pair<TracingPhoto, Response<JsonElement>>, Object>() {
                    @Override
                    public Object call(Pair<TracingPhoto, Response<JsonElement>> tracingPhotoResponsePair) {
                        Response<JsonElement> response = tracingPhotoResponsePair.second;
                        TracingPhoto tracingPhoto = tracingPhotoResponsePair.first;
                        updateRecordRev(record, response.body().getAsJsonObject().get("_rev").getAsString());
                        updateTracingPhotoSyncStatus(tracingPhoto, true);
                        return null;
                    }
                }).toList().toBlocking().first();
    }

    private void updateRecordRev(RecordModel record, String revId) {
        record.setInternalRev(revId);
        record.update();
    }

    private void updateTracingPhotoSyncStatus(TracingPhoto tracingPhoto, boolean status) {
        tracingPhoto.setSynced(status);
        tracingPhoto.update();
    }

    private static final String FORM_DATA_KEY_AUDIO = "tracing_request[audio]";

    private static final String FORM_DATA_KEY_PHOTO = "tracing_request[photo][0]";

    private void verifyResponse(Response<JsonElement> response) {
        if (!response.isSuccessful()) {
            throw new RuntimeException();
        }
    }
}


