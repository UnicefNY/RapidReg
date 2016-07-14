package org.unicef.rapidreg.childcase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.RecordActivity;
import org.unicef.rapidreg.event.SaveCaseEvent;
import org.unicef.rapidreg.service.CaseService;

public class CaseActivity extends RecordActivity {
    public static final String TAG = CaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navigationView.setCheckedItem(R.id.nav_cases);
        navigationView.setItemTextColor(caseColor);
        turnToFeature(CaseFeature.LIST, null);
    }

    @Override
    protected void processBackButton() {
        if (currentFeature.isListMode()) {
            logOut(this);
        } else if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_cases);
        } else {
            CaseService.clearAudioFile();
            turnToFeature(CaseFeature.LIST, null);
        }
    }

    @Override
    protected void navCaseAction() {
        if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_cases);
        } else {
            CaseService.clearAudioFile();
            turnToFeature(CaseFeature.LIST, null);
        }
    }

    @Override
    protected void navTracingAction() {
        if (currentFeature.isDetailMode()) {
            showQuitDialog(R.id.nav_cases);
        } else {
            CaseService.clearAudioFile();
            intentSender.showTracingActivity(this);
        }
    }

    @Override
    protected void showQuitDialog(final int clickedButton) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.quit)
                .setMessage(R.string.quit_without_saving)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CaseService.clearAudioFile();
                        switch (clickedButton) {
                            case R.id.nav_cases:
                                turnToFeature(CaseFeature.LIST, null);
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
        turnToFeature(CaseFeature.SEARCH, null);
    }

    @Override
    protected void save() {
        clearFocusToMakeLastFieldSaved();
        SaveCaseEvent event = new SaveCaseEvent();
        EventBus.getDefault().postSticky(event);
        turnToFeature(CaseFeature.LIST, null);
    }

    private void clearFocusToMakeLastFieldSaved() {
        CaseRegisterWrapperFragment fragment = (CaseRegisterWrapperFragment) getSupportFragmentManager()
                .findFragmentByTag(CaseRegisterWrapperFragment.class.getSimpleName());

        if (fragment != null) {
            fragment.clearFocus();
        }
    }
}
