package org.unicef.rapidreg.tracing;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.BaseView;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListFragment;
import org.unicef.rapidreg.event.LoadTracingFormEvent;
import org.unicef.rapidreg.event.SaveTracingEvent;
import org.unicef.rapidreg.tracing.tracinglist.TracingListFragment;
import org.unicef.rapidreg.utils.Utils;
import org.unicef.rapidreg.widgets.dialog.MessageDialog;

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
    public void sendSyncFormEvent() {
        EventBus.getDefault().postSticky(new LoadTracingFormEvent(PrimeroAppConfiguration.getCookie()));
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
        MessageDialog messageDialog = new MessageDialog(this);
        messageDialog.setTitle((R.string.quit));
        messageDialog.setMessage(R.string.quit_without_saving);
        messageDialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        turnToFeature(TracingFeature.SEARCH, null, null);
    }

    @Override
    protected void save() {
        SaveTracingEvent event = new SaveTracingEvent();
        EventBus.getDefault().postSticky(event);
    }

    @Override
    public void promoteSyncFormsError() {
        Utils.showMessageByToast(this, R.string.sync_forms_error, Toast.LENGTH_SHORT);
    }
}
