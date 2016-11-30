package org.unicef.rapidreg.base.record.recordregister;

import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

public interface RecordRegisterView extends MvpView {
    void initView(RecordRegisterAdapter adapter);
    void saveSuccessfully(long recordId);
    void promoteRequiredFieldNotFilled();
    void promoteSaveFail();
    List<String> getPhotos();
}
