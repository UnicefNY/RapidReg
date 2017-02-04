package org.unicef.rapidreg.tracing;

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
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.BaseAlertDialog;
import org.unicef.rapidreg.base.BaseView;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListFragment;
import org.unicef.rapidreg.event.LoadTracingFormEvent;
import org.unicef.rapidreg.event.SaveTracingEvent;
import org.unicef.rapidreg.tracing.tracinglist.TracingListFragment;
import org.unicef.rapidreg.utils.Utils;

import javax.inject.Inject;

import static org.unicef.rapidreg.service.RecordService.AUDIO_FILE_PATH;

public class TracingActivity extends RecordActivity implements BaseView {
    public static final String TAG = TracingActivity.class.getSimpleName();

    @Inject
    TracingPresenter tracingPresenter;

    @NonNull
    @Override
    public TracingPresenter createPresenter() {
        return tracingPresenter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        super.onCreate(savedInstanceState);

        setNavSelectedMenu(R.id.nav_tracing, tracingColor);
        drawer.closeDrawer(GravityCompat.START);

        turnToFeature(TracingFeature.LIST, null, null);
    }

    @Override
    protected RecordListFragment getRecordListFragment() {
        return (TracingListFragment) getSupportFragmentManager()
                .findFragmentByTag(TracingListFragment.class.getSimpleName());

    }

    @Override
    protected void processBackButton() {
        if (currentFeature.isListMode()) {
            logOut();
        } else if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_tracing);
        } else {
            Utils.clearAudioFile(AUDIO_FILE_PATH);
            turnToFeature(TracingFeature.LIST, null, null);
        }
    }

    @Override
    protected void navCaseAction() {
        if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_cases);
        } else {
            Utils.clearAudioFile(AUDIO_FILE_PATH);
            intentSender.showCasesActivity(this, true, false);
        }
    }

    @Override
    protected void navTracingAction() {
        setShowHideSwitcherToShowState();
        if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_tracing);
        } else {
            Utils.clearAudioFile(AUDIO_FILE_PATH);
            turnToFeature(TracingFeature.LIST, null, null);
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
                            case R.id.nav_tracing:
                                turnToFeature(TracingFeature.LIST, null, null);
                                break;
                            case R.id.nav_cases:
                                intentSender.showCasesActivity(TracingActivity.this, true, false);
                                break;
                            case R.id.nav_incident:
                                intentSender.showIncidentActivity(TracingActivity.this, true);
                                break;
                            case R.id.nav_sync:
                                intentSender.showSyncActivity(TracingActivity.this, true);
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
        turnToFeature(TracingFeature.SEARCH, null, null);
    }

    @Override
    protected void save() {
        SaveTracingEvent event = new SaveTracingEvent();
        EventBus.getDefault().postSticky(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void onNeedLoadFormsEvent(final LoadTracingFormEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        tracingPresenter.loadTracingForm();
    }

    @Override
    public void promoteSyncFormsError() {
        Toast.makeText(TracingActivity.this, R.string.sync_forms_error, Toast.LENGTH_SHORT).show();
    }
}
