package org.unicef.rapidreg.childcase.casephoto;

import android.content.Context;

import org.unicef.rapidreg.base.record.recordphoto.RecordPhotoAdapter;
import org.unicef.rapidreg.injection.ActivityContext;
import org.unicef.rapidreg.model.RecordPhoto;
import org.unicef.rapidreg.service.CasePhotoService;

import javax.inject.Inject;

public class CasePhotoAdapter extends RecordPhotoAdapter {

    @Inject
    CasePhotoService casePhotoService;

    @Inject
    public CasePhotoAdapter(@ActivityContext Context context) {
        super(context);
    }

    @Override
    protected RecordPhoto getPhotoById(long id) {
        return casePhotoService.getById(id);
    }
}
