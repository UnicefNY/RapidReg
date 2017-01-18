package org.unicef.rapidreg.sync;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

public abstract class BaseSyncPresenter extends MvpBasePresenter<SyncView> {
    public abstract void tryToSync();

    public abstract void execSync();

    public abstract void attemptCancelSync();

    public abstract void cancelSync();
}
