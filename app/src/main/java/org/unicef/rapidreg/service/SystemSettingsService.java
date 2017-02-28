package org.unicef.rapidreg.service;

import com.google.gson.JsonElement;

import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.forms.TracingTemplateForm;
import org.unicef.rapidreg.model.SystemSettings;

import rx.Observable;


public interface SystemSettingsService {
    void initSystemSettings();
}


