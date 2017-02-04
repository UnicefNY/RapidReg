package org.unicef.rapidreg.repository.impl;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.model.Incident_Table;
import org.unicef.rapidreg.repository.IncidentFormDao;
import org.unicef.rapidreg.model.IncidentForm;
import org.unicef.rapidreg.model.IncidentForm_Table;

public class IncidentFormDaoImpl implements IncidentFormDao {
    @Override
    public IncidentForm getIncidentForm(String moduleId, String apiBaseUrl) {
        return SQLite.select().from(IncidentForm.class)
                .where(IncidentForm_Table.module_id.eq(moduleId))
                .and(Incident_Table.url.eq(apiBaseUrl))
                .querySingle();
    }
}
