package org.unicef.rapidreg.sync;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface SyncView extends MvpView {

    public void showSyncProgressDialog();

    public void hideSyncProgressDialog();

    public void showSyncCancelConfirmDialog();

    public void showSyncErrorMessage();

    public void showSyncSuccessMessage();

    public void setDataViews(String syncDate, String hasSyncAmount, String notSyncAmount);
}
