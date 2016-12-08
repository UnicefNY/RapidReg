package org.unicef.rapidreg.network;


import org.unicef.rapidreg.forms.CaseTemplateForm;
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


public interface AuthServiceInterface {
    @POST("/api/login")
    Observable<Response<LoginResponse>> login(@Body LoginRequestBody body);

    @GET("/api/form_sections")
    Observable<CaseTemplateForm> getCaseForm(
            @Header("Cookie") String cookie,
            @Query("locale") String locale,
            @Query("mobile") Boolean isMobile,
            @Query("parent_form") String parentForm,
            @Query("module_id") String moduleId);

    @GET("/api/form_sections")
    Observable<TracingTemplateForm> getTracingForm(
            @Header("Cookie") String cookie,
            @Query("locale") String locale,
            @Query("mobile") Boolean isMobile,
            @Query("parent_form") String parentForm,
            @Query("module_id") String moduleId);
}
