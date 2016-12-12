package org.unicef.rapidreg.db.impl;

import com.raizlabs.android.dbflow.list.FlowQueryList;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.db.IncidentDao;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.model.Incident_Table;

import java.util.ArrayList;
import java.util.List;

public class IncidentDaoImpl implements IncidentDao {
    @Override
    public Incident getIncidentByUniqueId(String uniqueId) {
        return SQLite.select().from(Incident.class).where(Incident_Table.unique_id.eq(uniqueId))
                .querySingle();
    }

    @Override
    public List<Incident> getAllIncidentsOrderByDate(boolean isASC) {
        return isASC ? getIncidentsByDateASC() : getIncidentsByDateDES();
    }

    @Override
    public List<Incident> getAllIncidentsOrderByAge(boolean isASC) {
        return isASC ? getIncidentsByAgeASC() : getIncidentsByAgeDES();

    }

    @Override
    public List<Incident> getIncidentListByConditionGroup(ConditionGroup conditionGroup) {
        return SQLite.select().from(Incident.class)
                .where(conditionGroup)
                .orderBy(Incident_Table.registration_date, false)
                .queryList();
    }

    @Override
    public Incident getIncidentById(long caseId) {
        return SQLite.select().from(Incident.class).where(Incident_Table.id.eq(caseId)).querySingle();
    }

    @Override
    public Incident getByInternalId(String id) {
        return SQLite.select().from(Incident.class).where(Incident_Table._id.eq(id)).querySingle();
    }

    @Override
    public Incident getFirst() {
        return SQLite.select().from(Incident.class).querySingle();
    }

    @Override
    public List<Long> getAllIds() {
        List<Long> result = new ArrayList<>();
        FlowQueryList<Incident> incidents = SQLite.select().from(Incident.class).flowQueryList();
        for (Incident aIncident : incidents) {
            result.add(aIncident.getId());
        }
        return result;
    }

    private List<Incident> getIncidentsByAgeASC() {
        return SQLite.select().from(Incident.class).orderBy(Incident_Table.age, true).queryList();
    }

    private List<Incident> getIncidentsByAgeDES() {
        return SQLite.select().from(Incident.class).orderBy(Incident_Table.age, false).queryList();
    }

    private List<Incident> getIncidentsByDateASC() {
        return SQLite.select().from(Incident.class)
                .orderBy(Incident_Table.registration_date, true).queryList();
    }

    private List<Incident> getIncidentsByDateDES() {
        return SQLite.select().from(Incident.class).orderBy(Incident_Table.registration_date,
                false).queryList();
    }
}
