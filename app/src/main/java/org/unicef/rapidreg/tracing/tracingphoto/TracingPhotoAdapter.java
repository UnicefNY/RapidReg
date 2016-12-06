package org.unicef.rapidreg.tracing.tracingphoto;

import android.content.Context;

import org.unicef.rapidreg.base.record.recordphoto.RecordPhotoAdapter;
import org.unicef.rapidreg.injection.ActivityContext;
import org.unicef.rapidreg.model.RecordPhoto;
import org.unicef.rapidreg.service.TracingPhotoService;

import java.util.List;

import javax.inject.Inject;

public class TracingPhotoAdapter extends RecordPhotoAdapter {

    @Inject
    TracingPhotoService tracingPhotoService;

    @Inject
    public TracingPhotoAdapter(@ActivityContext Context context) {
        super(context);
    }

    @Override
    protected RecordPhoto getPhotoById(long id) {
        return tracingPhotoService.getById(id);
    }
}
