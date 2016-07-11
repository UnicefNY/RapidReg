package org.unicef.rapidreg.network;


import com.google.gson.JsonElement;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;


public interface SyncServiceInterface {
    @GET("/api/cases?")
    Observable<Response<List<JsonElement>>> getAllCases(
            @Header("Cookie") String cookie,
            @Query("locale") String locale);

    @POST("/api/cases")
    Observable<Response<JsonElement>> postCase(
            @Header("Cookie") String cookie,
            @Body Object requestBody);
}
