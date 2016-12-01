package org.unicef.rapidreg.base.record.recordregister;


import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.MvpView;

import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.List;

public interface RecordRegisterView extends MvpView {

    void onInitViewContent();
    void setRecordRegisterData(ItemValuesMap itemValues);
    void setPhotoPathsData(List<String> photoPaths);
    List<String> getPhotoPathsData();
    ItemValuesMap getRecordRegisterData();

    interface OnSaveRecordCallback {
        void saveSuccessfully(long recordId);
        void promoteRequiredFieldNotFilled();
        void promoteSaveFailed();
    }
}
