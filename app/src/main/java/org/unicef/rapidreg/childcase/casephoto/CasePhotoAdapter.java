package org.unicef.rapidreg.childcase.casephoto;

import android.content.Context;

import org.unicef.rapidreg.base.record.recordphoto.RecordPhotoAdapter;
import org.unicef.rapidreg.model.RecordPhoto;
import org.unicef.rapidreg.service.CasePhotoService;

import java.util.List;

public class CasePhotoAdapter extends RecordPhotoAdapter {

    public CasePhotoAdapter(Context context, List<String> paths) {
        super(context, paths);
    }

    @Override
    protected RecordPhoto getPhotoById(long id) {
        return CasePhotoService.getInstance().getById(id);
    }
}
