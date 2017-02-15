package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.IntentSender;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.BaseView;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListFragment;
import org.unicef.rapidreg.childcase.caselist.CaseListFragment;
import org.unicef.rapidreg.event.LoadCPCaseFormEvent;
import org.unicef.rapidreg.event.LoadGBVCaseFormEvent;
import org.unicef.rapidreg.event.LoadGBVIncidentFormEvent;
import org.unicef.rapidreg.event.RedirectIncidentEvent;
import org.unicef.rapidreg.event.SaveCaseEvent;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.utils.Utils;
import org.unicef.rapidreg.widgets.dialog.MessageDialog;

import javax.inject.Inject;

import static org.unicef.rapidreg.service.IncidentService.INCIDENT_ID;
import static org.unicef.rapidreg.service.RecordService.AUDIO_FILE_PATH;

public class CaseActivity extends RecordActivity implements BaseView {
    public static final String TAG = CaseActivity.class.getSimpleName();

    @Inject
    CasePresenter casePresenter;

    @NonNull
    @Override
    public CasePresenter createPresenter() {
        return casePresenter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);

        setNavSelectedMenu(R.id.nav_cases, caseColor);

        if (getIntent().getBooleanExtra(IntentSender.IS_FROM_LOGIN, false)) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            drawer.closeDrawer(GravityCompat.START);
        }

        turnToFeature(CaseFeature.LIST, null, null);

    }

    @Override
    public void sendSyncFormEvent() {
        User.Role roleType = PrimeroAppConfiguration.getCurrentUser().getRoleType();
        if (roleType == User.Role.CP) {
            EventBus.getDefault().postSticky(new LoadCPCaseFormEvent(PrimeroAppConfiguration.getCookie()));
        } else if (roleType == User.Role.GBV) {
            EventBus.getDefault().postSticky(new LoadGBVCaseFormEvent(PrimeroAppConfiguration.getCookie()));
            EventBus.getDefault().postSticky(new LoadGBVIncidentFormEvent(PrimeroAppConfiguration.getCookie()));
        } else {
            EventBus.getDefault().postSticky(new LoadCPCaseFormEvent(PrimeroAppConfiguration.getCookie()));
        }
    }

    @Override
    protected RecordListFragment getRecordListFragment() {
        return (CaseListFragment) getSupportFragmentManager()
                .findFragmentByTag(CaseListFragment.class.getSimpleName());

    }

    @Override
    protected void processBackButton() {
        if (currentFeature.isListMode()) {
            logOut();
        } else if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_cases);
        } else {
            Utils.clearAudioFile(AUDIO_FILE_PATH);
            turnToFeature(CaseFeature.LIST, null, null);
        }
    }

    @Override
    protected void navCaseAction() {
        setShowHideSwitcherToShowState();
        if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_cases);
        } else {
            Utils.clearAudioFile(AUDIO_FILE_PATH);
            turnToFeature(CaseFeature.LIST, null, null);
        }
    }

    @Override
    protected void navTracingAction() {
        if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_tracing);
        } else {
            Utils.clearAudioFile(AUDIO_FILE_PATH);
            intentSender.showTracingActivity(this, true);
        }
    }

    @Override
    protected void navIncidentAction() {
        if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_incident);
        } else {
            Utils.clearAudioFile(AUDIO_FILE_PATH);
            intentSender.showIncidentActivity(this, true);
        }
    }

    @Override
    protected void showQuitDialog(final int clickedButton) {
        MessageDialog messageDialog = new MessageDialog(this);
        messageDialog.setTitle((R.string.quit));
        messageDialog.setMessage(R.string.quit_without_saving);
        messageDialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.clearAudioFile(AUDIO_FILE_PATH);
                switch (clickedButton) {
                    case R.id.nav_cases:
                        turnToFeature(CaseFeature.LIST, null, null);
                        break;
                    case R.id.nav_tracing:
                        intentSender.showTracingActivity(CaseActivity.this, true);
                        break;
                    case R.id.nav_incident:
                        intentSender.showIncidentActivity(CaseActivity.this, true);
                        break;
                    case R.id.nav_sync:
                        intentSender.showSyncActivity(CaseActivity.this, true);
                        break;
                    default:
                        break;
                }
                messageDialog.dismiss();
            }
        });
        messageDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageDialog.dismiss();
            }
        });
        messageDialog.show();
    }

    @Override
    protected void search() {
        turnToFeature(CaseFeature.SEARCH, null, null);
    }

    @Override
    protected void save() {
        SaveCaseEvent event = new SaveCaseEvent();
        EventBus.getDefault().postSticky(event);
    }

    @Override
    public void promoteSyncFormsError() {
        Utils.showMessageByToast(this, R.string.sync_forms_error, Toast.LENGTH_SHORT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1)
    public void onRedirectIncidentEvent(RedirectIncidentEvent event) {
        String incidentId = event.getIncidentInfo();
        Bundle extra = new Bundle();
        extra.putString(INCIDENT_ID, incidentId);

        if (casePresenter.isIncidentFormReady()) {
            intentSender.showIncidentActivity(this, true, extra);
        } else {
            if (PrimeroApplication.getAppRuntime().isIncidentFormSyncFail()) {
                showSyncFormDialog(getResources().getString(R.string.sync_forms_message));
            } else {
                Utils.showMessageByToast(this, R.string.forms_is_syncing_msg, Toast.LENGTH_SHORT);
            }
        }
    }
}
