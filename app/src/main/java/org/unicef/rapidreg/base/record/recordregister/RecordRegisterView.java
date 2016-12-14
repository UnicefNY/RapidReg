package org.unicef.rapidreg.base.record.recordregister;

import com.hannesdorfmann.mosby.mvp.MvpView;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.List;

public interface RecordRegisterView extends MvpView {

    void onInitViewContent();
    void setRecordRegisterData(ItemValuesMap itemValues);
    void setPhotoPathsData(List<String> photoPaths);
    List getPhotoPathsData();
    ItemValuesMap getRecordRegisterData();

    interface SaveRecordCallback {
        void onSaveSuccessful(long recordId);
        void onSavedFail();
        void onRequiredFieldNotFilled();
    }
}
