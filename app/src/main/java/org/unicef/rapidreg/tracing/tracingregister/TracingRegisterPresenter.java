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
import org.unicef.rapidreg.forms.TracingFormRoot;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.TracingPhotoService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.JsonUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class TracingRegisterPresenter extends RecordRegisterPresenter {

    private static final String TAG = TracingRegisterPresenter.class.getSimpleName();

    private TracingService tracingService;
    private TracingFormService tracingFormService;
    private TracingPhotoService tracingPhotoService;

    @Inject
    public TracingRegisterPresenter(TracingService tracingService, TracingFormService tracingFormService, TracingPhotoService tracingPhotoService) {
        this.tracingService = tracingService;
        this.tracingFormService = tracingFormService;
        this.tracingPhotoService = tracingPhotoService;
    }

    @Override
    public TracingFormRoot getCurrentForm() {
        return tracingFormService.getCurrentForm();
    }

    @Override
    protected Long getRecordId(Bundle bundle) {
        return bundle.getLong(TracingService.TRACING_PRIMARY_ID, RecordRegisterFragment.INVALID_RECORD_ID);
    }

    @Override
    protected ItemValuesMap getItemValuesByRecordId(Long recordId) throws JSONException {
        Tracing tracingItem = tracingService.getById(recordId);
        String tracingJson = new String(tracingItem.getContent().getBlob());

        ItemValuesMap itemValues = new ItemValuesMap(JsonUtils.toMap(ItemValues.generateItemValues(tracingJson).getValues()));
        itemValues.addStringItem(TracingService.TRACING_ID, tracingItem.getUniqueId());

        DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        String shortUUID = RecordService.getShortUUID(tracingItem.getUniqueId());
        itemValues.addStringItem(ItemValues.RecordProfile.ID_NORMAL_STATE, shortUUID);
        itemValues.addStringItem(ItemValues.RecordProfile.REGISTRATION_DATE,
                dateFormat.format(tracingItem.getRegistrationDate()));
        itemValues.addNumberItem(ItemValues.RecordProfile.ID, tracingItem.getId());

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

        RecordForm form = tracingFormService.getCurrentForm();
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
        RecordForm form = getCurrentForm();
        if (form != null) {
            return form.getSections().get(position).getFields();
        }
        return null;
    }

    @Override
    public void saveRecord(SaveRecordCallback callback) {
        ItemValuesMap itemValuesMap = getView().getRecordRegisterData();
        saveRecord(itemValuesMap, callback);

    }

    @Override
    public void saveRecord(ItemValuesMap itemValuesMap, SaveRecordCallback callback) {
        if (!validateRequiredField(itemValuesMap)) {
            callback.onRequiredFieldNotFilled();
            return;
        }
        if (!isViewAttached()) {
            return;
        }
        clearProfileItems(itemValuesMap);
        List<String> photoPaths = getView().getPhotoPathsData();
        ItemValues itemValues = new ItemValues(new Gson().fromJson(new Gson().toJson(
                itemValuesMap.getValues()), JsonObject.class));

        try {
            Tracing record = tracingService.saveOrUpdate(itemValues, photoPaths);
            callback.onSaveSuccessful(record.getId());
        } catch (IOException e) {
            callback.onSavedFail();
        }
    }

    private boolean validateRequiredField(ItemValuesMap itemValuesMap) {
        TracingFormRoot recordForm = getCurrentForm();
        return RecordService.validateRequiredFields(recordForm, itemValuesMap);
    }
}
