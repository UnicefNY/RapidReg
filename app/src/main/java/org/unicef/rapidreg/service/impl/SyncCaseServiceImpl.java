package org.unicef.rapidreg.service.impl;

import android.support.v4.util.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.base.record.recordphoto.PhotoConfig;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.repository.CasePhotoDao;
import org.unicef.rapidreg.repository.remote.SyncCaseRepository;
import org.unicef.rapidreg.service.BaseRetrofitService;
import org.unicef.rapidreg.service.SyncCaseService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.TextUtils;

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


public class SyncCaseServiceImpl extends BaseRetrofitService<SyncCaseRepository> implements SyncCaseService {
    private CasePhotoDao casePhotoDao;

    @Override
    protected String getBaseUrl() {
        return PrimeroAppConfiguration.getApiBaseUrl();
    }

    public SyncCaseServiceImpl(CasePhotoDao casePhotoDao) {
        this.casePhotoDao = casePhotoDao;
    }

    public Observable<Response<JsonElement>> getCase(String id, String locale, Boolean isMobile) {
        return getRepository(SyncCaseRepository.class).getCase(PrimeroAppConfiguration.getCookie(), id, locale,
                isMobile);
    }

    public Observable<Response<ResponseBody>> getCasePhoto(String id, String photoKey, int
            photoSize) {
        return getRepository(SyncCaseRepository.class).getCasePhoto(PrimeroAppConfiguration.getCookie(), id, photoKey,
                photoSize);
    }

    public Observable<Response<ResponseBody>> getCaseAudio(String id) {
        return getRepository(SyncCaseRepository.class).getCaseAudio(PrimeroAppConfiguration.getCookie(), id);
    }

    @Override
    public Observable<Response<JsonElement>> getCasesIds(String moduleId, String lastUpdate, Boolean isMobile) {
        return getRepository(SyncCaseRepository.class).getCasesIds(PrimeroAppConfiguration.getCookie(), moduleId,
                lastUpdate, isMobile);
    }

    public Response<JsonElement> uploadCaseJsonProfile(RecordModel item) {
        ItemValuesMap itemValuesMap = ItemValuesMap.fromJson(new String(item.getContent().getBlob
                ()));
        String shortUUID = TextUtils.getLastSevenNumbers(item.getUniqueId());

        itemValuesMap.addStringItem("short_id", shortUUID);
        itemValuesMap.removeItem("_attachments");

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("child", new Gson().fromJson(new Gson().toJson(
                itemValuesMap.getValues()), JsonObject.class));

        Observable<Response<JsonElement>> responseObservable;
        if (!TextUtils.isEmpty(item.getInternalId())) {
            responseObservable = getRepository(SyncCaseRepository.class).putCase(PrimeroAppConfiguration.getCookie(),
                    item.getInternalId(), jsonObject);
        } else {
            responseObservable = getRepository(SyncCaseRepository.class).postCaseExcludeMediaData
                    (PrimeroAppConfiguration
                            .getCookie(), jsonObject);
        }
        Response<JsonElement> response = responseObservable.toBlocking().first();
        if (!response.isSuccessful()) {
            throw new RuntimeException(response.errorBody().toString());
        }

        JsonObject responseJsonObject = response.body().getAsJsonObject();

        item.setInternalId(responseJsonObject.get("_id").getAsString());
        item.setInternalRev(responseJsonObject.get("_rev").getAsString());
        item.setCaregiver(responseJsonObject.has("caregiver") ? responseJsonObject.get("caregiver").getAsString() :
                null);
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
            Observable<Response<JsonElement>> observable = getRepository(SyncCaseRepository.class).postCaseMediaData(
                    PrimeroAppConfiguration.getCookie(), item.getInternalId(), body);

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
        return getRepository(SyncCaseRepository.class).deleteCasePhoto(PrimeroAppConfiguration.getCookie(), id,
                requestBody);
    }

    public void uploadCasePhotos(final RecordModel record) {
        List<Long> casePhotosIds = casePhotoDao.getIdsByCaseId(record.getId());
        Observable.from(casePhotosIds)
                .filter(casePhotoId -> true)
                .flatMap(new Func1<Long, Observable<Pair<CasePhoto, Response<JsonElement>>>>() {
                    @Override
                    public Observable<Pair<CasePhoto, Response<JsonElement>>> call(final Long
                                                                                           casePhotoId) {
                        return Observable.create(new Observable.OnSubscribe<Pair<CasePhoto,
                                Response<JsonElement>>>() {
                            @Override
                            public void call(Subscriber<? super Pair<CasePhoto,
                                    Response<JsonElement>>> subscriber) {
                                CasePhoto casePhoto = casePhotoDao.getById(casePhotoId);

                                RequestBody requestFile = RequestBody.create(MediaType.parse
                                                (PhotoConfig.CONTENT_TYPE_IMAGE),
                                        casePhoto.getPhoto().getBlob());
                                MultipartBody.Part body = MultipartBody.Part.createFormData
                                        (FORM_DATA_KEY_PHOTO, casePhoto.getKey() + ".jpg",
                                                requestFile);
                                Observable<Response<JsonElement>> observable = getRepository(SyncCaseRepository.class)
                                        .postCaseMediaData(PrimeroAppConfiguration.getCookie(),
                                                record.getInternalId(), body);
                                Response<JsonElement> response = observable.toBlocking().first();
                                verifyResponse(response);
                                subscriber.onNext(new Pair<>(casePhoto, response));
                                subscriber.onCompleted();
                            }
                        });
                    }
                })
                .map(casePhotoResponsePair -> {
                    Response<JsonElement> response = casePhotoResponsePair.second;
                    CasePhoto casePhoto = casePhotoResponsePair.first;
                    updateRecordRev(record, response.body().getAsJsonObject().get("_rev")
                            .getAsString());
                    updateCasePhotoSyncStatus(casePhoto, true);
                    return null;
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


