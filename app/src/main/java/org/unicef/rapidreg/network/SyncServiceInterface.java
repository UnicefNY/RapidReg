package org.unicef.rapidreg.network;


import com.google.gson.JsonElement;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;


public interface SyncServiceInterface {
    @GET("/api/cases?")
    Observable<Response<List<JsonElement>>> getAllCases(
            @Header("Cookie") String cookie,
            @Query("locale") String locale);

    @POST("/api/cases")
    Observable<Response<JsonElement>> postCaseExcludeMediaData(
            @Header("Cookie") String cookie,
            @Body Object requestBody);

    @PUT("/api/cases")
    Observable<Response<JsonElement>> putCase(
            @Header("Cookie") String cookie,
            @Body Object requestBody);

    @POST("/api/cases/{_id}")
    Observable<Response<JsonElement>> postCaseMediaData(
            @Header("Cookie") String cookie,
            @Path("_id") String id,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file);
}
