package org.unicef.rapidreg.service.impl;

import org.unicef.rapidreg.db.CasePhotoDao;
import org.unicef.rapidreg.db.IncidentPhotoDao;
import org.unicef.rapidreg.db.impl.CasePhotoDaoImpl;
import org.unicef.rapidreg.db.impl.IncidentPhotoDaoImpl;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.model.IncidentPhoto;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.IncidentPhotoService;

import java.util.List;

public class IncidentPhotoServiceImpl implements IncidentPhotoService {

    public static final String TAG = IncidentPhotoService.class.getSimpleName();

    private static final IncidentPhotoServiceImpl INCIDENT_SERVICE
            = new IncidentPhotoServiceImpl(new IncidentPhotoDaoImpl());

    private IncidentPhotoDao incidentPhotoDao;

    public static IncidentPhotoService getInstance() {
        return INCIDENT_SERVICE;
    }

    public IncidentPhotoServiceImpl(IncidentPhotoDao incidentDao) {
        this.incidentPhotoDao = incidentDao;
    }

    public IncidentPhoto getFirst(long incidentId) {
        return incidentPhotoDao.getFirst(incidentId);
    }

    public IncidentPhoto getById(long id) {
        return incidentPhotoDao.getById(id);
    }

    public List<Long> getIdsByIncidentId(long incidentId) {
        return incidentPhotoDao.getIdsByIncidentId(incidentId);
    }

    public boolean hasUnSynced(long incidentId) {
        return incidentPhotoDao.countUnSynced(incidentId) > 0;
    }

    public void deleteByIncidentId(long incidentId) {
        incidentPhotoDao.deleteByIncidentId(incidentId);
    }

}
