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
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterBtnType;
import org.unicef.rapidreg.childcase.CaseFeature;
import org.unicef.rapidreg.event.LoadCPCaseFormEvent;

import java.util.Arrays;
import java.util.HashMap;

import javax.inject.Inject;

import static android.view.View.*;
import static org.unicef.rapidreg.base.record.recordregister.RecordRegisterBtnType.CASE_CP;
import static org.unicef.rapidreg.base.record.recordregister.RecordRegisterBtnType.CASE_GBV;


public class CaseListFragment extends RecordListFragment {

    public static final String BUNDLE_CASE_TYPE = "case_type";

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
    protected HashMap<RecordRegisterBtnType, OnClickListener> getCreateEvents() {
        HashMap<RecordRegisterBtnType, OnClickListener> events = new HashMap<>();
        events.put(CASE_CP, createOnClickListener(CASE_CP));
        events.put(CASE_GBV, createOnClickListener(CASE_GBV));
        return events;
    }

    private OnClickListener createOnClickListener(RecordRegisterBtnType eventType) {
        switch (eventType) {
            case CASE_CP: return new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCPCaseAddClicked();
                }
            };
            case CASE_GBV:return new OnClickListener() {
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
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_CASE_TYPE, CASE_CP.getBtnTitle());
        activity.turnToFeature(CaseFeature.ADD_MINI, bundle, null);
    }

    private void onGBVCaseAddClicked() {
        caseListPresenter.clearAudioFile();

        if (!caseListPresenter.isFormReady()) {
            showSyncFormDialog(getResources().getString(R.string.child_case));
            return;
        }

        RecordActivity activity = (RecordActivity) getActivity();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_CASE_TYPE, CASE_GBV.getBtnTitle());
        activity.turnToFeature(CaseFeature.ADD_MINI, bundle, null);
    }

}
