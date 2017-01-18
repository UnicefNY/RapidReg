package org.unicef.rapidreg.service.impl;


import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.base.record.recordphoto.PhotoConfig;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.model.TracingPhoto;
import org.unicef.rapidreg.repository.TracingPhotoDao;
import org.unicef.rapidreg.repository.impl.TracingPhotoDaoImpl;
import org.unicef.rapidreg.repository.remote.SyncTracingsRepository;
import org.unicef.rapidreg.service.BaseRetrofitService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.SyncTracingService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

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

import static org.unicef.rapidreg.service.TracingService.TRACING_ID;


public class SyncTracingServiceImpl extends BaseRetrofitService implements SyncTracingService{

    private SyncTracingsRepository syncTracingsRepository;

    private RecordService recordService;

    private TracingPhotoDao tracingPhotoDao;

    @Override
    protected String getBaseUrl() {
        return PrimeroAppConfiguration.getApiBaseUrl();
    }

    public SyncTracingServiceImpl(RecordService recordService, TracingPhotoDao
            tracingPhotoDao) {
        createRetrofit();
        this.recordService = recordService;
        this.tracingPhotoDao = tracingPhotoDao;
        syncTracingsRepository = getRetrofit().create(SyncTracingsRepository.class);
    }

    public Observable<Response<ResponseBody>> getPhoto(String id, String photoKey, String
            photoSize) {
        return syncTracingsRepository.getPhoto(PrimeroAppConfiguration.getCookie(), id, photoKey, photoSize);
    }

    public Observable<Response<ResponseBody>> getAudio(String id) {
        return syncTracingsRepository.getAudio(PrimeroAppConfiguration.getCookie(), id);
    }

    public Observable<Response<JsonElement>> get(String id, String locale, Boolean isMobile) {
        return syncTracingsRepository.get(PrimeroAppConfiguration.getCookie(), id, locale, isMobile);
    }

    public Observable<Response<JsonElement>> getIds(String lastUpdate, Boolean isMobile) {
        return syncTracingsRepository.getIds(PrimeroAppConfiguration.getCookie(), lastUpdate, isMobile);
    }

    public Response<JsonElement> uploadJsonProfile(RecordModel item) {
        ItemValuesMap values = ItemValuesMap.fromJson(new String(item.getContent().getBlob()));
        String shortUUID = recordService.getShortUUID(item.getUniqueId());
        values.addStringItem("short_id", shortUUID);
        values.addStringItem(TRACING_ID, shortUUID);
        values.addStringItem("tracing_request_id", item.getUniqueId());
        values.removeItem("_attachments");

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("tracing_request", new Gson().fromJson(new Gson().toJson(values.getValues
                ()), JsonObject.class));

        Observable<Response<JsonElement>> responseObservable;
        if (!TextUtils.isEmpty(item.getInternalId())) {
            responseObservable = syncTracingsRepository.put(PrimeroAppConfiguration.getCookie(), item
                            .getInternalId(),
                    jsonObject);
        } else {
            responseObservable = syncTracingsRepository.postExcludeMediaData(PrimeroAppConfiguration
                    .getCookie(), jsonObject);
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
            MultipartBody.Part body = MultipartBody.Part.createFormData(FORM_DATA_KEY_AUDIO,
                    "audioFile.amr", requestFile);
            Observable<Response<JsonElement>> observable = syncTracingsRepository.postMediaData(
                    PrimeroAppConfiguration.getCookie(), item.getInternalId(), body);

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
        return syncTracingsRepository.deletePhoto(PrimeroAppConfiguration.getCookie(), id, requestBody);
    }

    public void uploadPhotos(final RecordModel record) {
        List<Long> tracingPhotos = new TracingPhotoDaoImpl().getIdsByTracingId(record.getId());
        Observable.from(tracingPhotos)
                .filter(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long tracingPhotoId) {
                        return true;
                    }
                })
                .flatMap(new Func1<Long, Observable<Pair<TracingPhoto, Response<JsonElement>>>>() {
                    @Override
                    public Observable<Pair<TracingPhoto, Response<JsonElement>>> call(final Long
                                                                                              tracingPhotoId) {
                        return Observable.create(new Observable.OnSubscribe<Pair<TracingPhoto,
                                Response<JsonElement>>>() {
                            @Override
                            public void call(Subscriber<? super Pair<TracingPhoto,
                                    Response<JsonElement>>> subscriber) {
                                TracingPhoto tracingPhoto = tracingPhotoDao.getById
                                        (tracingPhotoId);

                                RequestBody requestFile = RequestBody.create(MediaType.parse
                                                (PhotoConfig.CONTENT_TYPE_IMAGE),
                                        tracingPhoto.getPhoto().getBlob());
                                MultipartBody.Part body = MultipartBody.Part.createFormData
                                        (FORM_DATA_KEY_PHOTO, tracingPhoto.getKey() + ".jpg",
                                                requestFile);
                                Observable<Response<JsonElement>> observable = syncTracingsRepository
                                        .postMediaData(PrimeroAppConfiguration.getCookie(), record
                                                .getInternalId(), body);
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
                    public Object call(Pair<TracingPhoto, Response<JsonElement>>
                                               tracingPhotoResponsePair) {
                        Response<JsonElement> response = tracingPhotoResponsePair.second;
                        TracingPhoto tracingPhoto = tracingPhotoResponsePair.first;
                        updateRecordRev(record, response.body().getAsJsonObject().get("_rev")
                                .getAsString());
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


