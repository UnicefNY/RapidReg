package org.unicef.rapidreg.base.record.recordregister;

import com.hannesdorfmann.mosby.mvp.MvpView;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.List;

public interface RecordRegisterView extends MvpView {

    void onInitViewContent();
    void setRecordRegisterData(ItemValuesMap itemValues);
    void setPhotoPathsData(List<String> photoPaths);
    List<String> getPhotoPathsData();
    ItemValuesMap getRecordRegisterData();
    void setFieldValueVerifyResult(ItemValuesMap fieldValueVerifyResult);
    ItemValuesMap getFieldValueVerifyResult();

    interface SaveRecordCallback {
        void onSaveSuccessful(long recordId);
        void onSavedFail();
        void onRequiredFieldNotFilled();
        void onFieldValueInvalid();
    }
}
