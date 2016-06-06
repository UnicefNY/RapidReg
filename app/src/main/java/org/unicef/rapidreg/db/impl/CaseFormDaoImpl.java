package org.unicef.rapidreg.db.impl;

import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.db.CaseFormDao;
import org.unicef.rapidreg.model.CaseForm;

public class CaseFormDaoImpl implements CaseFormDao {

    @Override
    public CaseForm getCaseForm() {
        return SQLite.select().from(CaseForm.class).querySingle();
    }

    @Override
    public Blob getCaseFormContent() {
        CaseForm caseForm = SQLite.select().from(CaseForm.class).querySingle();

        return caseForm == null ? null : caseForm.getForm();
    }
}
