package org.unicef.rapidreg.childcase.caseregister;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordphoto.RecordPhotoAdapter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterFragment;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterPresenter;
import org.unicef.rapidreg.childcase.CaseFeature;
import org.unicef.rapidreg.childcase.casephoto.CasePhotoAdapter;
import org.unicef.rapidreg.forms.CaseFormRoot;
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

import static org.unicef.rapidreg.service.RecordService.ITEM_VALUES;

public class CaseRegisterPresenter extends RecordRegisterPresenter {

    private static final String TAG = CaseRegisterPresenter.class.getSimpleName();
    private CaseService caseService;
    private CaseFormService caseFormService;

    @Inject
    public CaseRegisterPresenter(CaseService caseService, CaseFormService caseFormService) {
        this.caseService = caseService;
        this.caseFormService = caseFormService;
    }

    @Override
    public CaseFormRoot getCurrentForm() {
        return caseFormService.getCurrentForm();
    }

    @Override
    public void saveRecord(ItemValuesMap itemValuesMap) {
        if (!validateRequiredField(itemValuesMap)) {
            getView().promoteRequiredFieldNotFilled();
            return;
        }
        if (!isViewAttached()){
            return;
        }
        CaseMiniFormFragment view = (CaseMiniFormFragment) getView();
        clearProfileItems(itemValuesMap);
        List<String> photoPaths =  view.getPhotos();
        ItemValues itemValues = new ItemValues(new Gson().fromJson(new Gson().toJson(
                view.getItemValues().getValues()), JsonObject.class));
        try {
            Case record = caseService.saveOrUpdate(itemValues, photoPaths);
            getView().saveSuccessfully(record.getId());
        } catch (IOException e) {
            getView().promoteSaveFail();
        }


    }

    private boolean validateRequiredField(ItemValuesMap itemValuesMap) {
        CaseFormRoot caseForm = getCurrentForm();
        return RecordService.validateRequiredFields(caseForm, itemValuesMap);
    }

    public void initItemValues() {
        CaseMiniFormFragment view = (CaseMiniFormFragment) getView();
        if (view.getArguments() != null) {
            long  recordId = view.getArguments().getLong(CaseService.CASE_PRIMARY_ID, RecordRegisterFragment.INVALID_RECORD_ID);
            if (recordId != RecordRegisterFragment.INVALID_RECORD_ID) {
                Case item = caseService.getById(recordId);
                String caseJson = new String(item.getContent().getBlob());
                try {
                    ItemValuesMap itemValues = new ItemValuesMap(JsonUtils.toMap(ItemValues.generateItemValues(caseJson).getValues()));
                    itemValues.addStringItem(CaseService.CASE_ID, item.getUniqueId());
                    ((CaseMiniFormFragment) getView()).setItemValues(itemValues);
                } catch (JSONException e) {
                    Log.e(TAG, "Json conversion error");
                }

                DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
                String shortUUID = RecordService.getShortUUID(item.getUniqueId());
                view.getItemValues().addStringItem(ItemValues.RecordProfile.ID_NORMAL_STATE, shortUUID);
                view.getItemValues().addStringItem(ItemValues.RecordProfile.REGISTRATION_DATE,
                        dateFormat.format(item.getRegistrationDate()));
                view.getItemValues().addNumberItem(ItemValues.RecordProfile.ID, item.getId());
                view.setPhotoAdapter(initPhotoAdapter(recordId));
            } else {
                view.getRegisterAdapter().setItemValues((ItemValuesMap) view.getArguments().getSerializable(ITEM_VALUES));
                CasePhotoAdapter photoAdapter = new CasePhotoAdapter(view.getContext(),
                        view.getArguments().getStringArrayList(RecordService.RECORD_PHOTOS));
                view.setPhotoAdapter(photoAdapter);
            }
        } else {
            view.setItemValues(new ItemValuesMap());
            view.setPhotoAdapter(new CasePhotoAdapter(view.getContext(), new ArrayList<String>()));
        }
    }

    private RecordPhotoAdapter initPhotoAdapter(long recordId) {
        List<String> paths = new ArrayList<>();

        List<Long> casePhotoIds = CasePhotoService.getInstance().getIdsByCaseId(recordId);

        for (Long casePhotoId : casePhotoIds) {
            paths.add(String.valueOf(casePhotoId));
        }
        return new CasePhotoAdapter(((CaseMiniFormFragment)getView()).getContext(), paths);
    }
}
