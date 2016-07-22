package org.unicef.rapidreg.network;


import android.content.Context;

import com.google.gson.JsonElement;

import org.unicef.rapidreg.PrimeroConfiguration;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;


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


    public Observable<Response<List<JsonElement>>> getAllCasesRx(
            String cookie,
            String locale,
            Boolean isMobile) {
        return serviceInterface.getAllCases(cookie, locale);
    }

    public Observable<Response<JsonElement>> postCaseExcludeMediaData(
            String cookie,
            Boolean isMobile,
            Object requestBody) {
        return serviceInterface.postCaseExcludeMediaData(cookie, requestBody);
    }
    public Observable<Response<JsonElement>> syncPostCaseExcludeMediaData(
            String cookie,
            Boolean isMobile,
            Object requestBody) {
        return serviceInterface.syncPostCaseExcludeMediaData(cookie, requestBody);
    }

    public Observable<Response<JsonElement>> postCaseMediaData(
            String cookie,
            String id,
            MultipartBody.Part file) {
        return serviceInterface.postCaseMediaData(cookie, id, file);
    }

    public Observable<Response<JsonElement>> putCase(
            String cookie,
            String id,
            Boolean isMobile,
            Object requestBody) {
        return serviceInterface.putCase(cookie, id, requestBody);
    }

    public Observable<Response<JsonElement>> syncPutCase(
            String cookie,
            String id,
            Boolean isMobile,
            Object requestBody) {
        return serviceInterface.syncPutCase(cookie, id, requestBody);
    }

    public Observable<Response<ResponseBody>> getCasePhoto(
            String cookie,
            String id,
            String photoKey,
            String photoSize) {
        return serviceInterface.getCasePhoto(cookie, id, photoKey, photoSize);
    }

    public Observable<Response<ResponseBody>> getCaseAudio(
            String cookie,
            String id) {
        return serviceInterface.getCaseAudio(cookie, id);
    }

    public Observable<Response<JsonElement>> getCase(
            String cookie,
            String id,
            String locale,
            Boolean isMobile) {
        return serviceInterface.getCase(cookie, id, locale, isMobile);
    }

    public Observable<Response<JsonElement>> getCasesIds(
            String cookie,
            String lastUpdate,
            Boolean isMobile) {
        return serviceInterface.getCasesIds(cookie, lastUpdate, isMobile);
    }
}


