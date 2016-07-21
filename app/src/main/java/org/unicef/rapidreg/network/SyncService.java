package org.unicef.rapidreg.network;


import android.content.Context;

import com.google.gson.JsonElement;

import org.unicef.rapidreg.PrimeroConfiguration;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

    public Observable<Response<JsonElement>> postCaseMediaData(
            String cookie,
            String id,
            RequestBody requestBody,
            MultipartBody.Part file) {
        return serviceInterface.postCaseMediaData(cookie, id, requestBody, file);
    }

    public Observable<Response<JsonElement>> putCase(
            String cookie,
            Boolean isMobile,
            Object requestBody) {
        return serviceInterface.putCase(cookie, requestBody);
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


