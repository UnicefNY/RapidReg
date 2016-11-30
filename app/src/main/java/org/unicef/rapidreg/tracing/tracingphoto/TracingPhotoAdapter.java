package org.unicef.rapidreg.tracing.tracingphoto;

import android.content.Context;

import org.unicef.rapidreg.base.RecordPhotoAdapter;
import org.unicef.rapidreg.model.RecordPhoto;
import org.unicef.rapidreg.service.TracingPhotoService;

import java.util.List;

public class TracingPhotoAdapter extends RecordPhotoAdapter {

    public TracingPhotoAdapter(Context context, List<String> paths) {
        super(context, paths);
    }

    @Override
    protected RecordPhoto getPhotoById(long id) {
        return TracingPhotoService.getInstance().getById(id);
    }
}
