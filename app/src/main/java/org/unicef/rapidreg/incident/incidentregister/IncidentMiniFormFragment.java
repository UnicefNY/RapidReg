package org.unicef.rapidreg.incident.incidentregister;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterAdapter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterFragment;
import org.unicef.rapidreg.event.SaveIncidentEvent;
import org.unicef.rapidreg.incident.IncidentActivity;
import org.unicef.rapidreg.incident.IncidentFeature;
import org.unicef.rapidreg.service.IncidentService;
import org.unicef.rapidreg.service.RecordService;

import javax.inject.Inject;

import butterknife.OnClick;

public class IncidentMiniFormFragment extends RecordRegisterFragment {

    public static final String TAG = IncidentMiniFormFragment.class.getSimpleName();

    @Inject
    IncidentRegisterPresenter incidentRegisterPresenter;

    @NonNull
    @Override
    public IncidentRegisterPresenter createPresenter() {
        return incidentRegisterPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onInitViewContent() {
        super.onInitViewContent();
        addProfileFieldForDetailsPage(incidentRegisterPresenter.getFields());
        formSwitcher.setText(R.string.show_more_details);
        if (((RecordActivity) getActivity()).getCurrentFeature().isDetailMode()) {
            editButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected RecordRegisterAdapter createRecordRegisterAdapter() {
        RecordRegisterAdapter recordRegisterAdapter = new RecordRegisterAdapter(getActivity(),
                incidentRegisterPresenter.getValidFields(),
                incidentRegisterPresenter.getDefaultItemValues(),
                true);
        return recordRegisterAdapter;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveIncident(SaveIncidentEvent event) {
        incidentRegisterPresenter.saveRecord(getRecordRegisterData(), this);
    }

    @Override
    public void onSaveSuccessful(long recordId) {
        Toast.makeText(getActivity(), R.string.save_success, Toast.LENGTH_SHORT).show();
        Bundle args = new Bundle();
        args.putLong(IncidentService.INCIDENT_PRIMARY_ID, recordId);
        ((RecordActivity) getActivity()).turnToFeature(IncidentFeature.DETAILS_MINI, args, null);
    }

    @OnClick(R.id.edit)
    public void onEditClicked() {
        Bundle args = new Bundle();
        args.putSerializable(RecordService.ITEM_VALUES, getRecordRegisterData());
        ((IncidentActivity) getActivity()).turnToFeature(IncidentFeature.EDIT_MINI, args, null);
    }

    @OnClick(R.id.form_switcher)
    public void onSwitcherChecked() {
        Bundle args = new Bundle();
        args.putSerializable(RecordService.ITEM_VALUES, getRecordRegisterData());
        Feature feature = ((RecordActivity) getActivity()).getCurrentFeature().isDetailMode() ?
                IncidentFeature.DETAILS_FULL : ((RecordActivity) getActivity()).getCurrentFeature()
                .isAddMode() ?
                IncidentFeature.ADD_FULL : IncidentFeature.EDIT_FULL;
        ((RecordActivity) getActivity()).turnToFeature(feature, args, ANIM_TO_FULL);
    }
}
