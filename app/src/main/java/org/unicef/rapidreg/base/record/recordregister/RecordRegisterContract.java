package org.unicef.rapidreg.base.record.recordregister;

import org.unicef.rapidreg.base.record.recordregister.RecordRegisterContract.View.OnSaveRecordCallback;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.List;

public interface RecordRegisterContract {

    interface View {
        void initView(RecordRegisterAdapter adapter);
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

    interface Presenter {

        void initItemValues();

        void saveRecord(OnSaveRecordCallback callback);

    }

}
