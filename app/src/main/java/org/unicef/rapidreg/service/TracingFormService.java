package org.unicef.rapidreg.service;

import org.unicef.rapidreg.forms.TracingTemplateForm;
import org.unicef.rapidreg.model.TracingForm;

public interface TracingFormService {
    boolean isReady();

    TracingTemplateForm getCPTemplate();

    void saveOrUpdate(TracingForm tracingForm);
}
