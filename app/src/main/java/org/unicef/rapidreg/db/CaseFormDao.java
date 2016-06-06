package org.unicef.rapidreg.db;

import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.model.CaseForm;

public interface CaseFormDao {
    Blob getForm();

    CaseForm getCaseForm();
}
