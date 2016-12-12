package org.unicef.rapidreg.db;

import org.unicef.rapidreg.model.IncidentPhoto;

import java.util.List;

public interface IncidentPhotoDao {
    IncidentPhoto getFirst(long incidentId);

    List<Long> getIdsByIncidentId(long incidentId);

    IncidentPhoto getByIncidentIdAndOrder(long incidentId, int order);

    IncidentPhoto getById(long id);

    long countUnSynced(long incidentId);

    void deleteByIncidentId(long incidentId);
}
