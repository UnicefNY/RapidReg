package org.unicef.rapidreg.incident.incidentregister;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterFragment;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterPresenter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterView;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.service.IncidentFormService;
import org.unicef.rapidreg.service.IncidentService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class IncidentRegisterPresenter extends RecordRegisterPresenter {

    private static final String TAG = IncidentRegisterPresenter.class.getSimpleName();

    private IncidentService incidentService;
    private IncidentFormService incidentFormService;

    @Inject
    public IncidentRegisterPresenter(IncidentService incidentService, IncidentFormService
            incidentFormService) {
        super(incidentService);
        this.incidentService = incidentService;
        this.incidentFormService = incidentFormService;
    }

    @Override
    protected Long getRecordId(Bundle bundle) {
        return bundle.getLong(IncidentService.INCIDENT_PRIMARY_ID, RecordRegisterFragment
                .INVALID_RECORD_ID);
    }

    @Override
    public void saveRecord(ItemValuesMap itemValuesMap, List<String> photoPaths,
                           RecordRegisterView.SaveRecordCallback callback) {

        IncidentTemplateForm incidentForm = incidentFormService.getGBVTemplate();
        boolean validateRequiredFields = incidentService.validateRequiredFields(incidentForm,
                itemValuesMap);

        if (!validateRequiredFields) {
            callback.onRequiredFieldNotFilled();
            return;
        }
        clearProfileItems(itemValuesMap);
        try {
            Incident record = incidentService.saveOrUpdate(itemValuesMap);
            addProfileItems(itemValuesMap, record.getRegistrationDate(), record.getUniqueId(),
                    record.getId());
            callback.onSaveSuccessful(record.getId());
        } catch (IOException e) {
            callback.onSavedFail();
        }
    }

    @Override
    protected ItemValuesMap getItemValuesByRecordId(Long recordId) throws JSONException {
        Incident incidentItem = incidentService.getById(recordId);
        String incidentJson = new String(incidentItem.getContent().getBlob());
        final ItemValuesMap itemValues = new ItemValuesMap(JsonUtils.toMap(new Gson().fromJson
                (incidentJson, JsonObject.class)));
        itemValues.addStringItem(IncidentService.INCIDENT_ID, incidentItem.getUniqueId());

        addProfileItems(itemValues, incidentItem.getRegistrationDate(), incidentItem.getUniqueId
                (), recordId);

        return itemValues;
    }

    @Override
    protected List<String> getPhotoPathsByRecordId(Long recordId) {
        return null;
    }

    @Override
    protected List<Field> getFields() {
        List<Field> fields = new ArrayList<>();

        RecordForm form = incidentFormService.getGBVTemplate();

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
        RecordForm form = incidentFormService.getGBVTemplate();
        if (form != null) {
            return form.getSections().get(position).getFields();
        }
        return null;
    }

    @Override
    public RecordForm getTemplateForm() {
        return incidentFormService.getGBVTemplate();
    }

    private boolean validateRequiredField(ItemValuesMap itemValuesMap) {
        IncidentTemplateForm incidentForm = incidentFormService.getGBVTemplate();
        return incidentService.validateRequiredFields(incidentForm, itemValuesMap);
    }

}
