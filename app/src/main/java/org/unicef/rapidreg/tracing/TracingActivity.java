package org.unicef.rapidreg.tracing;

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
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.event.LoadTracingFormEvent;
import org.unicef.rapidreg.event.SaveTracingEvent;
import org.unicef.rapidreg.forms.TracingFormRoot;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.tracing.tracinglist.TracingListFragment;
import org.unicef.rapidreg.utils.Utils;

import javax.inject.Inject;

import rx.functions.Action1;

public class TracingActivity extends RecordActivity {
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

        navigationView.setCheckedItem(R.id.nav_tracing);
        navigationView.setItemTextColor(tracingColor);

        boolean showAddPage = getIntent().getBooleanExtra(IntentSender.SHOW_ADD_PAGE, false);
        turnToFeature(showAddPage ? TracingFeature.ADD_MINI : TracingFeature.LIST, null, null);
    }

    @Override
    protected void processBackButton() {
        if (currentFeature.isListMode()) {
            logOut();
        } else if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_tracing);
        } else {
            TracingService.clearAudioFile();
            turnToFeature(TracingFeature.LIST, null, null);
        }
    }

    @Override
    protected void navCaseAction() {
        if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_cases);
        } else {
            TracingService.clearAudioFile();
            intentSender.showCasesActivity(this, false);
        }
    }

    @Override
    protected void navTracingAction() {
        if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_tracing);
        } else {
            TracingService.clearAudioFile();
            turnToFeature(TracingFeature.LIST, null, null);
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
                        TracingService.clearAudioFile();
                        switch (clickedButton) {
                            case R.id.nav_tracing:
                                turnToFeature(TracingFeature.LIST, null, null);
                                break;
                            case R.id.nav_cases:
                                intentSender.showCasesActivity(TracingActivity.this, false);
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

        Utils.changeDialogDividerColor(this, dialog);
    }

    @Override
    protected void showHideDetail() {
        textAreaState = textAreaState.getNextState();

        showHideMenu.setIcon(textAreaState.getResId());
        TracingListFragment listFragment = (TracingListFragment) getSupportFragmentManager()
                .findFragmentByTag(TracingListFragment.class.getSimpleName());
        listFragment.toggleMode(textAreaState.isDetailShow());
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

        tracingPresenter.loadTracingForm(event.getCookie())
                .subscribe(new Action1<TracingFormRoot>() {
                    @Override
                    public void call(TracingFormRoot tracingFormRoot) {
                        tracingPresenter.saveForm(tracingFormRoot);
                        setTracingFormSyncFail(false);
                        Log.i(TAG, "load tracing form successfully");

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        setTracingFormSyncFail(true);
                        Toast.makeText(TracingActivity.this, R.string.sync_tracing_forms_error, Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }
}
