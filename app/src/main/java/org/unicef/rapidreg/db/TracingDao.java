package org.unicef.rapidreg.db;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import org.unicef.rapidreg.model.Tracing;

import java.util.List;

public interface TracingDao {
    Tracing getTracingByUniqueId(String id);

    List<Tracing> getAllTracingsOrderByDate(boolean isASC);

    List<Tracing> getAllTracingsByConditionGroup(ConditionGroup conditionGroup);

    Tracing getTracingById(long tracingId);

    Tracing getByInternalId(String id);

    List<Long> getAllIds();
}
