package org.unicef.rapidreg.childcase.caselist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.base.record.recordlist.RecordListFragment;
import org.unicef.rapidreg.base.record.recordlist.RecordListPresenter;
import org.unicef.rapidreg.base.record.recordlist.spinner.SpinnerState;
import org.unicef.rapidreg.childcase.CaseFeature;
import org.unicef.rapidreg.event.LoadCPCaseFormEvent;

import java.util.Arrays;
import java.util.HashMap;

import javax.inject.Inject;

import static android.view.View.*;


public class CaseListFragment extends RecordListFragment {

    private static final SpinnerState[] SPINNER_STATES = {
            SpinnerState.AGE_ASC,
            SpinnerState.AGE_DES,
            SpinnerState.REG_DATE_ASC,
            SpinnerState.REG_DATE_DES};

    @Inject
    CaseListPresenter caseListPresenter;

    @Inject
    CaseListAdapter caseListAdapter;

    @Override
    public RecordListPresenter createPresenter() {
        return caseListPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getDefaultSpinnerStatePosition() {
        return Arrays.asList(SPINNER_STATES).indexOf(SpinnerState.REG_DATE_DES);
    }

    @Override
    protected SpinnerState[] getDefaultSpinnerStates() {
        return SPINNER_STATES;
    }

    @Override
    protected HashMap<String, OnClickListener> getCreateEvents() {
        HashMap<String, OnClickListener> events = new HashMap<>();
        events.put("CASE", createOnClickListener("CASE"));
        events.put("GBV", createOnClickListener("GBV"));
        return events;
    }

    private OnClickListener createOnClickListener(String eventType) {
        switch (eventType) {
            case "CASE" : return new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCPCaseAddClicked();
                }
            };
            case "GBV" :return new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onGBVCaseAddClicked();
                }
            };
            default: return null;
        }
    }

    @Override
    protected void sendSyncFormEvent() {
        EventBus.getDefault().postSticky(new LoadCPCaseFormEvent(PrimeroConfiguration.getCookie()));
    }

    @Override
    protected RecordListAdapter createRecordListAdapter() {
        return caseListAdapter;
    }

    private void onCPCaseAddClicked() {
        caseListPresenter.clearAudioFile();

        if (!caseListPresenter.isFormReady()) {
            showSyncFormDialog(getResources().getString(R.string.child_case));
            return;
        }

        RecordActivity activity = (RecordActivity) getActivity();
        activity.turnToFeature(CaseFeature.ADD_MINI, null, null);
    }

    private void onGBVCaseAddClicked() {
        //TODO redirect to GBVCaseRegisterFragment
        Toast.makeText(getActivity(), "GBV creation has been clicked", Toast.LENGTH_SHORT).show();
    }

}
