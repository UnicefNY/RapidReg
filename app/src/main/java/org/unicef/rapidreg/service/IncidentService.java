package org.unicef.rapidreg.service;

import android.util.Log;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.repository.IncidentDao;
import org.unicef.rapidreg.repository.impl.IncidentDaoImpl;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.TextUtils;
import org.unicef.rapidreg.utils.Utils;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.unicef.rapidreg.model.RecordModel.EMPTY_AGE;

public class IncidentService extends RecordService {
    public static final String TAG = IncidentService.class.getSimpleName();
    public static final String INCIDENT_DISPLAY_ID = "incident_id_display";
    public static final String INCIDENT_ID = "incident_id";
    public static final String INCIDENT_PRIMARY_ID = "incident_primary_id";

    private IncidentDao incidentDao = new IncidentDaoImpl();

    public IncidentService() {}

    public IncidentService(IncidentDao incidentDao) {
        this.incidentDao = incidentDao;
    }

    public Incident getById(long incidentId) {
        return incidentDao.getIncidentById(incidentId);
    }

    public Incident getByUniqueId(String uniqueId) {
        return incidentDao.getIncidentByUniqueId(uniqueId);
    }

    public List<Incident> getAll() {
        return incidentDao.getAllIncidentsOrderByDate(false, PrimeroAppConfiguration.getCurrentUsername(), TextUtils
                .lintUrl(PrimeroAppConfiguration.getApiBaseUrl()));
    }

    public List<Long> getAllOrderByDateASC() {
        return extractIds(incidentDao.getAllIncidentsOrderByDate(true, PrimeroAppConfiguration.getCurrentUsername(),
                TextUtils.lintUrl(PrimeroAppConfiguration.getApiBaseUrl())));
    }

    public List<Long> getAllOrderByDateDES() {
        return extractIds(incidentDao.getAllIncidentsOrderByDate(false, PrimeroAppConfiguration.getCurrentUsername(),
                TextUtils.lintUrl(PrimeroAppConfiguration.getApiBaseUrl())));
    }

    public List<Long> getAllOrderByAgeASC() {
        return extractIds(incidentDao.getAllIncidentsOrderByAge(true, PrimeroAppConfiguration.getCurrentUsername(),
                TextUtils.lintUrl(PrimeroAppConfiguration.getApiBaseUrl())));
    }

    public List<Long> getAllOrderByAgeDES() {
        return extractIds(incidentDao.getAllIncidentsOrderByAge(false, PrimeroAppConfiguration.getCurrentUsername(),
                TextUtils.lintUrl(PrimeroAppConfiguration.getApiBaseUrl())));
    }

    public List<Long> getSearchResult(String uniqueId, String survivorCode, int ageFrom, int ageTo, String
            typeOfViolence, String location) {
        ConditionGroup searchCondition = getSearchCondition(uniqueId, survivorCode, ageFrom, ageTo, typeOfViolence,
                location);
        return extractIds(incidentDao.getIncidentListByConditionGroup(searchCondition));
    }

