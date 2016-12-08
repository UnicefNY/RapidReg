package org.unicef.rapidreg.childcase.caselist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import javax.inject.Inject;

import butterknife.OnClick;


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
    protected void sendSyncFormEvent() {
        EventBus.getDefault().postSticky(new LoadCPCaseFormEvent(PrimeroConfiguration.getCookie()));
    }

    @Override
    protected RecordListAdapter createRecordListAdapter() {
        return caseListAdapter;
    }

    @OnClick(R.id.add)
    public void onCaseAddClicked() {
        caseListPresenter.clearAudioFile();

        if (!caseListPresenter.isFormReady()) {
            showSyncFormDialog(getResources().getString(R.string.child_case));
            return;
        }

        RecordActivity activity = (RecordActivity) getActivity();
        activity.turnToFeature(CaseFeature.ADD_MINI, null, null);
    }

}
