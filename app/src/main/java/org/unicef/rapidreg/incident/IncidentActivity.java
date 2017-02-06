package org.unicef.rapidreg.incident;

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
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.BaseAlertDialog;
import org.unicef.rapidreg.base.BaseView;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListFragment;
import org.unicef.rapidreg.event.LoadGBVIncidentFormEvent;
import org.unicef.rapidreg.event.SaveIncidentEvent;
import org.unicef.rapidreg.incident.incidentlist.IncidentListFragment;
import org.unicef.rapidreg.utils.Utils;

import javax.inject.Inject;

import static org.unicef.rapidreg.IntentSender.BUNDLE_EXTRA;
import static org.unicef.rapidreg.service.RecordService.AUDIO_FILE_PATH;

public class IncidentActivity extends RecordActivity implements BaseView {

    public static final String TAG = IncidentActivity.class.getSimpleName();

    @Inject
    IncidentPresenter incidentPresenter;

    @NonNull
    @Override
    public IncidentPresenter createPresenter() {
        return incidentPresenter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        super.onCreate(savedInstanceState);

        setNavSelectedMenu(R.id.nav_incident, incidentColor);
        drawer.closeDrawer(GravityCompat.START);

        turnToFeature(IncidentFeature.LIST, null, null);

        if (getIntent() != null && getIntent().getBundleExtra(BUNDLE_EXTRA) != null) {
            if (!incidentPresenter.isFormReady()) {
                showQuitDialog(R.id.nav_cases);
                return;
            }
            Bundle extra = getIntent().getBundleExtra(BUNDLE_EXTRA);
            turnToFeature(IncidentFeature.ADD_MINI, extra, null);
        }
    }

    @Override
    protected void processBackButton() {
        if (currentFeature.isListMode()) {
            logOut();
        } else if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_incident);
        } else {
            Utils.clearAudioFile(AUDIO_FILE_PATH);
            turnToFeature(IncidentFeature.LIST, null, null);
        }
    }

    @Override
    protected void navIncidentAction() {
        if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_incident);
        } else {
            Utils.clearAudioFile(AUDIO_FILE_PATH);
            turnToFeature(IncidentFeature.LIST, null, null);
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

    protected void navCaseAction() {
        if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_cases);
        } else {
            Utils.clearAudioFile(AUDIO_FILE_PATH);
            intentSender.showCasesActivity(this, true, false);
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
                            case R.id.nav_incident:
                                turnToFeature(IncidentFeature.LIST, null, null);
                                break;
                            case R.id.nav_cases:
                                intentSender.showCasesActivity(IncidentActivity.this, true, false);
                                break;
                            case R.id.nav_tracing:
                                intentSender.showTracingActivity(IncidentActivity.this, true);
                                break;
                            case R.id.nav_sync:
                                intentSender.showSyncActivity(IncidentActivity.this, true);
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
        if (!incidentPresenter.isFormReady()) {
            showSyncFormDialog(getResources().getString(R.string.child_incident));
            return;
        }
        turnToFeature(IncidentFeature.SEARCH, null, null);
    }

    @Override
    protected void save() {
        SaveIncidentEvent event = new SaveIncidentEvent();
        EventBus.getDefault().postSticky(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void sendSyncFormEvent() {
        EventBus.getDefault().postSticky(new LoadGBVIncidentFormEvent(PrimeroAppConfiguration
                .getCookie()));
    }

    @Override
    protected RecordListFragment getRecordListFragment() {
        return (IncidentListFragment) getSupportFragmentManager()
                .findFragmentByTag(IncidentListFragment.class.getSimpleName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void onNeedLoadGBVIncidentFormsEvent(final LoadGBVIncidentFormEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        incidentPresenter.loadIncidentForm(PrimeroAppConfiguration.MODULE_ID_GBV);
    }

    @Override
    public void promoteSyncFormsError() {
        Toast.makeText(IncidentActivity.this, R.string.sync_forms_error,
                Toast.LENGTH_SHORT).show();
    }

}