    private ConditionGroup getSearchCondition(String uniqueId, String survivorCode, int ageFrom, int ageTo, String
            typeOfViolence, String location) {
        ConditionGroup conditionGroup = ConditionGroup.clause();
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_UNIQUE_ID).build())
                .like(getWrappedCondition(uniqueId)));
        conditionGroup.and(Condition.column(NameAlias.builder(Incident.COLUMN_SURVIVOR_CODE).build())
                .like(getWrappedCondition(survivorCode)));
        SQLCondition ageSearchCondition = generateAgeSearchCondition(ageFrom, ageTo);
        if (ageSearchCondition != null) {
            conditionGroup.and(ageSearchCondition);
        }
        conditionGroup.and(Condition.column(NameAlias.builder(Incident.COLUMN_SURVIVOR_CODE).build())
                .like(getWrappedCondition(survivorCode)));
        conditionGroup.and(Condition.column(NameAlias.builder(Incident.COLUMN_TYPE_OF_VIOLENCE).build())
                .like(getWrappedCondition(typeOfViolence)));
        conditionGroup.and(Condition.column(NameAlias.builder(Incident.COLUMN_LOCATION).build())
                .like(getWrappedCondition(location)));
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_CREATED_BY).build())
                .eq(PrimeroAppConfiguration.getCurrentUser().getUsername()));

        return conditionGroup;
    }

    private List<Long> extractIds(List<Incident> incidents) {
        List<Long> result = new ArrayList<>();
        for (Incident incident : incidents) {
            result.add(incident.getId());
        }
        return result;
    }

    public Incident saveOrUpdate(ItemValuesMap itemValues) throws IOException {
        if (itemValues.getAsString(INCIDENT_ID) == null) {
            return save(itemValues);
        } else {
            Log.d(TAG, "update the existing incident");
            return update(itemValues);
        }
    }

    public Incident save(ItemValuesMap itemValues) throws IOException {
        if (!itemValues.has(DATE_OF_INTERVIEW)) {
            itemValues.addStringItem(DATE_OF_INTERVIEW, getCurrentRegistrationDateAsString());
        }
        String uniqueId = generateUniqueId();
        itemValues.addStringItem(INCIDENT_DISPLAY_ID, getShortUUID(uniqueId));
        itemValues.addStringItem(INCIDENT_ID, uniqueId);
        String username = PrimeroAppConfiguration.getCurrentUser().getUsername();
        itemValues.addStringItem(MODULE, MODULE_GBV_CASE);
        itemValues.addStringItem(RECORD_OWNED_BY, username);
        itemValues.addStringItem(RECORD_CREATED_BY, username);
        itemValues.addStringItem(PREVIOUS_OWNER, username);

        Gson gson = new Gson();
        Date date = new Date(Calendar.getInstance().getTimeInMillis());
        Blob tracingBlob = new Blob(gson.toJson(itemValues.getValues()).getBytes());

        Incident incident = new Incident();
        incident.setUniqueId(uniqueId);
        incident.setCreateDate(date);
        incident.setLastUpdatedDate(date);
        incident.setContent(tracingBlob);
        incident.setName(getName(itemValues));
        incident.setSurvivorCode(getSurvivorCode(itemValues));
        incident.setTypeOfViolence(getTypeOfViolence(itemValues));
        incident.setLocation(getLocation(itemValues));
        int age = itemValues.has(AGE) ? itemValues.getAsInt(AGE) : EMPTY_AGE;
        incident.setUrl(TextUtils.lintUrl(PrimeroAppConfiguration.getApiBaseUrl()));
        incident.setAge(age);
        incident.setCaregiver(getCaregiverName(itemValues));
        incident.setRegistrationDate(Utils.getRegisterDate(itemValues.getAsString(DATE_OF_INTERVIEW)));
        incident.setCreatedBy(username);
        incident.setOwnedBy(username);
        incidentDao.save(incident);
        return incident;
    }


    public Incident update(ItemValuesMap itemValues) throws IOException {
        Gson gson = new Gson();
        Blob blob = new Blob(gson.toJson(itemValues.getValues()).getBytes());

        Incident incident = incidentDao.getIncidentByUniqueId(itemValues.getAsString(INCIDENT_ID));
        incident.setLastUpdatedDate(new Date(Calendar.getInstance().getTimeInMillis()));
        incident.setContent(blob);
        incident.setName(getName(itemValues));
        incident.setName(getName(itemValues));
        incident.setSurvivorCode(getSurvivorCode(itemValues));
        incident.setTypeOfViolence(getTypeOfViolence(itemValues));
        incident.setLocation(getLocation(itemValues));
        int age = itemValues.getAsInt(AGE) != null ? itemValues.getAsInt(AGE) : 0;
        incident.setAge(age);
        incident.setCaregiver(getCaregiverName(itemValues));
        setSyncedStatus(incident);

        if (itemValues.has(DATE_OF_INTERVIEW)) {
            incident.setRegistrationDate(Utils.getRegisterDate(itemValues.getAsString(DATE_OF_INTERVIEW)));
        }

        incident = incidentDao.update(incident);

        return incident;
    }


    private String getName(ItemValuesMap values) {
        return values.getAsString(RELATION_NAME) + " "
                + values.getAsString(RELATION_AGE) + " "
                + values.getAsString(RELATION_NICKNAME);
    }

    public Incident getByInternalId(String id) {
        return incidentDao.getByInternalId(id);
    }

    public boolean hasSameRev(String id, String rev) {
        Incident incident = incidentDao.getByInternalId(id);
        return incident != null && rev.equals(incident.getInternalRev());
    }

}
