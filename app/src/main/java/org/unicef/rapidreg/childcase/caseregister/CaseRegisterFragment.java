package org.unicef.rapidreg.childcase.caseregister;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterAdapter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterFragment;
import org.unicef.rapidreg.childcase.CaseFeature;
import org.unicef.rapidreg.childcase.casephoto.CasePhotoAdapter;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.OnClick;

public class CaseRegisterFragment extends RecordRegisterFragment {

    @Inject
    CaseRegisterPresenter caseRegisterPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public CaseRegisterPresenter createPresenter() {
        return caseRegisterPresenter;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<Field> fields = getFields();
        caseRegisterPresenter.initContext(getActivity(), fields, false);
        if (getArguments() != null) {
            setPhotoAdapter(new CasePhotoAdapter(getContext(),
                    getArguments().getStringArrayList(RecordService.RECORD_PHOTOS)));
            setItemValues((ItemValuesMap) getArguments().getSerializable(ITEM_VALUES));
        }
    }

    @OnClick(R.id.form_switcher)
    public void onSwitcherChecked() {
        Bundle args = new Bundle();
        args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) getPhotos());
        args.putSerializable(RecordService.ITEM_VALUES, getItemValues());

        Feature feature = ((RecordActivity) getActivity()).getCurrentFeature().isDetailMode() ?
                CaseFeature.DETAILS_MINI : ((RecordActivity) getActivity()).getCurrentFeature().isAddMode() ?
                CaseFeature.ADD_MINI : CaseFeature.EDIT_MINI;
        ((RecordActivity) getActivity()).turnToFeature(feature, args, ANIM_TO_MINI);
    }

    protected List<Field> getFields() {
        int position = FragmentPagerItem.getPosition(getArguments());
        RecordForm form = caseRegisterPresenter.getCurrentForm();
        if (form != null) {
            return form.getSections().get(position).getFields();
        }
        return null;
    }

    @Override
    public void saveSuccessfully(long recordId) {}

    @Override
    public void initView(RecordRegisterAdapter adapter) {
        super.initView(adapter);
    }
}
