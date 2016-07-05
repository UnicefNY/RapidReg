package org.unicef.rapidreg.sync;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

public class SyncPresenter extends MvpBasePresenter<SyncView> {

    private Context context;

    public SyncPresenter() {}

    public SyncPresenter(Context context) {
        this.context = context;
    }

    public void doSync() {
        if (isViewAttached()) {
            getView().showSyncProgressDialog();
            try {
                startUpLoadCases();
                startDownLoadCaseForms();
                startDownLoadCases();
                doSyncSuccessAction();
            } catch (Exception e) {
                doSyncFailureAction();
            }
        }
    }

    public void doAttemptCancelSync() {
        getView().showSyncCancelConfirmDialog();
    }

    public void doCancelSync() {

    }

    private void startUpLoadCases() {

    }

    private void startDownLoadCaseForms() {

    }

    private void startDownLoadCases() {

    }

    private void doSyncSuccessAction() {
        updateDataViews();
        getView().showSyncSuccessMessage();
        getView().hideSyncProgressDialog();
    }

    private void doSyncFailureAction() {
        updateDataViews();
        getView().showSyncErrorMessage();
        getView().hideSyncProgressDialog();
    }

    private void updateDataViews() {

    }
}
