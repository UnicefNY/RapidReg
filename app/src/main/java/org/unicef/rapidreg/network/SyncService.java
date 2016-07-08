package org.unicef.rapidreg.network;


import android.content.Context;

import com.google.gson.JsonElement;

import org.unicef.rapidreg.PrimeroConfiguration;

import java.util.List;

import okhttp3.ResponseBody;
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

    public Observable<Response<String>> postCase(
            String cookie,
            Boolean isMobile,
            String requestBody) {
        return serviceInterface.postCase(cookie, requestBody);
    }
}


