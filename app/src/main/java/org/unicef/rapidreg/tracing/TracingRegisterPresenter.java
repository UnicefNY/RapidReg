package org.unicef.rapidreg.tracing;

import org.unicef.rapidreg.base.RecordRegisterPresenter;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.service.cache.ItemValues;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

public class TracingRegisterPresenter extends RecordRegisterPresenter {

    private TracingService tracingService;

    @Inject
    public TracingRegisterPresenter(TracingService tracingService) {
        this.tracingService = tracingService;
    }

    public Tracing saveTracing(ItemValues itemValues, ArrayList<String> photoPaths) throws IOException {
        return tracingService.saveOrUpdate(itemValues, photoPaths);
    }

    public Tracing getById(long recordId) {
        return tracingService.getById(recordId);
    }
}
