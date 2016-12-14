package org.unicef.rapidreg.childcase.caseregister;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterFragment;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterPresenter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterView;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterView.SaveRecordCallback;
import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;
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

import static org.unicef.rapidreg.service.RecordService.MODULE;


public class CaseRegisterPresenter extends RecordRegisterPresenter {
    private static final String TAG = CaseRegisterPresenter.class.getSimpleName();

    public static final String MODULE_CASE_CP = "primeromodule-cp";
    public static final String MODULE_CASE_GBV = "primeromodule-gbv";

    private CaseService caseService;
    private CaseFormService caseFormService;
    private CasePhotoService casePhotoService;

    private String caseType = "";

    @Inject
    public CaseRegisterPresenter(CaseService caseService, CaseFormService caseFormService, CasePhotoService
            casePhotoService) {
        this.caseService = caseService;
        this.caseFormService = caseFormService;
        this.casePhotoService = casePhotoService;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getCaseType() {
        return caseType;
    }

    @Override
    protected Long getRecordId(Bundle bundle) {
        return bundle.getLong(CaseService.CASE_PRIMARY_ID, RecordRegisterFragment.INVALID_RECORD_ID);
    }

    @Override
    public void saveRecord(ItemValuesMap itemValuesMap, List<String> photoPaths, SaveRecordCallback callback) {
        if (!validateRequiredField(itemValuesMap)) {
            callback.onRequiredFieldNotFilled();
            return;
        }
        ItemValues newItemValues = new ItemValues(new Gson().fromJson(new Gson().toJson(
                itemValuesMap.getValues()), JsonObject.class));
        newItemValues.addStringItem(MODULE, caseType);
        clearProfileItems(newItemValues);
        try {
            Case record = caseService.saveOrUpdate(newItemValues, photoPaths);
            callback.onSaveSuccessful(record.getId());
        } catch (IOException e) {
            callback.onSavedFail();
        }
    }

    @Override
    protected ItemValuesMap getItemValuesByRecordId(Long recordId) throws JSONException {
        Case caseItem = caseService.getById(recordId);
        String caseJson = new String(caseItem.getContent().getBlob());
        ItemValuesMap itemValues = new ItemValuesMap(JsonUtils.toMap(ItemValues.generateItemValues(caseJson)
                .getValues()));
        itemValues.addStringItem(CaseService.CASE_ID, caseItem.getUniqueId());

        DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        String shortUUID = RecordService.getShortUUID(caseItem.getUniqueId());
        itemValues.addStringItem(ItemValues.RecordProfile.ID_NORMAL_STATE, shortUUID);
        itemValues.addStringItem(ItemValues.RecordProfile.REGISTRATION_DATE,
                dateFormat.format(caseItem.getRegistrationDate()));
        itemValues.addNumberItem(ItemValues.RecordProfile.ID, caseItem.getId());

        return itemValues;
    }

    @Override
    protected List<String> getPhotoPathsByRecordId(Long recordId) {
        List<String> paths = new ArrayList<>();
        for (Long casePhotoId : casePhotoService.getIdsByCaseId(recordId)) {
            paths.add(String.valueOf(casePhotoId));
        }
        return paths;
    }

    @Override
    protected List<Field> getFields() {
        List<Field> fields = new ArrayList<>();

        RecordForm form = getTemplateForm();

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
        RecordForm form = getTemplateForm();
        if (form != null) {
            return form.getSections().get(position).getFields();
        }
        return null;
    }

    @Override
    public RecordForm getTemplateForm() {
        switch (caseType) {
            case MODULE_CASE_CP: return caseFormService.getCPTemplate();
            case MODULE_CASE_GBV: return caseFormService.getGBVTemplate();
            default: return new CaseTemplateForm();
        }
    }

    private boolean validateRequiredField(ItemValuesMap itemValuesMap) {
        return RecordService.validateRequiredFields(getTemplateForm(), itemValuesMap);
    }
}
