package org.unicef.rapidreg.service;

import org.unicef.rapidreg.model.IncidentPhoto;

import java.util.List;

public interface IncidentPhotoService {

    public IncidentPhoto getFirst(long caseId);

    public IncidentPhoto getById(long id);

    public List<Long> getIdsByIncidentId(long incidentId);

    public boolean hasUnSynced(long incidentId);

    public void deleteByIncidentId(long incidentId);

}
