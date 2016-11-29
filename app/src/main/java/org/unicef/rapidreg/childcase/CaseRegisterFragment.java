package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.base.RecordActivity;
import org.unicef.rapidreg.base.RecordRegisterFragment;
import org.unicef.rapidreg.base.RecordRegisterPresenter;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

public class CaseRegisterFragment extends RecordRegisterFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (getArguments() != null) {
            photoAdapter = new CasePhotoAdapter(getContext(),
                    getArguments().getStringArrayList(RecordService.RECORD_PHOTOS));
            itemValues = (ItemValuesMap) getArguments().getSerializable(ITEM_VALUES);
        }
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<Field> fields = getFields();
        presenter.initContext(getActivity(), fields, false);
    }

    @OnClick(R.id.form_switcher)
    public void onSwitcherChecked() {
        Bundle args = new Bundle();
        args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) photoAdapter.getAllItems());
        args.putSerializable(RecordService.ITEM_VALUES, itemValues);

        Feature feature = ((RecordActivity) getActivity()).getCurrentFeature().isDetailMode() ?
                CaseFeature.DETAILS_MINI : ((RecordActivity) getActivity()).getCurrentFeature().isAddMode() ?
                CaseFeature.ADD_MINI : CaseFeature.EDIT_MINI;
        ((RecordActivity) getActivity()).turnToFeature(feature, args, ANIM_TO_MINI);
    }

    protected List<Field> getFields() {
        int position = FragmentPagerItem.getPosition(getArguments());
        RecordForm form = CaseFormService.getInstance().getCurrentForm();
        if (form != null) {
            return form.getSections().get(position).getFields();
        }
        return null;
    }
}
