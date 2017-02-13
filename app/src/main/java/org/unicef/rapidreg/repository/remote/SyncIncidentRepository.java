package org.unicef.rapidreg.repository.remote;

import com.google.gson.JsonElement;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface SyncIncidentRepository {

    @GET("/incident-ids")
    @Headers("Content-Type: application/json")
    Observable<Response<JsonElement>> getIncidentIds(
            @Header("Cookie") String cookie,
            @Query("last_update") String lastUpdate,
            @Query("mobile") Boolean isMobile);

    @GET("/api/incidents/{id}")
    Observable<Response<JsonElement>> getIncident(
            @Header("Cookie") String cookie,
            @Path("id") String id,
            @Query("locale") String locale,
            @Query("mobile") Boolean isMobile);

    @POST("/api/incidents")
    Observable<Response<JsonElement>> postIncident(
            @Header("Cookie") String cookie,
            @Body Object requestBody);

    @PUT("/api/incidents/{id}")
    Observable<Response<JsonElement>> putIncident(
            @Header("Cookie") String cookie,
            @Path("id") String id,
            @Body Object requestBody);
}
