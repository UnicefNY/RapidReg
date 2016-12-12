package org.unicef.rapidreg.incident.incidentphoto;

import android.content.Context;

import org.unicef.rapidreg.base.record.recordphoto.RecordPhotoAdapter;
import org.unicef.rapidreg.injection.ActivityContext;
import org.unicef.rapidreg.model.RecordPhoto;
import org.unicef.rapidreg.service.IncidentPhotoService;

import javax.inject.Inject;

public class IncidentPhotoAdapter extends RecordPhotoAdapter {
    @Inject
    IncidentPhotoService incidentPhotoService;

    @Inject
    public IncidentPhotoAdapter(@ActivityContext Context context) {
        super(context);
    }

    @Override
    protected RecordPhoto getPhotoById(long id) {
        return incidentPhotoService.getById(id);
    }
}
