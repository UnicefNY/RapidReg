package org.unicef.rapidreg.service;

import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.model.CaseForm;

public interface CaseFormService {
    boolean isReady();

    CaseTemplateForm getCPTemplate();

    CaseTemplateForm getGBVTemplate();

    void saveOrUpdate(CaseForm caseForm);
}
