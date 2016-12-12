package org.unicef.rapidreg.incident;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.IntentSender;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.RecordConfiguration;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.event.LoadGBVIncidentFormEvent;
import org.unicef.rapidreg.event.SaveIncidentEvent;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.incident.incidentlist.IncidentListFragment;
import org.unicef.rapidreg.service.IncidentService;
import org.unicef.rapidreg.utils.Utils;

import javax.inject.Inject;

import rx.functions.Action1;

public class IncidentActivity extends RecordActivity {

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

        navigationView.setCheckedItem(R.id.nav_incident);
        navigationView.setItemTextColor(incidentColor);

        boolean showAddPage = getIntent().getBooleanExtra(IntentSender.SHOW_ADD_PAGE, false);
        turnToFeature(showAddPage ? IncidentFeature.ADD_MINI : IncidentFeature.LIST, null, null);
    }

    @Override
    protected void processBackButton() {
        if (currentFeature.isListMode()) {
            logOut();
        } else if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_incident);
        } else {
            IncidentService.clearAudioFile();
            turnToFeature(IncidentFeature.LIST, null, null);
        }
    }

    @Override
    protected void navIncidentAction() {
        if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_incident);
        } else {
            IncidentService.clearAudioFile();
            turnToFeature(IncidentFeature.LIST, null, null);
        }
    }

    @Override
    protected void navTracingAction() {
        if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_tracing);
        } else {
            IncidentService.clearAudioFile();
            intentSender.showTracingActivity(this);
        }
    }

    protected void navCaseAction() {
        if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_cases);
        } else {
            IncidentService.clearAudioFile();
            intentSender.showCasesActivity(this, false);
        }
    }

    @Override
    protected void showQuitDialog(final int clickedButton) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.quit)
                .setMessage(R.string.quit_without_saving)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        IncidentService.clearAudioFile();
                        switch (clickedButton) {
                            case R.id.nav_incident:
                                turnToFeature(IncidentFeature.LIST, null, null);
                                break;
                            case R.id.nav_cases:
                                intentSender.showCasesActivity(IncidentActivity.this, false);
                                break;
                            case R.id.nav_tracing:
                                intentSender.showTracingActivity(IncidentActivity.this);
                                break;
                            case R.id.nav_sync:
                                intentSender.showSyncActivity(IncidentActivity.this);
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
    protected void showHideDetail() {
        textAreaState = textAreaState.getNextState();

        showHideMenu.setIcon(textAreaState.getResId());
        IncidentListFragment listFragment = (IncidentListFragment) getSupportFragmentManager()
                .findFragmentByTag(IncidentListFragment.class.getSimpleName());
        listFragment.toggleMode(textAreaState.isDetailShow());
    }

    @Override
    protected void search() {
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void onNeedLoadGBVIncidentFormsEvent(final LoadGBVIncidentFormEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        String moduleId = RecordConfiguration.MODULE_ID_GBV;
        String cookie = event.getCookie();
        loadIncidentFormByModuleId(moduleId, cookie);
    }

    private void loadIncidentFormByModuleId(final String moduleId, String cookie) {
        incidentPresenter.loadIncidentForm(cookie, moduleId)
                .subscribe(new Action1<IncidentTemplateForm>() {
                    @Override
                    public void call(IncidentTemplateForm incidentForm) {
                        incidentPresenter.saveForm(incidentForm, moduleId);
                        setFormSyncFail(false);
                        Log.i(TAG, "load incident form successfully");

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        setFormSyncFail(true);
                        Toast.makeText(IncidentActivity.this, R.string.sync_incident_forms_error,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

}
