package org.unicef.rapidreg.tracing.tracingregister;

import org.unicef.rapidreg.base.record.recordregister.RecordRegisterPresenter;
import org.unicef.rapidreg.forms.TracingFormRoot;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.service.cache.ItemValues;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

public class TracingRegisterPresenter extends RecordRegisterPresenter {

    private TracingService tracingService;
    private TracingFormService tracingFormService;

    @Inject
    public TracingRegisterPresenter(TracingService tracingService, TracingFormService tracingFormService) {
        this.tracingService = tracingService;
        this.tracingFormService = tracingFormService;
    }

    public Tracing saveTracing(ItemValues itemValues, ArrayList<String> photoPaths) throws IOException {
        return tracingService.saveOrUpdate(itemValues, photoPaths);
    }

    public Tracing getById(long recordId) {
        return tracingService.getById(recordId);
    }

    @Override
    public TracingFormRoot getCurrentForm() {
        return tracingFormService.getCurrentForm();
    }
}
