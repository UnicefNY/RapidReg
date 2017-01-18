package org.unicef.rapidreg.repository.remote;


import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.forms.TracingTemplateForm;
import org.unicef.rapidreg.model.LoginRequestBody;
import org.unicef.rapidreg.model.LoginResponse;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;


public interface LoginRepository {
    @POST("/api/login")
    Observable<Response<LoginResponse>> login(@Body LoginRequestBody body);
}
