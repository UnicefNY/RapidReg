package org.unicef.rapidreg.incident;

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
import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListFragment;
import org.unicef.rapidreg.event.LoadGBVIncidentFormEvent;
import org.unicef.rapidreg.event.SaveIncidentEvent;
import org.unicef.rapidreg.incident.incidentlist.IncidentListFragment;
import org.unicef.rapidreg.utils.Utils;
import org.unicef.rapidreg.widgets.dialog.MessageDialog;

import javax.inject.Inject;

import static org.unicef.rapidreg.IntentSender.BUNDLE_EXTRA;
import static org.unicef.rapidreg.service.CaseService.CASE_ID;
import static org.unicef.rapidreg.service.IncidentService.INCIDENT_ID;
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
                showSyncFormDialog(getResources().getString(R.string.child_incident));
                return;
            }

            Bundle extra = getIntent().getBundleExtra(BUNDLE_EXTRA);
            Feature turnFeature = extra.containsKey(CASE_ID) ? IncidentFeature.ADD_MINI :
                    extra.containsKey(INCIDENT_ID) ? IncidentFeature.DETAILS_MINI : IncidentFeature.LIST;

            turnToFeature(turnFeature, extra, null);
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
        MessageDialog messageDialog = new MessageDialog(this);
        messageDialog.setTitle(R.string.quit);
        messageDialog.setMessage(R.string.quit_without_saving);
        messageDialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    public void sendSyncFormEvent() {
        EventBus.getDefault().postSticky(new LoadGBVIncidentFormEvent(PrimeroAppConfiguration
                .getCookie()));
    }

    @Override
    protected RecordListFragment getRecordListFragment() {
        return (IncidentListFragment) getSupportFragmentManager()
                .findFragmentByTag(IncidentListFragment.class.getSimpleName());
    }

    @Override
    public void promoteSyncFormsError() {
        Utils.showMessageByToast(this, R.string.sync_forms_error, Toast.LENGTH_SHORT);
    }

}
