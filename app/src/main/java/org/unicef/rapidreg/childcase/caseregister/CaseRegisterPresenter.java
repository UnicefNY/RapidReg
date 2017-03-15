package org.unicef.rapidreg.childcase.caseregister;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONException;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterFragment;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterPresenter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterView.SaveRecordCallback;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.inject.Inject;

import static org.unicef.rapidreg.service.CaseService.CASE_ID;
import static org.unicef.rapidreg.service.RecordService.MODULE;


public class CaseRegisterPresenter extends RecordRegisterPresenter {
    private static final String TAG = CaseRegisterPresenter.class.getSimpleName();

    public static final String MODULE_CASE_CP = "primeromodule-cp";
    public static final String MODULE_CASE_GBV = "primeromodule-gbv";

    public static final String AGE = "age";
    public static final String AGE_SECTION = "Basic Identity";
    public static final String CAREGIVER_AGE = "caregiver_age";
    public static final String VERIFICATION_SUBFORM_SECTION = "verification_subform_section";
    public static final String VERIFICATION_INQUIRER_AGE = "verification_inquirer_age";
    public static final String FAMILY_DETAILS_SECTION = "family_details_section";
    public static final String RELATION_AGE = "relation_age";

    private CaseService caseService;
    private CaseFormService caseFormService;
    private CasePhotoService casePhotoService;

    private String caseType = "";

    @Inject
    public CaseRegisterPresenter(CaseService caseService, CaseFormService caseFormService,
                                 CasePhotoService casePhotoService) {
        super(caseService);
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
        return bundle.getLong(CaseService.CASE_PRIMARY_ID, RecordRegisterFragment
                .INVALID_RECORD_ID);
    }

    @Override
    protected String getUniqueId(Bundle bundle) {
        return bundle.getString(CASE_ID, RecordRegisterFragment.INVALID_UNIQUE_ID);
    }

    @Override
    public void saveRecord(ItemValuesMap itemValuesMap, List<String> photoPaths,
                           SaveRecordCallback callback) {
        ItemValuesMap fieldValueVerifyResult = getView().getFieldValueVerifyResult();
        if (fieldValueVerifyResult != null && fieldValueVerifyResult.getValues().size() > 0) {
            callback.onFieldValueInvalid();
            return;
        }
        if (!validateRequiredField(itemValuesMap)) {
            callback.onRequiredFieldNotFilled();
            return;
        }

        itemValuesMap.addStringItem(MODULE, caseType);
        clearProfileItems(itemValuesMap);
        try {
            Case record = caseService.saveOrUpdate(itemValuesMap, photoPaths);

            List<String> incidentList = caseService.getIncidentsByCaseId(record.getUniqueId());
            addProfileItems(itemValuesMap, record.getRegistrationDate(), record.getUniqueId(),
                    incidentList, record.getId());
            clearImagesCache();
            callback.onSaveSuccessful(record.getId());
        } catch (IOException e) {
            callback.onSavedFail();
        }
    }

    @Override
    protected ItemValuesMap getItemValuesByRecordId(Long recordId) throws JSONException {
        Case caseItem = caseService.getById(recordId);
        String caseJson = new String(caseItem.getContent().getBlob());
        ItemValuesMap itemValues = new ItemValuesMap(JsonUtils.toMap(new Gson().fromJson
                (caseJson, JsonObject.class)));
        itemValues.addStringItem(CASE_ID, caseItem.getUniqueId());

        List<String> incidentList = caseService.getIncidentsByCaseId(caseItem.getUniqueId());
        addProfileItems(itemValues, caseItem.getRegistrationDate(), caseItem.getUniqueId(),
                incidentList, recordId);
        return itemValues;
    }

    @Override
    protected ItemValuesMap getItemValuesByUniqueId(String uniqueId) throws JSONException {
        Case caseItem = caseService.getByUniqueId(uniqueId);
        String caseJson = new String(caseItem.getContent().getBlob());
        ItemValuesMap itemValues = new ItemValuesMap(JsonUtils.toMap(new Gson().fromJson
                (caseJson, JsonObject.class)));
        itemValues.addStringItem(CASE_ID, caseItem.getUniqueId());

        List<String> incidentList = caseService.getIncidentsByCaseId(caseItem.getUniqueId());
        addProfileItems(itemValues, caseItem.getRegistrationDate(), caseItem.getUniqueId(),
                incidentList, caseItem.getId());
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
                field.setSectionName(section.getName());
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
            List<Field> fields = new ArrayList<>();
            Section section = form.getSections().get(position);
            for (Field field : section.getFields()) {
                field.setSectionName(section.getName());
                fields.add(field);
            }
            return fields;
        }
        return null;
    }

    @Override
    public RecordForm getTemplateForm() {
        switch (caseType) {
            case MODULE_CASE_CP:
                return caseFormService.getCPTemplate();
            case MODULE_CASE_GBV:
                return caseFormService.getGBVTemplate();
            default:
                return caseFormService.getCPTemplate();
        }
    }

    private boolean validateRequiredField(ItemValuesMap itemValuesMap) {
        return caseService.validateRequiredFields(getTemplateForm(), itemValuesMap);
    }

    public ItemValuesMap filterGBVRelatedItemValues(ItemValuesMap recordRegisterData) {
        ItemValuesMap itemValuesMap = new ItemValuesMap();
        for (String itemKey : RecordService.RelatedItemColumn.GBV_RELATED_ITEMS) {
            if (recordRegisterData.has(itemKey)) {
                itemValuesMap.addItem(itemKey, recordRegisterData.getAsObject(itemKey));
            }
        }

        return itemValuesMap;
    }
}
