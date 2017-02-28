package org.unicef.rapidreg.repository;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import org.unicef.rapidreg.model.Incident;

import java.util.List;

public interface IncidentDao {

    Incident getIncidentByUniqueId(String id);

    List<Incident> getAllIncidentsOrderByDate(boolean isASC, String ownedBy, String url);

    List<Incident> getAllIncidentsOrderByAge(boolean isASC, String ownedBy, String url);

    List<Incident> getIncidentListByConditionGroup(String ownedBy, String url, ConditionGroup conditionGroup);

    Incident getIncidentById(long incidentId);

    Incident getByInternalId(String id);

    Incident getFirst();

    Incident save(Incident incident);

    Incident update(Incident incident);

    List<Incident> getAllIncidentsByCaseUniqueId(String caseUniqueId);

    Incident delete(Incident deleteIncident);

    List<Incident> getALLSyncedRecords(String ownedBy);
}
