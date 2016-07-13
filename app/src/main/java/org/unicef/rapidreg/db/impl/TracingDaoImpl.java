package org.unicef.rapidreg.db.impl;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.db.TracingDao;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.model.Tracing_Table;

import java.util.List;

public class TracingDaoImpl implements TracingDao {
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

    private List<Tracing> getTracingsByDateASC() {
        return SQLite.select().from(Tracing.class)
                .orderBy(Tracing_Table.registration_date, true).queryList();
    }

    private List<Tracing> getTracingsByDateDES() {
        return SQLite.select().from(Tracing.class)
                .orderBy(Tracing_Table.registration_date, false).queryList();
    }
}
