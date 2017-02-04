package org.unicef.rapidreg.repository;


import org.unicef.rapidreg.model.CaseForm;

public interface CaseFormDao {
    CaseForm getCaseForm(String moduleId, String apiBaseUrl);
}
