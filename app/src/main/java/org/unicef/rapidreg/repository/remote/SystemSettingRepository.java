package org.unicef.rapidreg.repository.remote;

import com.google.gson.JsonElement;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import rx.Observable;

public interface SystemSettingRepository {
    @GET("/api/system_settings")
    Observable<Response<JsonElement>> getSystemSettings(@Header("Cookie") String cookie);
}
