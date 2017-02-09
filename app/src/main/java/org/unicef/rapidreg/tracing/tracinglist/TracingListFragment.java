package org.unicef.rapidreg.tracing.tracinglist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.base.record.recordlist.RecordListFragment;
import org.unicef.rapidreg.base.record.recordlist.RecordListPresenter;
import org.unicef.rapidreg.base.record.recordlist.spinner.SpinnerState;
import org.unicef.rapidreg.event.LoadTracingFormEvent;
import org.unicef.rapidreg.tracing.TracingActivity;
import org.unicef.rapidreg.tracing.TracingFeature;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.OnClick;

public class TracingListFragment extends RecordListFragment {

    public static final SpinnerState[] SPINNER_STATES = {
            SpinnerState.INQUIRY_DATE_ASC,
            SpinnerState.INQUIRY_DATE_DES};

    @Inject
    TracingListPresenter tracingListPresenter;

    @Inject
    TracingListAdapter tracingListAdapter;

    @Override
    public RecordListPresenter createPresenter() {
        return tracingListPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        ((TracingActivity)getActivity()).enableShowHideSwitcher();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected RecordListAdapter createRecordListAdapter() {
        return tracingListAdapter;
    }

    @Override
    protected int getDefaultSpinnerStatePosition() {
        return Arrays.asList(SPINNER_STATES).indexOf(SpinnerState.INQUIRY_DATE_DES);
    }

    @Override
    protected SpinnerState[] getDefaultSpinnerStates() {
        return SPINNER_STATES;
    }

    @OnClick(R.id.add)
    public void onTracingAddClicked() {
        tracingListPresenter.clearAudioFile();

        if (!tracingListPresenter.isFormReady()) {
            if (PrimeroApplication.getAppRuntime().isTracingFormSyncFail()) {
                showSyncFormDialog(getResources().getString(R.string.tracing_request));
            } else {
                showMessageThruToast(getResources().getString(R.string.forms_is_syncing_msg));
            }
            return;
        }

        TracingActivity activity = (TracingActivity) getActivity();
        activity.turnToFeature(TracingFeature.ADD_MINI, null, null);
    }
}
