package org.unicef.rapidreg.db;

import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.model.TracingPhoto;

import java.io.IOException;
import java.util.List;

public interface TracingPhotoDao {
    TracingPhoto getFirst(long tracingId);

    List<Long> getIdsByTracingId(long tracingId);

    TracingPhoto getByTracingIdAndOrder(long tracingId, int order);

    TracingPhoto getById(long id);

    long countUnSynced(long tracingId);

    void deleteByTracingId(long tracingId);

    Tracing save(Tracing tracing, List<String> photoPaths) throws IOException;

    Tracing update(Tracing update, List<String> photoPaths) throws IOException;
}
