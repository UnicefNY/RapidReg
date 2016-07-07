package org.unicef.rapidreg.network;


import java.util.ArrayList;
import java.util.Map;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import rx.Observable;


public interface SyncServiceInterface {

    @GET("/api/cases?")
    Observable<Response<ArrayList<Map<String, Object>>>> getAllCases(
            @Header("Cookie") String cookie,
            @Query("locale") String locale);

}
