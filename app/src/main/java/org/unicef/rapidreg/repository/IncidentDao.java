package org.unicef.rapidreg.repository;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import org.unicef.rapidreg.model.Incident;

import java.util.List;

public interface IncidentDao {

    Incident getIncidentByUniqueId(String id);

    List<Incident> getAllIncidentsOrderByDate(boolean isASC, String ownedBy);

    List<Incident> getAllIncidentsOrderByAge(boolean isASC, String ownedBy);

    List<Incident> getIncidentListByConditionGroup(ConditionGroup conditionGroup);

    Incident getIncidentById(long incidentId);

    Incident getByInternalId(String id);

    Incident getFirst();

    List<Long> getAllIds(String ownedBy);

    Incident save(Incident incident);

    Incident update(Incident incident);

}
