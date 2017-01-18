package org.unicef.rapidreg.service;

import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.forms.TracingTemplateForm;
import org.unicef.rapidreg.model.LoginRequestBody;
import org.unicef.rapidreg.model.LoginResponse;

import retrofit2.Response;
import rx.Observable;


public interface FormRemoteService {
    Observable<CaseTemplateForm> getCaseForm(String cookie, String locale,
                                             Boolean isMobile, String parentForm, String moduleId);

    Observable<TracingTemplateForm> getTracingForm(String cookie, String locale,
                                                   Boolean isMobile, String parentForm, String moduleId);

    Observable<IncidentTemplateForm> getIncidentForm(String cookie, String locale,
                                                     Boolean isMobile, String parentForm, String moduleId);
}


