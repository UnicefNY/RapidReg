package org.unicef.rapidreg.db.impl;

import com.raizlabs.android.dbflow.list.FlowQueryList;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.db.TracingDao;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.model.Tracing_Table;

import java.util.ArrayList;
import java.util.List;

public class TracingDaoImpl implements TracingDao {

    public static final String TRACING_DISPLAY_ID = "tracing_request_id_display";
    public static final String TRACING_ID = "tracing_request_id";
    public static final String TRACING_PRIMARY_ID = "tracing_primary_id";

    @Override
    public Tracing save(Tracing tracing) {
        tracing.save();

        return tracing;
    }

    @Override
    public Tracing update(Tracing tracing) {
        tracing.update();

        return tracing;
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

    private List<Tracing> getTracingsByDateASC() {
        return SQLite.select().from(Tracing.class)
                .orderBy(Tracing_Table.registration_date, true).queryList();
    }

    private List<Tracing> getTracingsByDateDES() {
        return SQLite.select().from(Tracing.class)
                .orderBy(Tracing_Table.registration_date, false).queryList();
    }
}
