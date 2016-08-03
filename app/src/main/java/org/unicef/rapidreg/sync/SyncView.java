package org.unicef.rapidreg.sync;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface SyncView extends MvpView {

    void showSyncProgressDialog(String title);

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

    void setSyncProgressDialogTitle(String title);

    void showSyncUploadSuccessMessage();
}
