package org.unicef.rapidreg.db;

import org.unicef.rapidreg.model.IncidentForm;

public interface IncidentFormDao {
    IncidentForm getIncidentForm(String moduleId);
}
