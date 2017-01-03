package org.unicef.rapidreg.tracing.tracingregister;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterFragment;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterPresenter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterView.SaveRecordCallback;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.forms.TracingTemplateForm;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.TracingPhotoService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import static org.unicef.rapidreg.service.TracingService.TRACING_ID;
import static org.unicef.rapidreg.service.TracingService.TRACING_PRIMARY_ID;

public class TracingRegisterPresenter extends RecordRegisterPresenter {

    private static final String TAG = TracingRegisterPresenter.class.getSimpleName();

    private TracingService tracingService;
    private TracingFormService tracingFormService;
    private TracingPhotoService tracingPhotoService;

    @Inject
    public TracingRegisterPresenter(TracingService tracingService, TracingFormService
            tracingFormService, TracingPhotoService tracingPhotoService) {
        super(tracingService);
        this.tracingService = tracingService;
        this.tracingFormService = tracingFormService;
        this.tracingPhotoService = tracingPhotoService;
    }

    @Override
    protected Long getRecordId(Bundle bundle) {
        return bundle.getLong(TRACING_PRIMARY_ID, RecordRegisterFragment.INVALID_RECORD_ID);
    }

    @Override
    public void saveRecord(ItemValuesMap itemValuesMap, List<String> photoPaths,
                           SaveRecordCallback callback) {
        if (!validateRequiredField(itemValuesMap)) {
            callback.onRequiredFieldNotFilled();
            return;
        }

        clearProfileItems(itemValuesMap);
        try {
            Tracing record = tracingService.saveOrUpdate(itemValuesMap, photoPaths);
            addProfileItems(itemValuesMap, record.getRegistrationDate(), record.getUniqueId(),
                    record.getId());
            callback.onSaveSuccessful(record.getId());
        } catch (IOException e) {
            callback.onSavedFail();
        }
    }

    @Override
    protected ItemValuesMap getItemValuesByRecordId(Long recordId) throws JSONException {
        Tracing tracingItem = tracingService.getById(recordId);
        String tracingJson = new String(tracingItem.getContent().getBlob());
        final ItemValuesMap itemValues = new ItemValuesMap(JsonUtils.toMap(new Gson().fromJson(tracingJson, JsonObject.class)));
        itemValues.addStringItem(TRACING_ID, tracingItem.getUniqueId());
        addProfileItems(itemValues, tracingItem.getRegistrationDate(), tracingItem.getUniqueId(),
                tracingItem.getId());

        return itemValues;
    }

    @Override
    protected List<String> getPhotoPathsByRecordId(Long recordId) {
        List<String> paths = new ArrayList<>();
        List<Long> tracings = tracingPhotoService.getIdsByTracingId(recordId);
        for (Long tracingId : tracings) {
            paths.add(String.valueOf(tracingId));
        }
        return paths;
    }

    @Override
    protected List<Field> getFields() {
        List<Field> fields = new ArrayList<>();

        RecordForm form = tracingFormService.getCPTemplate();
        List<Section> sections = form.getSections();

        for (Section section : sections) {
            for (Field field : section.getFields()) {
                if (field.isShowOnMiniForm()) {
                    if (field.isPhotoUploadBox()) {
                        fields.add(0, field);
                    } else {
                        fields.add(field);
                    }
                }
            }
        }

        return fields;
    }

    @Override
    public List<Field> getFields(int position) {
        RecordForm form = tracingFormService.getCPTemplate();
        if (form != null) {
            return form.getSections().get(position).getFields();
        }
        return null;
    }

    @Override
    public RecordForm getTemplateForm() {
        return tracingFormService.getCPTemplate();
    }

    private boolean validateRequiredField(ItemValuesMap itemValuesMap) {
        TracingTemplateForm recordForm = tracingFormService.getCPTemplate();
        return tracingService.validateRequiredFields(recordForm, itemValuesMap);
    }
}
