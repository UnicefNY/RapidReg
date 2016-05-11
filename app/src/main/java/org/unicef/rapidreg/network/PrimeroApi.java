package org.unicef.rapidreg.network;

import org.unicef.rapidreg.model.LoginBody;
import org.unicef.rapidreg.model.Root;
import org.unicef.rapidreg.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

public interface PrimeroApi {

    @POST("/api/login")
    @Headers("Content-Type: application/json")
    public Call<Root> login(@Body LoginBody body);
}
