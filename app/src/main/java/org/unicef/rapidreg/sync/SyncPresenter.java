package org.unicef.rapidreg.sync;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.network.SyncService;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SyncPresenter extends MvpBasePresenter<SyncView> {

    private Context context;

    private SyncService syncService;

    public SyncPresenter() {
    }

    public SyncPresenter(Context context) {
        this.context = context;
        try {
            syncService = new SyncService(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doSync() {
        if (isViewAttached()) {
            getView().showSyncProgressDialog();

            syncService.getAllCasesRx(PrimeroConfiguration.getCookie(), "en", false)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Response<ArrayList<Map<String, Object>>>>() {
                @Override
                public void call(Response<ArrayList<Map<String, Object>>> arrayListResponse) {
                    ArrayList<Map<String, Object>> t = arrayListResponse.body();
                    return;
                }
            });

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
