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
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.childcase.CaseFeature;
import org.unicef.rapidreg.event.LoadCPCaseFormEvent;
import org.unicef.rapidreg.event.LoadGBVCaseFormEvent;
import org.unicef.rapidreg.model.User;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.OnClick;

import static org.unicef.rapidreg.childcase.caseregister.CaseRegisterPresenter.MODULE_CASE_CP;
import static org.unicef.rapidreg.childcase.caseregister.CaseRegisterPresenter.MODULE_CASE_GBV;
import static org.unicef.rapidreg.service.RecordService.MODULE;


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
        enableShowHideSwitcherForCPUser();
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
        EventBus.getDefault().postSticky(new LoadGBVCaseFormEvent(PrimeroConfiguration.getCookie()));
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
        bundle.putString(MODULE, MODULE_CASE_CP);
        activity.turnToFeature(CaseFeature.ADD_CP_MINI, bundle, null);
    }

    private void onGBVCaseAddClicked() {
        caseListPresenter.clearAudioFile();

        if (!caseListPresenter.isFormReady()) {
            showSyncFormDialog(getResources().getString(R.string.child_case));
            return;
        }

        RecordActivity activity = (RecordActivity) getActivity();
        Bundle bundle = new Bundle();
        bundle.putString(MODULE, MODULE_CASE_GBV);
        activity.turnToFeature(CaseFeature.ADD_GBV_MINI, bundle, null);
    }

    public void enableShowHideSwitcherForCPUser() {
        User.Role role = PrimeroConfiguration.getCurrentUser().getRoleType();
        if (User.Role.CP == role) {
            ((CaseActivity) getActivity()).enableShowHideSwitcher();
        }
    }

    @OnClick(R.id.add)
    public void onCaseAddClicked() {
        User.Role role = PrimeroConfiguration.getCurrentUser().getRoleType();
        switch (role) {
            case CP:
                onCPCaseAddClicked();
                break;
            case GBV:
                onGBVCaseAddClicked();
                break;
            default:
                break;
        }
    }

}
