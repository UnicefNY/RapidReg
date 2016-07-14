package org.unicef.rapidreg.tracing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.RecordActivity;
import org.unicef.rapidreg.event.SaveTracingEvent;
import org.unicef.rapidreg.service.TracingService;

public class TracingActivity extends RecordActivity {
    public static final String TAG = TracingActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        turnToFeature(TracingFeature.LIST, null);
    }

    @Override
    protected void processBackButton() {
        if (currentFeature.isListMode()) {
            logOut(this);
        } else if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_tracing);
        } else {
            TracingService.clearAudioFile();
            turnToFeature(TracingFeature.LIST, null);
        }
    }

    @Override
    protected void navCaseAction() {
        if (currentFeature.isDetailMode()) {
            showQuitDialog(R.id.nav_tracing);
        } else {
            TracingService.clearAudioFile();
            intentSender.showCasesActivity(this, null, false);
        }
    }

    @Override
    protected void navTracingAction() {
        if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_tracing);
        } else {
            TracingService.clearAudioFile();
            turnToFeature(TracingFeature.LIST, null);
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
                        TracingService.clearAudioFile();
                        switch (clickedButton) {
                            case R.id.nav_tracing:
                                turnToFeature(TracingFeature.LIST, null);
                                break;
                            case R.id.nav_sync:
                                intentSender.showSyncActivity(TracingActivity.this);
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
    protected void search() {
        turnToFeature(TracingFeature.SEARCH, null);
    }

    @Override
    protected void save() {
        clearFocusToMakeLastFieldSaved();
        SaveTracingEvent event = new SaveTracingEvent();
        EventBus.getDefault().postSticky(event);
        turnToFeature(TracingFeature.LIST, null);
    }

    private void clearFocusToMakeLastFieldSaved() {
//        TracingRegisterWrapperFragment fragment =
//                (TracingRegisterWrapperFragment) getSupportFragmentManager()
//                        .findFragmentByTag(TracingRegisterWrapperFragment.class.getSimpleName());
//
//        if (fragment != null) {
//            fragment.clearFocus();
//        }
    }
}
