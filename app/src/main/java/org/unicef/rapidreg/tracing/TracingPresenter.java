package org.unicef.rapidreg.tracing;

import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.base.record.RecordPresenter;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.model.TracingForm;
import org.unicef.rapidreg.service.FormRemoteService;
import org.unicef.rapidreg.service.TracingFormService;

import javax.inject.Inject;

public class TracingPresenter extends RecordPresenter {
    private TracingFormService tracingFormService;

    @Inject
    public TracingPresenter(TracingFormService tracingFormService) {
        this.tracingFormService = tracingFormService;
    }

    @Override
    public void saveForm(RecordForm recordForm, String moduleId) {
        TracingForm tracingForm = new TracingForm(new Blob(gson.toJson(recordForm).getBytes()));
        tracingFormService.saveOrUpdate(tracingForm);
    }
}
