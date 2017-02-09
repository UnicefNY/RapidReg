package org.unicef.rapidreg.repository.remote;


import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.forms.TracingTemplateForm;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import rx.Observable;


public interface FormRepository {
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

    @GET("/api/form_sections")
    Observable<IncidentTemplateForm> getIncidentForm(
            @Header("Cookie") String cookie,
            @Query("locale") String locale,
            @Query("mobile") Boolean isMobile,
            @Query("parent_form") String parentForm,
            @Query("module_id") String moduleId);
}
