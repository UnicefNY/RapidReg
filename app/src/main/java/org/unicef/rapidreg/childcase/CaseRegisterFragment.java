package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.RecordRegisterFragment;
import org.unicef.rapidreg.base.RecordRegisterPresenter;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValues;

public class CaseRegisterFragment extends RecordRegisterFragment {

    @NonNull
    @Override
    public RecordRegisterPresenter createPresenter() {
        return new RecordRegisterPresenter(RecordModel.CASE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        if (getArguments() != null) {
            photoAdapter = new CasePhotoAdapter(getContext(),
                    getArguments().getStringArrayList(RecordService.RECORD_PHOTOS));
            itemValues = ItemValues.fromJson(getArguments().getString(ITEM_VALUES));
        }
        return inflater.inflate(R.layout.fragment_register, container, false);
    }
}
