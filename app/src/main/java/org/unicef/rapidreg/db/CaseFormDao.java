package org.unicef.rapidreg.db;


import org.unicef.rapidreg.model.CaseForm;

public interface CaseFormDao {
    CaseForm getCaseForm(String moduleId);
}
