package org.unicef.rapidreg.base.record.recordregister;

import android.os.Bundle;
import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.json.JSONException;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterView.SaveRecordCallback;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static org.unicef.rapidreg.base.record.recordregister.RecordRegisterFragment
        .INVALID_RECORD_ID;
import static org.unicef.rapidreg.base.record.recordregister.RecordRegisterFragment.ITEM_VALUES;

public abstract class RecordRegisterPresenter extends MvpBasePresenter<RecordRegisterView> {

    private static final String TAG = RecordRegisterPresenter.class.getSimpleName();

    private List<Field> validFields;

    private RecordService recordService;

    public RecordRegisterPresenter(RecordService recordService) {
        this.recordService = recordService;
    }

    public List<Field> getValidFields() {
        List<Field> fields = getFields();
        validFields = removeSeparatorFields(fields);
        return validFields;
    }

    public List<Field> getValidFields(int position) {
        List<Field> fields = getFields(position);
        validFields = removeSeparatorFields(fields);
        return validFields;
    }


    public List<Field> removeSeparatorFields(List<Field> fields) {
        Iterator<Field> iterator = fields.iterator();

        while (iterator.hasNext()) {
            Field field = iterator.next();
            if (field.isSeparator()) {
                iterator.remove();
            }
        }

        return fields;
    }


    public void addProfileItems(ItemValuesMap itemValues, Date registrationDate, String
            uniqueId, List<String> incidentIds, long recordId) {
        String shortUUID = recordService.getShortUUID(uniqueId);
        DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        itemValues.addStringItem(ItemValuesMap.RecordProfile.ID_NORMAL_STATE, shortUUID);
        itemValues.addNumberItem(ItemValuesMap.RecordProfile.ID, recordId);

        if (registrationDate != null) {
            itemValues.addStringItem(ItemValuesMap.RecordProfile.REGISTRATION_DATE,
                    dateFormat.format(registrationDate));
        }

        if (incidentIds != null) {
            itemValues.addListItem(ItemValuesMap.RecordProfile.INCIDENT_LINKS,
                    incidentIds);
        }

    }

    public void clearProfileItems(ItemValuesMap itemValues) {
        itemValues.removeItem(ItemValuesMap.RecordProfile.ID_NORMAL_STATE);
        itemValues.removeItem(ItemValuesMap.RecordProfile.REGISTRATION_DATE);
        itemValues.removeItem(ItemValuesMap.RecordProfile.ID);
    }

    public ItemValuesMap getDefaultItemValues() {
        if (!isViewAttached()) {
            return new ItemValuesMap();
        }

        Bundle bundle = ((MvpFragment) getView()).getArguments();
        if (bundle == null) {
            return new ItemValuesMap();
        }

        if (getRecordId(bundle) == INVALID_RECORD_ID) {
            ItemValuesMap itemValuesMap = (ItemValuesMap) bundle.getSerializable(ITEM_VALUES);
            if (itemValuesMap != null) {
                return itemValuesMap;
            }
            return new ItemValuesMap();
        }

        try {
            return getItemValuesByRecordId(getRecordId(bundle));
        } catch (JSONException e) {
            Log.e(TAG, "Json conversion error");
            return new ItemValuesMap();
        }
    }

    public List<String> getDefaultPhotoPaths() {
        if (!isViewAttached()) {
            return new ArrayList<>();
        }

        Bundle bundle = ((RecordRegisterFragment) getView()).getArguments();
        if (bundle == null) {
            return new ArrayList<>();
        }

        List<String> recordPhotos = bundle.getStringArrayList(RecordService.RECORD_PHOTOS);
        if (recordPhotos != null && !recordPhotos.isEmpty()) {
            return recordPhotos;
        }

        if (getRecordId(bundle) == INVALID_RECORD_ID) {
            return new ArrayList<>();
        }

        return getPhotoPathsByRecordId(getRecordId(bundle));
    }

    public abstract void saveRecord(ItemValuesMap itemValuesMap, List<String> photoPaths,
                                    SaveRecordCallback callback);

    protected abstract ItemValuesMap getItemValuesByRecordId(Long recordId) throws JSONException;

    protected abstract List<String> getPhotoPathsByRecordId(Long recordId);

    protected abstract Long getRecordId(Bundle bundle);

    protected abstract List<Field> getFields();

    public abstract List<Field> getFields(int position);

    public abstract RecordForm getTemplateForm();
}