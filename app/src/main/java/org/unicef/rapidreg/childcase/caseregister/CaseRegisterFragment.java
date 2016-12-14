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
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.childcase.CaseFeature;
import org.unicef.rapidreg.childcase.casephoto.CasePhotoAdapter;
import org.unicef.rapidreg.service.RecordService;

import java.util.ArrayList;
import javax.inject.Inject;
import butterknife.OnClick;

import static org.unicef.rapidreg.childcase.CaseFeature.ADD_CP_MINI;
import static org.unicef.rapidreg.childcase.CaseFeature.ADD_GBV_MINI;
import static org.unicef.rapidreg.childcase.CaseFeature.DETAILS_MINI;
import static org.unicef.rapidreg.childcase.CaseFeature.EDIT_MINI;
import static org.unicef.rapidreg.service.RecordService.MODULE;

public class CaseRegisterFragment extends RecordRegisterFragment {
    @Inject
    CaseRegisterPresenter caseRegisterPresenter;

    @Inject
    CasePhotoAdapter casePhotoAdapter;

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

        casePhotoAdapter.setItems(caseRegisterPresenter.getDefaultPhotoPaths());
        recordRegisterAdapter.setPhotoAdapter(casePhotoAdapter);

        return recordRegisterAdapter;
    }

    @Override
    public CaseRegisterPresenter createPresenter() {
        if (getArguments() != null && getArguments().containsKey(MODULE)) {
            caseRegisterPresenter.setCaseType(getArguments().getString(MODULE));
        }
        return caseRegisterPresenter;
    }

    @OnClick(R.id.form_switcher)
    public void onSwitcherChecked() {
        Bundle args = new Bundle();
        args.putString(MODULE, caseRegisterPresenter.getCaseType());
        args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) getPhotoPathsData());
        args.putSerializable(RecordService.ITEM_VALUES, getRecordRegisterData());

        CaseFeature currentFeature = (CaseFeature) ((CaseActivity) getActivity()).getCurrentFeature();

        Feature feature = currentFeature.isDetailMode() ?
                DETAILS_MINI : currentFeature.isAddMode() ?
                (currentFeature.isCPCase() ? ADD_CP_MINI : ADD_GBV_MINI) : EDIT_MINI;
        ((RecordActivity) getActivity()).turnToFeature(feature, args, ANIM_TO_MINI);
    }

    @Override
    public void onSaveSuccessful(long recordId) {
        Toast.makeText(getActivity(), "CaseRgisterFragment save successfully", Toast.LENGTH_SHORT).show();
    }
}
