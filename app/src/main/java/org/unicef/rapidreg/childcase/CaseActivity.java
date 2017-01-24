package org.unicef.rapidreg.childcase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.IntentSender;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.BaseAlertDialog;
import org.unicef.rapidreg.base.BaseView;
import org.unicef.rapidreg.base.RecordConfiguration;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListFragment;
import org.unicef.rapidreg.childcase.caselist.CaseListFragment;
import org.unicef.rapidreg.event.LoadCPCaseFormEvent;
import org.unicef.rapidreg.event.LoadGBVCaseFormEvent;
import org.unicef.rapidreg.event.SaveCaseEvent;
import org.unicef.rapidreg.utils.Utils;

import javax.inject.Inject;

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
        super.onCreate(savedInstanceState);

        navigationView.setCheckedItem(R.id.nav_cases);
        navigationView.setItemTextColor(caseColor);
        if (getIntent().getBooleanExtra(IntentSender.IS_FROM_LOGIN, false)) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            drawer.closeDrawer(GravityCompat.START);
        }

        turnToFeature(CaseFeature.LIST, null, null);
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
        AlertDialog dialog = new BaseAlertDialog.Builder(this)
                .setTitle(R.string.quit)
                .setMessage(R.string.quit_without_saving)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();

        Utils.changeDialogDividerColor(this, dialog);
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void onNeedLoadGBVCaseFormsEvent(final LoadGBVCaseFormEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        String moduleId = RecordConfiguration.MODULE_ID_GBV;
        String cookie = event.getCookie();
        casePresenter.loadCaseForm(PrimeroAppConfiguration.getDefaultLanguage(), cookie, moduleId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void onNeedLoadCPCaseFormsEvent(final LoadCPCaseFormEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        String moduleId = RecordConfiguration.MODULE_ID_CP;
        String cookie = event.getCookie();
        casePresenter.loadCaseForm(PrimeroAppConfiguration.getDefaultLanguage(), cookie, moduleId);
    }

    @Override
    public void promoteSyncFormsError() {
        Toast.makeText(CaseActivity.this, R.string.sync_forms_error, Toast.LENGTH_SHORT).show();
    }
}
