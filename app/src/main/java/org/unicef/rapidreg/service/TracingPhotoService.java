package org.unicef.rapidreg.service;

import org.unicef.rapidreg.db.TracingPhotoDao;
import org.unicef.rapidreg.db.impl.TracingPhotoDaoImpl;
import org.unicef.rapidreg.model.TracingPhoto;

import java.util.List;

public class TracingPhotoService {
    public static final String TAG = TracingPhotoService.class.getSimpleName();

    private static final TracingPhotoService TRACING_PHOTO_SERVICE
            = new TracingPhotoService(new TracingPhotoDaoImpl());

    private TracingPhotoDao tracingPhotoDao;

    public static TracingPhotoService getInstance() {
        return TRACING_PHOTO_SERVICE;
    }

    public TracingPhotoService(TracingPhotoDao tracingDao) {
        this.tracingPhotoDao = tracingDao;
    }

    public TracingPhoto getFirstThumbnail(long tracingId) {
        return tracingPhotoDao.getFirstThumbnail(tracingId);
    }

    public TracingPhoto getById(long tracingId) {
        return tracingPhotoDao.getById(tracingId);
    }

    public List<TracingPhoto> getByTracingId(long tracingId) {
        return tracingPhotoDao.getByTracingId(tracingId);
    }
}
