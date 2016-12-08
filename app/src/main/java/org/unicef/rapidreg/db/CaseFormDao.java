package org.unicef.rapidreg.db;

import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.BaseModelQueriable;

import org.unicef.rapidreg.model.CaseForm;

public interface CaseFormDao {
    CaseForm getCaseForm(String moduleId);
}
