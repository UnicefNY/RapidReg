package org.unicef.rapidreg.sync;

import android.app.ProgressDialog;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface SyncView extends MvpView {
    void hideSyncProgressDialog();

    void showSyncCancelConfirmDialog();

    void showSyncErrorMessage();

    void showSyncDownloadSuccessMessage();

    void setDataViews(String syncDate, String hasSyncAmount, String notSyncAmount);

    void setProgressMax(int max);

    void disableSyncButton();

    void enableSyncButton();

    void setNotSyncedRecordNumber(int recordNumber);

    void setProgressIncrease();

    void showAttemptSyncDialog();

    void showSyncUploadSuccessMessage();

    void showSyncPullFormSuccessMessage();

    void showRequestTimeoutSyncErrorMessage();

    void showServerNotAvailableSyncErrorMessage();

    void showDownloadingCasesSyncProgressDialog();

    void showDownloadingTracingsSyncProgressDialog();

    void showDownloadingIncidentsSyncProgressDialog();

    void showUploadCasesSyncProgressDialog();

    ProgressDialog showFetchingCaseAmountLoadingDialog();

    ProgressDialog showFetchingTracingAmountLoadingDialog();

    ProgressDialog showFetchingFormLoadingDialog();

    ProgressDialog showFetchingIncidentAmountLoadingDialog();

}
