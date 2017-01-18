package org.unicef.rapidreg.repository.impl;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.repository.CaseFormDao;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.model.CaseForm_Table;

public class CaseFormDaoImpl implements CaseFormDao {
    @Override
    public CaseForm getCaseForm(String moduleId) {
        return SQLite.select().from(CaseForm.class).where(CaseForm_Table.module_id.eq(moduleId))
                .querySingle();
    }
}
