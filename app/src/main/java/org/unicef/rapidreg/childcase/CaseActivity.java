package org.unicef.rapidreg.childcase;

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
import org.unicef.rapidreg.childcase.caselist.CaseListFragment;
import org.unicef.rapidreg.event.LoadCPCaseFormEvent;
import org.unicef.rapidreg.event.LoadGBVCaseFormEvent;
import org.unicef.rapidreg.event.SaveCaseEvent;
import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.utils.Utils;

import javax.inject.Inject;

import rx.functions.Action1;

public class CaseActivity extends RecordActivity implements CaseView{
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

        boolean showAddPage = getIntent().getBooleanExtra(IntentSender.SHOW_ADD_PAGE, false);
        turnToFeature(showAddPage ? CaseFeature.ADD_MINI : CaseFeature.LIST, null, null);
    }

    @Override
    protected void processBackButton() {
        if (currentFeature.isListMode()) {
            logOut();
        } else if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_cases);
        } else {
            CaseService.clearAudioFile();
            turnToFeature(CaseFeature.LIST, null, null);
        }
    }

    @Override
    protected void navCaseAction() {
        if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_cases);
        } else {
            CaseService.clearAudioFile();
            turnToFeature(CaseFeature.LIST, null, null);
        }
    }

    @Override
    protected void navTracingAction() {
        if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_tracing);
        } else {
            CaseService.clearAudioFile();
            intentSender.showTracingActivity(this);
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
                        CaseService.clearAudioFile();
                        switch (clickedButton) {
                            case R.id.nav_cases:
                                turnToFeature(CaseFeature.LIST, null, null);
                                break;
                            case R.id.nav_tracing:
                                intentSender.showTracingActivity(CaseActivity.this);
                                break;
                            case R.id.nav_sync:
                                intentSender.showSyncActivity(CaseActivity.this);
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
        CaseListFragment listFragment = (CaseListFragment) getSupportFragmentManager()
                .findFragmentByTag(CaseListFragment.class.getSimpleName());
        listFragment.toggleMode(textAreaState.isDetailShow());
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
        casePresenter.loadCaseForm(cookie, moduleId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void onNeedLoadCPCaseFormsEvent(final LoadCPCaseFormEvent event) {
        EventBus.getDefault().removeStickyEvent(event);

        String moduleId = RecordConfiguration.MODULE_ID_CP;
        String cookie = event.getCookie();
        casePresenter.loadCaseForm(cookie, moduleId);
    }

    @Override
    public void promoteSyncCaseFail() {
        Toast.makeText(CaseActivity.this, R.string.sync_case_forms_error, Toast.LENGTH_SHORT).show();
    }
}
