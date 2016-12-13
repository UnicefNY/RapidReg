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
import org.unicef.rapidreg.service.RecordService;
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

public class IncidentRegisterPresenter extends RecordRegisterPresenter {

    private static final String TAG = IncidentRegisterPresenter.class.getSimpleName();

    private IncidentService incidentService;
    private IncidentFormService incidentFormService;

    @Inject
    public IncidentRegisterPresenter(IncidentService incidentService, IncidentFormService
            incidentFormService) {
        this.incidentService = incidentService;
        this.incidentFormService = incidentFormService;
    }

    @Override
    protected Long getRecordId(Bundle bundle) {
        return bundle.getLong(IncidentService.INCIDENT_PRIMARY_ID, RecordRegisterFragment
                .INVALID_RECORD_ID);
    }

    @Override
    protected ItemValuesMap getItemValuesByRecordId(Long recordId) throws JSONException {
        Incident incidentItem = incidentService.getById(recordId);
        String incidentJson = new String(incidentItem.getContent().getBlob());
        ItemValuesMap itemValues = new ItemValuesMap(JsonUtils.toMap(ItemValues
                .generateItemValues(incidentJson)
                .getValues()));
        itemValues.addStringItem(IncidentService.INCIDENT_ID, incidentItem.getUniqueId());

        DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        String shortUUID = RecordService.getShortUUID(incidentItem.getUniqueId());
        itemValues.addStringItem(ItemValues.RecordProfile.ID_NORMAL_STATE, shortUUID);
        itemValues.addStringItem(ItemValues.RecordProfile.REGISTRATION_DATE,
                dateFormat.format(incidentItem.getRegistrationDate()));
        itemValues.addNumberItem(ItemValues.RecordProfile.ID, incidentItem.getId());

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

    @Override
    public void saveRecord(RecordRegisterView.SaveRecordCallback callback) {
        ItemValuesMap itemValues = getView().getRecordRegisterData();
        if (!validateRequiredField(itemValues)) {
            callback.onRequiredFieldNotFilled();
            return;
        }
        if (!isViewAttached()) {
            return;
        }
        ItemValues newItemValues = new ItemValues(new Gson().fromJson(new Gson().toJson(
                itemValues.getValues()), JsonObject.class));
        clearProfileItems(newItemValues);
        try {
            Incident record = incidentService.saveOrUpdate(newItemValues);
            callback.onSaveSuccessful(record.getId());
        } catch (IOException e) {
            callback.onSavedFail();
        }
    }

    private boolean validateRequiredField(ItemValuesMap itemValuesMap) {
        IncidentTemplateForm incidentForm = incidentFormService.getGBVTemplate();
        return RecordService.validateRequiredFields(incidentForm, itemValuesMap);
    }

}
