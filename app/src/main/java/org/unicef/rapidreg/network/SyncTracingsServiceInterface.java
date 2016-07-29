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
import rx.Observable;


public interface SyncTracingsServiceInterface {
    @GET("/api/tracing_requests?")
    Observable<Response<List<JsonElement>>> getAll(
            @Header("Cookie") String cookie,
            @Query("locale") String locale);

    @GET("/api/tracing_requests/{id}")
    Observable<Response<JsonElement>> get(
            @Header("Cookie") String cookie,
            @Path("id") String id,
            @Query("locale") String locale,
            @Query("mobile") Boolean isMobile);

    @GET("/tracing_requests/{id}/photo/{photo_key}/resized/{photo_size}")
    Observable<Response<ResponseBody>> getPhoto(
            @Header("Cookie") String cookie,
            @Path("id") String id,
            @Path("photo_key") String photoKey,
            @Path("photo_size") String photoSize);

    @GET("/tracing_requests/{id}/audio")
    Observable<Response<ResponseBody>> getAudio(
            @Header("Cookie") String cookie,
            @Path("id") String id);

    @GET("/tracing_requests-ids?")
    @Headers("Content-Type: application/json")
    Observable<Response<JsonElement>> getIds(
            @Header("Cookie") String cookie,
            @Query("last_update") String lastUpdate,
            @Query("mobile") Boolean isMobile);

    @POST("/api/tracing_requests")
    Observable<Response<JsonElement>> postExcludeMediaData(
            @Header("Cookie") String cookie,
            @Body Object requestBody);

    @PUT("/api/tracing_requests/{id}")
    Observable<Response<JsonElement>> put(
            @Header("Cookie") String cookie,
            @Path("id") String id,
            @Body Object requestBody);

    @Multipart
    @PUT("/api/tracing_requests/{id}")
    Observable<Response<JsonElement>> postMediaData(
            @Header("Cookie") String cookie,
            @Path("id") String id,
            @Part MultipartBody.Part file);

    @POST("/api/tracing_requests")
    Observable<Response<JsonElement>> syncPostExcludeMediaData(
            @Header("Cookie") String cookie,
            @Body Object requestBody);

    @PUT("/api/tracing_requests/{id}")
    Observable<Response<JsonElement>> syncPut(
            @Header("Cookie") String cookie,
            @Path("id") String id,
            @Body Object requestBody);

    @PUT("/api/tracing_requests/{id}")
    Call<Response<JsonElement>> deletePhoto(
            @Header("Cookie") String cookie,
            @Path("id") String id,
            @Body Object requestBody);
}
