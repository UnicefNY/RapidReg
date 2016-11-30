package org.unicef.rapidreg.tracing.tracingregister;

import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterPresenter;
import org.unicef.rapidreg.forms.TracingFormRoot;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

public class TracingRegisterPresenter extends RecordRegisterPresenter {

    private TracingService tracingService;
    private TracingFormService tracingFormService;

    @Inject
    public TracingRegisterPresenter(TracingService tracingService, TracingFormService tracingFormService) {
        this.tracingService = tracingService;
        this.tracingFormService = tracingFormService;
    }

    public Tracing getById(long recordId) {
        return tracingService.getById(recordId);
    }

    @Override
    public TracingFormRoot getCurrentForm() {
        return tracingFormService.getCurrentForm();
    }

    @Override
    public void saveRecord(ItemValuesMap itemValuesMap) {
        if (!validateRequiredField(itemValuesMap)) {
            getView().promoteRequiredFieldNotFilled();
            return;
        }
        if (!isViewAttached()) {
            return;
        }
        clearProfileItems(itemValuesMap);
        List<String> photoPaths = getView().getPhotos();
        ItemValues itemValues = new ItemValues(new Gson().fromJson(new Gson().toJson(
                itemValuesMap.getValues()), JsonObject.class));

        try {
            Tracing record = tracingService.saveOrUpdate(itemValues, photoPaths);
            getView().saveSuccessfully(record.getId());
        } catch (IOException e) {
            getView().promoteSaveFail();
        }

    }

    private boolean validateRequiredField(ItemValuesMap itemValuesMap) {
        TracingFormRoot recordForm = getCurrentForm();
        return RecordService.validateRequiredFields(recordForm, itemValuesMap);
    }
}
