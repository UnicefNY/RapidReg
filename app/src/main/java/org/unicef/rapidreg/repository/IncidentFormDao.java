package org.unicef.rapidreg.repository;

import org.unicef.rapidreg.model.IncidentForm;

public interface IncidentFormDao {
    IncidentForm getIncidentForm(String moduleId);
}
