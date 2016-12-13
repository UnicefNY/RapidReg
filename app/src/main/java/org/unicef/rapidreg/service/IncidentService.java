package org.unicef.rapidreg.service;

import android.util.Log;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.NameAlias;

import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.db.IncidentDao;
import org.unicef.rapidreg.db.impl.IncidentDaoImpl;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.utils.StreamUtil;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IncidentService extends RecordService{
    public static final String TAG = IncidentService.class.getSimpleName();
    public static final String INCIDENT_DISPLAY_ID = "incident_id_display";
    public static final String INCIDENT_ID = "incident_id";
    public static final String INCIDENT_PRIMARY_ID = "incident_primary_id";

    private IncidentDao incidentDao = new IncidentDaoImpl();

    public IncidentService() {

    }

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
        return incidentDao.getAllIncidentsOrderByDate(false);
    }

    public List<Long> getAllIds(){
        return incidentDao.getAllIds();
    }

    public List<Long> getAllOrderByDateASC() {
        return extractIds(incidentDao.getAllIncidentsOrderByDate(true));
    }

    public List<Long> getAllOrderByDateDES() {
        return extractIds(incidentDao.getAllIncidentsOrderByDate(false));
    }

    public List<Long> getAllOrderByAgeASC() {
        return extractIds(incidentDao.getAllIncidentsOrderByAge(true));
    }

    public List<Long> getAllOrderByAgeDES() {
        return extractIds(incidentDao.getAllIncidentsOrderByAge(false));
    }

    public List<Long> getSearchResult(String uniqueId, String name, int ageFrom,
                                      int ageTo, Date date) {
        ConditionGroup searchCondition = getSearchCondition(uniqueId, name, ageFrom, ageTo, date);
        return extractIds(incidentDao.getIncidentListByConditionGroup(searchCondition));
    }

    private List<Long> extractIds(List<Incident> incidents){
        List<Long> result = new ArrayList<>();
        for (Incident incident : incidents) {
            result.add(incident.getId());
        }
        return result;
    }

    private ConditionGroup getSearchCondition(String uniqueId, String name, int ageFrom,
                                              int ageTo, Date date) {
        ConditionGroup conditionGroup = ConditionGroup.clause();
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_UNIQUE_ID).build())
                .like(getWrappedCondition(uniqueId)));
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_NAME).build())
                .like(getWrappedCondition(name)));
        conditionGroup.and(Condition.column(NameAlias.builder(RecordModel.COLUMN_AGE).build())
                .between(ageFrom).and(ageTo));

        if (date != null) {
            conditionGroup.and(Condition.column(
                    NameAlias.builder(RecordModel.COLUMN_REGISTRATION_DATE).build()).eq(date));
        }
        return conditionGroup;
    }

    public Incident saveOrUpdate(ItemValues itemValues) throws IOException {

        if (itemValues.getAsString(INCIDENT_ID) == null) {
            return save(itemValues);
        } else {
            Log.d(TAG, "update the existing incident");
            return update(itemValues);
        }
    }

    public Incident save(ItemValues itemValues) throws IOException {
        String uniqueId = createUniqueId();
        itemValues.addStringItem(INCIDENT_DISPLAY_ID, getShortUUID(uniqueId));
        itemValues.addStringItem(INCIDENT_ID, uniqueId);
        String username = PrimeroConfiguration.getCurrentUser().getUsername();
        itemValues.addStringItem(MODULE, "primeromodule-gbv");
        itemValues.addStringItem(CASEWORKER_CODE, username);
        itemValues.addStringItem(RECORD_CREATED_BY, username);
        itemValues.addStringItem(PREVIOUS_OWNER, username);

        if (!itemValues.has(INQUIRY_DATE)) {
            itemValues.addStringItem(INQUIRY_DATE, getCurrentRegistrationDateAsString());
        }

        Gson gson = new Gson();
        Date date = new Date(Calendar.getInstance().getTimeInMillis());
        Blob tracingBlob = new Blob(gson.toJson(itemValues.getValues()).getBytes());
        Blob audioFileDefault = null;
        audioFileDefault = getAudioBlob(audioFileDefault);

        Incident incident = new Incident();
        incident.setUniqueId(uniqueId);
        incident.setCreateDate(date);
        incident.setLastUpdatedDate(date);
        incident.setContent(tracingBlob);
        incident.setName(getName(itemValues));
        int age = itemValues.getAsInt(RELATION_AGE) != null ? itemValues.getAsInt(RELATION_AGE) : 0;
        incident.setAge(age);
        incident.setCaregiver(getCaregiverName(itemValues));
        incident.setRegistrationDate(getRegisterDate(itemValues.getAsString(INQUIRY_DATE)));
        incident.setAudio(audioFileDefault);
        incident.setCreatedBy(username);
        incident.save();
        return incident;
    }


    public Incident update(ItemValues itemValues) throws IOException {
        Gson gson = new Gson();
        Blob blob = new Blob(gson.toJson(itemValues.getValues()).getBytes());
        Blob audioFileDefault = null;
        audioFileDefault = getAudioBlob(audioFileDefault);

        Incident incident = incidentDao.getIncidentByUniqueId(itemValues.getAsString(INCIDENT_ID));
        incident.setLastUpdatedDate(new Date(Calendar.getInstance().getTimeInMillis()));
        incident.setContent(blob);
        incident.setName(getName(itemValues));
        int age = itemValues.getAsInt(AGE) != null ? itemValues.getAsInt(AGE) : 0;
        incident.setAge(age);
        incident.setCaregiver(getCaregiverName(itemValues));
        setSyncedStatus(incident);
        incident.setRegistrationDate(getRegisterDate(itemValues.getAsString(INQUIRY_DATE)));
        incident.setAudio(audioFileDefault);
        incident.update();

        return incident;
    }

    private Blob getAudioBlob(Blob blob) {
        if (StreamUtil.isFileExists(AUDIO_FILE_PATH)) {
            try {
                blob = new Blob(StreamUtil.readFile(AUDIO_FILE_PATH));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return blob;
    }

    private String getName(ItemValues values) {
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