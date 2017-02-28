package org.unicef.rapidreg.repository;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import org.unicef.rapidreg.model.Tracing;

import java.util.List;

public interface TracingDao {
    Tracing save(Tracing tracing);

    Tracing update(Tracing tracing);

    Tracing getTracingByUniqueId(String id);

    List<Tracing> getAllTracingsOrderByDate(boolean isASC, String ownedBy, String url);

    List<Tracing> getAllTracingsByConditionGroup(String ownedBy, String url, ConditionGroup conditionGroup);

    Tracing getTracingById(long tracingId);

    Tracing getByInternalId(String id);

    Tracing deleteByRecordId(long recordId);

    Tracing delete(Tracing deleteTracing);

    List<Tracing> getALLSyncedRecords();
}
