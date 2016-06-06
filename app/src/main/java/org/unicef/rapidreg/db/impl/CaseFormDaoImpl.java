package org.unicef.rapidreg.db.impl;

import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.db.CaseFormDao;
import org.unicef.rapidreg.model.ChildCase;

public class CaseFormDaoImpl implements CaseFormDao {

    @Override
    public Blob getForm() {
        ChildCase childCase = SQLite.select().from(ChildCase.class).querySingle();

        return childCase == null ? null : childCase.getForm();
    }
}
