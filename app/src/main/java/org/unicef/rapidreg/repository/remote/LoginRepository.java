package org.unicef.rapidreg.repository.remote;


import org.unicef.rapidreg.model.LoginRequestBody;
import org.unicef.rapidreg.model.LoginResponse;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;


public interface LoginRepository {
    @POST("/api/login")
    Observable<Response<LoginResponse>> login(@Body LoginRequestBody body);
}
