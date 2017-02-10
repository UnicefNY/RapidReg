package org.unicef.rapidreg.incident.incidentregister;

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
import org.unicef.rapidreg.incident.IncidentFeature;
import org.unicef.rapidreg.service.RecordService;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.OnClick;

import static org.unicef.rapidreg.service.CaseService.CASE_ID;

public class IncidentRegisterFragment extends RecordRegisterFragment {
    @Inject
    IncidentRegisterPresenter incidentRegisterPresenter;

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
                incidentRegisterPresenter.getValidFields(FragmentPagerItem.getPosition(getArguments())),
                incidentRegisterPresenter.getDefaultItemValues(),
                false);
        return recordRegisterAdapter;
    }

    @Override
    public IncidentRegisterPresenter createPresenter() {
        return incidentRegisterPresenter;
    }

    @OnClick(R.id.form_switcher)
    public void onSwitcherChecked() {
        Bundle args = new Bundle();
        args.putSerializable(RecordService.ITEM_VALUES, getRecordRegisterData());
        args.putString(CASE_ID, getArguments().getString(CASE_ID, null));

        Feature feature = ((RecordActivity) getActivity()).getCurrentFeature().isDetailMode() ?
                IncidentFeature.DETAILS_MINI : ((RecordActivity) getActivity()).getCurrentFeature()
                .isAddMode() ?
                IncidentFeature.ADD_MINI : IncidentFeature.EDIT_MINI;
        ((RecordActivity) getActivity()).turnToFeature(feature, args, ANIM_TO_MINI);
    }

    @Override
    public void onSaveSuccessful(long recordId) {
        Toast.makeText(getActivity(), "IncidentRegisterFragment save successfully", Toast
                .LENGTH_SHORT).show();
    }
}
