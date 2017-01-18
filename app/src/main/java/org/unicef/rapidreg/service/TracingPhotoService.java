package org.unicef.rapidreg.service;

import org.unicef.rapidreg.repository.TracingPhotoDao;
import org.unicef.rapidreg.repository.impl.TracingPhotoDaoImpl;
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

    public TracingPhoto getFirst(long tracingId) {
        return tracingPhotoDao.getFirst(tracingId);
    }

    public TracingPhoto getById(long id) {
        return tracingPhotoDao.getById(id);
    }

    public List<Long> getIdsByTracingId(long tracingId) {
        return tracingPhotoDao.getIdsByTracingId(tracingId);
    }

    public boolean hasUnSynced(long tracingId) {
        return tracingPhotoDao.countUnSynced(tracingId) > 0;
    }

    public void deleteByTracingId(long tracingId) {
        tracingPhotoDao.deleteByTracingId(tracingId);
    }
}
