package org.unicef.rapidreg.repository;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import org.unicef.rapidreg.model.Tracing;

import java.util.List;

public interface TracingDao {

    Tracing save(Tracing tracing);

    Tracing update(Tracing tracing);

    Tracing getTracingByUniqueId(String id);

    List<Tracing> getAllTracingsOrderByDate(boolean isASC, String ownedBy);

    List<Tracing> getAllTracingsByConditionGroup(ConditionGroup conditionGroup);

    Tracing getTracingById(long tracingId);

    Tracing getByInternalId(String id);

    List<Long> getAllIds(String ownedBy);
}
