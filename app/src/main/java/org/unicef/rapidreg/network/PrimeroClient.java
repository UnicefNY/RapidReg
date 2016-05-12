package org.unicef.rapidreg.network;

import org.unicef.rapidreg.model.LoginRequestBody;
import org.unicef.rapidreg.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PrimeroClient {

    @POST("/api/login")
    @Headers("Content-Type: application/json")
    public Call<Response<LoginResponse>> login(@Body LoginRequestBody body);
}
