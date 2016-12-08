package org.unicef.rapidreg.service;

import org.unicef.rapidreg.forms.CaseTemplateForm;

public interface CaseFormService {
    boolean isReady();

    CaseTemplateForm getCPTemplate();

    CaseTemplateForm getGBVTemplate();

    void saveOrUpdate(org.unicef.rapidreg.model.CaseForm caseForm);
}
