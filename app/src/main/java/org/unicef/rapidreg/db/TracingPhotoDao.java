package org.unicef.rapidreg.db;

import org.unicef.rapidreg.model.TracingPhoto;

import java.util.List;

public interface TracingPhotoDao {
    TracingPhoto getFirstThumbnail(long tracingId);

    List<TracingPhoto> getByTracingId(long tracingId);

    TracingPhoto getByTracingIdAndOrder(long tracingId, int order);

    TracingPhoto getById(long id);
}
