package org.unicef.rapidreg.network;


import android.content.Context;

import org.unicef.rapidreg.PrimeroConfiguration;

import java.util.ArrayList;
import java.util.Map;

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


    public Observable<Response<ArrayList<Map<String, Object>>>> getAllCasesRx(
            String cookie,
            String locale,
            Boolean isMobile) {
        return serviceInterface.getAllCases(cookie, locale);
    }

}


