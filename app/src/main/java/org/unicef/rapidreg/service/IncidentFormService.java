package org.unicef.rapidreg.service;

import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.model.IncidentForm;

public interface IncidentFormService {

    boolean isReady();

    IncidentTemplateForm getGBVTemplate();

    void saveOrUpdate(IncidentForm incidentForm);

    List<String> getViolenceTypeList();
}
