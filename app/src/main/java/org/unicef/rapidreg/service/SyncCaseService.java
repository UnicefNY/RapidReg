package org.unicef.rapidreg.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.unicef.rapidreg.model.RecordModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;


public interface SyncCaseService {
    Observable<Response<ResponseBody>> getCasePhoto(String id, String photoKey, int
            photoSize);

    Observable<Response<ResponseBody>> getCaseAudio(String id);

    Observable<Response<JsonElement>> getCase(String id, String locale, Boolean isMobile);

    Observable<Response<JsonElement>> getCasesIds(String moduleId, String lastUpdate, Boolean isMobile);

    Response<JsonElement> uploadCaseJsonProfile(RecordModel item);

    void uploadAudio(RecordModel item);

    Call<Response<JsonElement>> deleteCasePhotos(String id, JsonArray photoKeys);

    void uploadCasePhotos(final RecordModel record);
}


