package org.unicef.rapidreg.network;


import com.google.gson.JsonElement;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import rx.Observable;


public interface SyncServiceInterface {
    @GET("/api/cases?")
    Observable<Response<List<JsonElement>>> getAllCases(
            @Header("Cookie") String cookie,
            @Query("locale") String locale);

    @GET("/api/cases/{id}")
    Observable<Response<JsonElement>> getCase(
            @Header("Cookie") String cookie,
            @Path("id") String id,
            @Query("locale") String locale,
            @Query("mobile") Boolean isMobile);

    @GET("/cases/{id}/photo/{photo_key}/resized/{photo_size}")
    @Streaming
    Observable<Response<ResponseBody>> getCasePhoto(
            @Header("Cookie") String cookie,
            @Path("id") String id,
            @Path("photo_key") String photoKey,
            @Path("photo_size") String photoSize);

    @GET("/cases/{id}/audio")
    @Streaming
    Observable<Response<ResponseBody>> getCaseAudio(
            @Header("Cookie") String cookie,
            @Path("id") String id);

    @GET("/children-ids?")
    @Headers("Content-Type: application/json")
    Observable<Response<JsonElement>> getCasesIds(
            @Header("Cookie") String cookie,
            @Query("last_update") String lastUpdate,
            @Query("mobile") Boolean isMobile);

    @POST("/api/cases")
    Observable<Response<JsonElement>> postCaseExcludeMediaData(
            @Header("Cookie") String cookie,
            @Body Object requestBody);

    @PUT("/api/cases/{id}")
    Observable<Response<JsonElement>> putCase(
            @Header("Cookie") String cookie,
            @Path("id") String id,
            @Body Object requestBody);

    @Multipart
    @PUT("/api/cases/{id}")
    Observable<Response<JsonElement>> postCaseMediaData(
            @Header("Cookie") String cookie,
            @Path("id") String id,
            @Part MultipartBody.Part file);

    @POST("/api/cases")
    Observable<Response<JsonElement>> syncPostCaseExcludeMediaData(
            @Header("Cookie") String cookie,
            @Body Object requestBody);

    @PUT("/api/cases/{id}")
    Observable<Response<JsonElement>> syncPutCase(
            @Header("Cookie") String cookie,
            @Path("id") String id,
            @Body Object requestBody);
}
