package org.unicef.rapidreg.repository.remote;


import com.google.gson.JsonElement;

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

public interface SyncCaseRepository {
    @GET("/api/cases/{id}")
    Observable<Response<JsonElement>> getCase(
            @Header("Cookie") String cookie,
            @Path("id") String id,
            @Query("locale") String locale,
            @Query("mobile") Boolean isMobile);

    @GET("/cases/{id}/photo/{photo_key}/resized/{photo_size}")
    Observable<Response<ResponseBody>> getCasePhoto(
            @Header("Cookie") String cookie,
            @Path("id") String id,
            @Path("photo_key") String photoKey,
            @Path("photo_size") int photoSize);

    @GET("/cases/{id}/audio")
    Observable<Response<ResponseBody>> getCaseAudio(
            @Header("Cookie") String cookie,
            @Path("id") String id);

    @GET("/children-ids?")
    @Headers("Content-Type: application/json")
    Observable<Response<JsonElement>> getCasesIds(
            @Header("Cookie") String cookie,
            @Query("module_id") String moduleId,
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

    @PUT("/api/cases/{id}")
    Call<Response<JsonElement>> deleteCasePhoto(
            @Header("Cookie") String cookie,
            @Path("id") String id,
            @Body Object requestBody);
}
