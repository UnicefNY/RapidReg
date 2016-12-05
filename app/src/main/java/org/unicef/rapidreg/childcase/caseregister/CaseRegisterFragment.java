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
import org.unicef.rapidreg.base.record.recordphoto.RecordPhotoAdapter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterAdapter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterFragment;
import org.unicef.rapidreg.childcase.CaseFeature;
import org.unicef.rapidreg.childcase.casephoto.CasePhotoAdapter;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.RecordForm;
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
    protected RecordRegisterAdapter createRecordRegisterAdapter() {
        RecordRegisterAdapter recordRegisterAdapter = new RecordRegisterAdapter(getActivity(),
                caseRegisterPresenter.getValidFields(FragmentPagerItem.getPosition(getArguments())),
                caseRegisterPresenter.getDefaultItemValues(),
                false);

        RecordPhotoAdapter recordPhotoAdapter = new CasePhotoAdapter(getActivity(), caseRegisterPresenter.getDefaultPhotoPaths());
        recordRegisterAdapter.setPhotoAdapter(recordPhotoAdapter);

        return recordRegisterAdapter;
    }

    @Override
    public CaseRegisterPresenter createPresenter() {
        return caseRegisterPresenter;
    }

    @OnClick(R.id.form_switcher)
    public void onSwitcherChecked() {
        Bundle args = new Bundle();
        args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) getPhotoPathsData());
        args.putSerializable(RecordService.ITEM_VALUES, getRecordRegisterData());

        Feature feature = ((RecordActivity) getActivity()).getCurrentFeature().isDetailMode() ?
                CaseFeature.DETAILS_MINI : ((RecordActivity) getActivity()).getCurrentFeature().isAddMode() ?
                CaseFeature.ADD_MINI : CaseFeature.EDIT_MINI;
        ((RecordActivity) getActivity()).turnToFeature(feature, args, ANIM_TO_MINI);
    }

    @Override
    public void onSaveSuccessful(long recordId) {
        Toast.makeText(getActivity(), "CaseRgisterFragment save successfully", Toast.LENGTH_SHORT).show();
    }
}
