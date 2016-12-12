package org.unicef.rapidreg.db.impl;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.db.IncidentFormDao;
import org.unicef.rapidreg.model.IncidentForm;
import org.unicef.rapidreg.model.IncidentForm_Table;

public class IncidentFormDaoImpl implements IncidentFormDao {
    @Override
    public IncidentForm getIncidentForm(String moduleId) {
        return SQLite.select().from(IncidentForm.class).where(IncidentForm_Table.module_id.
                eq(moduleId)).querySingle();
    }
}
