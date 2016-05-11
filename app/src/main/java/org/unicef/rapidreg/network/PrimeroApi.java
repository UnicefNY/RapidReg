package org.unicef.rapidreg.network;

import org.unicef.rapidreg.model.LoginBody;
import org.unicef.rapidreg.model.Root;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PrimeroApi {

    @POST("/api/login")
    @Headers("Content-Type: application/json")
    public Call<Root> login(@Body LoginBody body);
}
