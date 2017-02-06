package org.unicef.rapidreg.sync;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.FormRemoteService;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public abstract class BaseSyncPresenter extends MvpBasePresenter<SyncView> {
    protected Context context;

    protected CaseService caseService;
    private CaseFormService caseFormService;

    protected FormRemoteService formRemoteService;

    protected int numberOfSuccessfulUploadedRecords;
    protected int totalNumberOfUploadRecords;

    protected boolean isSyncing;

    private List<Case> cases;

    public BaseSyncPresenter(Context context, CaseService caseService, CaseFormService caseFormService,
                             FormRemoteService formRemoteService) {
        this.context = context;
        this.caseService = caseService;
        cases = caseService.getAll();
        this.caseFormService = caseFormService;
        this.formRemoteService = formRemoteService;
    }

    @Override
    public void attachView(SyncView view) {
        super.attachView(view);
        if (isViewAttached()) {
            getView().setNotSyncedRecordNumber(totalNumberOfUploadRecords);
        }
    }

    public void tryToSync() {
        if (isViewAttached()) {
            getView().showAttemptSyncDialog();
        }
    }

    public void execSync() {
        if (!isViewAttached()) {
            return;
        }
        try {
            getView().disableSyncButton();
            initSyncRecordNumber();
            upLoadCases(cases);
        } catch (Throwable t) {
            syncFail(t);
        }
    }

    protected void initSyncRecordNumber() {
        numberOfSuccessfulUploadedRecords = 0;
        totalNumberOfUploadRecords = 0;
        totalNumberOfUploadRecords += countNumber(cases);
        totalNumberOfUploadRecords += countNumber(getTracings());
        totalNumberOfUploadRecords += countNumber(getIncidents());
    }

    private int countNumber(List<? extends RecordModel> recordModels) {
        int initNumber = 0;
        for (RecordModel recordModel : recordModels) {
            if (!recordModel.isSynced()) {
                initNumber++;
            }
        }
        return initNumber;
    }

    public void attemptCancelSync() {
        if (isViewAttached()) {
            getView().showSyncCancelConfirmDialog();
        }
    }

    public void cancelSync() {
        isSyncing = false;
    }

    protected void increaseSyncNumber() {
        numberOfSuccessfulUploadedRecords += 1;
    }

    protected List<Tracing> getTracings() {
        return Collections.EMPTY_LIST;
    }

    protected List<Incident> getIncidents() {
        return Collections.EMPTY_LIST;
    }

    protected abstract void upLoadCases(List<Case> cases);

    public void downloadCaseForm(ProgressDialog loadingDialog, String moduleId) {
        formRemoteService.getCaseForm(PrimeroAppConfiguration.getCookie(), PrimeroAppConfiguration.getDefaultLanguage
                (), true, PrimeroAppConfiguration.PARENT_CASE, moduleId)
                .subscribe(caseFormJson -> {
                            CaseForm caseForm = new CaseForm(new Blob(new Gson().toJson(caseFormJson).getBytes()));
                            caseForm.setModuleId(PrimeroAppConfiguration.MODULE_ID_CP);
                            caseFormService.saveOrUpdate(caseForm);
                        },
                        throwable -> {
                            loadingDialog.dismiss();
                            syncFail(throwable);
                        },
                        () -> downloadSecondFormByModule());
    }

    protected abstract void downloadSecondFormByModule();


    protected void syncPullFormSuccessfully() {
        if (getView() != null && isViewAttached()) {
            getView().showSyncPullFormSuccessMessage();
            getView().hideSyncProgressDialog();
            getView().enableSyncButton();
        }
    }

    protected void updateRecordSynced(RecordModel record, boolean synced) {
        record.setSynced(synced);
        record.update();
    }

    protected void syncFail(Throwable throwable) {
        if (getView() == null || !isViewAttached()) {
            return;
        }
        Throwable cause = throwable.getCause();
        if (throwable instanceof SocketTimeoutException || cause instanceof
                SocketTimeoutException) {
            getView().showRequestTimeoutSyncErrorMessage();
        } else if (throwable instanceof ConnectException || cause instanceof ConnectException
                || throwable instanceof IOException || cause instanceof IOException) {
            getView().showServerNotAvailableSyncErrorMessage();
        } else {
            getView().showSyncErrorMessage();
        }
        getView().hideSyncProgressDialog();
        updateDataViews();
        getView().enableSyncButton();
    }

    protected void setProgressIncrease() {
        if (getView() != null && isViewAttached()) {
            getView().setProgressIncrease();
        }
    }

    private void updateDataViews() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        String currentDateTime = sdf.format(new Date());
        int numberOfFailedUploadedCases = totalNumberOfUploadRecords -
                numberOfSuccessfulUploadedRecords;

        getView().setDataViews(currentDateTime, String.valueOf(numberOfSuccessfulUploadedRecords)
                , String.valueOf
                        (numberOfFailedUploadedCases));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (context);
        sharedPreferences.edit().putString("syncStatisticData", new Gson().toJson(new
                SyncStatisticData
                (currentDateTime, numberOfSuccessfulUploadedRecords, numberOfFailedUploadedCases)
        )).apply();
    }

    protected void syncUploadSuccessfully() {
        if (getView() != null && isViewAttached()) {
            updateDataViews();
            getView().showSyncUploadSuccessMessage();
            getView().hideSyncProgressDialog();
        }
    }

    protected void syncDownloadSuccessfully() {
        if (getView() != null && isViewAttached()) {
            updateDataViews();
            getView().showSyncDownloadSuccessMessage();
            getView().hideSyncProgressDialog();
        }
    }
}
