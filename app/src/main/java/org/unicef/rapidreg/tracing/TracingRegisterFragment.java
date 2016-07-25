package org.unicef.rapidreg.tracing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.RecordRegisterFragment;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.List;

public class TracingRegisterFragment extends RecordRegisterFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (getArguments() != null) {
            photoAdapter = new TracingPhotoAdapter(getContext(),
                    getArguments().getStringArrayList(RecordService.RECORD_PHOTOS));
            itemValues = (ItemValuesMap) getArguments().getSerializable(ITEM_VALUES);
        }
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    protected List<Field> getFields(int position) {
        RecordForm form = TracingFormService.getInstance().getCurrentForm();
        if (form != null) {
            return form.getSections().get(position).getFields();
        }
        return null;
    }
}
