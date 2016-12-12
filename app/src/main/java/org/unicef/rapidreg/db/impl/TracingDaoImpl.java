package org.unicef.rapidreg.db.impl;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.list.FlowQueryList;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.db.TracingDao;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.model.Tracing_Table;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.utils.StreamUtil;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static org.unicef.rapidreg.service.RecordService.*;

public class TracingDaoImpl implements TracingDao {

    public static final String TRACING_DISPLAY_ID = "tracing_request_id_display";
    public static final String TRACING_ID = "tracing_request_id";
    public static final String TRACING_PRIMARY_ID = "tracing_primary_id";

    @Override
    public Tracing save(ItemValues itemValues) {
        String uniqueId = createUniqueId();
        itemValues.addStringItem(TRACING_DISPLAY_ID, getShortUUID(uniqueId));
        itemValues.addStringItem(TRACING_ID, uniqueId);
        String username = PrimeroConfiguration.getCurrentUser().getUsername();
        itemValues.addStringItem(MODULE, "primeromodule-cp");
        itemValues.addStringItem(CASEWORKER_CODE, username);
        itemValues.addStringItem(RECORD_CREATED_BY, username);
        itemValues.addStringItem(PREVIOUS_OWNER, username);

        if (!itemValues.has(INQUIRY_DATE)) {
            itemValues.addStringItem(INQUIRY_DATE, getCurrentRegistrationDateAsString());
        }

        Gson gson = new Gson();
        Date date = new Date(Calendar.getInstance().getTimeInMillis());
        Blob tracingBlob = new Blob(gson.toJson(itemValues.getValues()).getBytes());
        Blob audioFileDefault = getAudioBlob();

        Tracing tracing = new Tracing();
        tracing.setUniqueId(uniqueId);
        tracing.setCreateDate(date);
        tracing.setLastUpdatedDate(date);
        tracing.setContent(tracingBlob);
        tracing.setName(getName(itemValues));
        int age = itemValues.getAsInt(RELATION_AGE) != null ? itemValues.getAsInt(RELATION_AGE) : 0;
        tracing.setAge(age);
        tracing.setCaregiver(getCaregiverName(itemValues));
        tracing.setRegistrationDate(getRegisterDate(itemValues.getAsString(INQUIRY_DATE)));
        tracing.setAudio(audioFileDefault);
        tracing.setCreatedBy(username);
        tracing.save();

        return tracing;
    }

    @Override
    public Tracing update(ItemValues itemValues) {
        return null;
    }

    @Override
    public Tracing delete() {
        return null;
    }

    @Override
    public Tracing getTracingByUniqueId(String uniqueId) {
        return SQLite.select().from(Tracing.class)
                .where(Tracing_Table.unique_id.eq(uniqueId))
                .querySingle();
    }

    @Override
    public List<Tracing> getAllTracingsOrderByDate(boolean isASC) {
        return isASC ? getTracingsByDateASC() : getTracingsByDateDES();
    }

    @Override
    public List<Tracing> getAllTracingsByConditionGroup(ConditionGroup conditionGroup) {
        return SQLite.select().from(Tracing.class)
                .where(conditionGroup)
                .orderBy(Tracing_Table.registration_date, false)
                .queryList();
    }

    @Override
    public Tracing getTracingById(long tracingId) {
        return SQLite.select().from(Tracing.class).where(Tracing_Table.id.eq(tracingId)).querySingle();
    }

    @Override
    public Tracing getByInternalId(String id) {
        return SQLite.select().from(Tracing.class).where(Tracing_Table._id.eq(id)).querySingle();
    }

    @Override
    public List<Long> getAllIds() {
        List<Long> result = new ArrayList<>();
        FlowQueryList<Tracing> cases = SQLite.select().from(Tracing.class).flowQueryList();
        for (Tracing tracing : cases) {
            result.add(tracing.getId());
        }
        return result;
    }

    private   String createUniqueId() {
        return UUID.randomUUID().toString();
    }

    private String getCurrentRegistrationDateAsString() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
    }

    private String getCaregiverName(ItemValues itemValues) {
        return "" + itemValues.getAsString(CAREGIVER_NAME);
    }

    private List<Tracing> getTracingsByDateASC() {
        return SQLite.select().from(Tracing.class)
                .orderBy(Tracing_Table.registration_date, true).queryList();
    }

    private List<Tracing> getTracingsByDateDES() {
        return SQLite.select().from(Tracing.class)
                .orderBy(Tracing_Table.registration_date, false).queryList();
    }

    private Blob getAudioBlob() {
        if (StreamUtil.isFileExists(AUDIO_FILE_PATH)) {
            try {
                return new Blob(StreamUtil.readFile(AUDIO_FILE_PATH));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getName(ItemValues values) {
        return values.getAsString(RELATION_NAME) + " "
                + values.getAsString(RELATION_AGE) + " "
                + values.getAsString(RELATION_NICKNAME);
    }
}
