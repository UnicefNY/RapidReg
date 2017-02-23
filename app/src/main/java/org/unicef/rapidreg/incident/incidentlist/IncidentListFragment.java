package org.unicef.rapidreg.incident.incidentlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.base.record.recordlist.RecordListFragment;
import org.unicef.rapidreg.base.record.recordlist.RecordListPresenter;
import org.unicef.rapidreg.base.record.recordlist.spinner.SpinnerState;
import org.unicef.rapidreg.incident.IncidentFeature;
import org.unicef.rapidreg.utils.Utils;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.OnClick;

public class IncidentListFragment extends RecordListFragment {

    private static final SpinnerState[] SPINNER_STATES = {
            SpinnerState.AGE_ASC,
            SpinnerState.AGE_DES,
            SpinnerState.INTERVIEW_DATE_ASC,
            SpinnerState.INTERVIEW_DATE_DES};

    @Inject
    IncidentListPresenter incidentListPresenter;

    @Inject
    IncidentListAdapter incidentListAdapter;

    @Override
    public RecordListPresenter createPresenter() {
        return incidentListPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        getComponent().inject(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getDefaultSpinnerStatePosition() {
        return Arrays.asList(SPINNER_STATES).indexOf(SpinnerState.INTERVIEW_DATE_DES);
    }

    @Override
    protected SpinnerState[] getDefaultSpinnerStates() {
        return SPINNER_STATES;
    }

    @Override
    protected RecordListAdapter createRecordListAdapter() {
        return incidentListAdapter;
    }

    @OnClick(R.id.add)
    public void onIncidentAddClicked() {
        incidentListPresenter.clearAudioFile();

        if (!incidentListPresenter.isFormReady()) {
            if (PrimeroApplication.getAppRuntime().isIncidentFormSyncFail()) {
                showSyncFormDialog(getResources().getString(R.string.child_incident));
            } else {
                Utils.showMessageByToast(getActivity(), R.string.forms_is_syncing_msg, Toast.LENGTH_SHORT);
            }
            return;
        }

        RecordActivity activity = (RecordActivity) getActivity();
        activity.turnToFeature(IncidentFeature.ADD_MINI, null, null);
    }

    @OnClick(R.id.list_item_delete_cancel_button)
    public void onListItemDeleteCancelButtonClicked(Button button) {
        ((RecordActivity) getActivity()).turnToFeature(IncidentFeature.LIST, null, null);
    }
}
