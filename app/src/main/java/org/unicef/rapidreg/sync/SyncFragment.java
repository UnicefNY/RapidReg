package org.unicef.rapidreg.sync;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.BaseProgressDialog;
import org.unicef.rapidreg.injection.component.DaggerFragmentComponent;
import org.unicef.rapidreg.injection.component.FragmentComponent;
import org.unicef.rapidreg.injection.module.FragmentModule;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.utils.TextUtils;
import org.unicef.rapidreg.utils.Utils;
import org.unicef.rapidreg.widgets.dialog.MessageDialog;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SyncFragment extends MvpFragment<SyncView, BaseSyncPresenter> implements SyncView {
    private BaseProgressDialog syncProgressDialog;

    @BindView(R.id.btn_sync)
    Button syncButton;

    @BindView(R.id.tv_produce_cases)
    TextView tvProduceCases;

    @BindString(R.string.produce_cases_successfully_msg)
    String produceCasesSuccessfullyMsg;

    @BindView(R.id.last_sync_time)
    TextView lastSyncTime;

    @BindView(R.id.record_count_for_last_sync)
    TextView countOfLastSync;

    @BindView(R.id.record_count_for_not_sync)
    TextView countOfNotSync;

    @BindString(R.string.start_sync_message)
    String startSyncMessage;
    @BindString(R.string.confirm_cancel_sync_message)
    String confirmCancelSyncMessage;
    @BindString(R.string.deny_button_text)
    String denyButtonText;
    @BindString(R.string.confirm_button_text)
    String confirmButtonText;
    @BindString(R.string.cancel_button_text)
    String cancelButtonText;
    @BindString(R.string.sync_upload_success_message)
    String syncUploadSuccessMessage;
    @BindString(R.string.sync_download_success_message)
    String syncDownloadSuccessMessage;
    @BindString(R.string.sync_pull_form_success_message)
    String syncDownloadFormSuccessMessage;
    @BindString(R.string.try_to_sync_message)
    String tryToSyncMessage;
    @BindString(R.string.not_now_button_text)
    String notNowButtonText;
    @BindString(R.string.stop_sync_button_text)
    String stopSyncButtonText;
    @BindString(R.string.continue_sync_button_text)
    String continueSyncButtonText;

    @Inject
    CPSyncPresenter cpSyncPresenter;

    @Inject
    GBVSyncPresenter gbvSyncPresenter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        View view = inflater.inflate(R.layout.fragment_sync, container, false);
        ButterKnife.bind(this, view);
        initView();

        return view;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager conMgr = ((ConnectivityManager) getActivity().getSystemService(Context
                .CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public void disableSyncButton() {
        syncButton.setEnabled(false);
        syncButton.setBackgroundResource(R.drawable.sync_add_button_without_network);
    }

    public void enableSyncButton() {
        syncButton.setEnabled(true);
        syncButton.setBackgroundResource(R.drawable.sync_add_button);
    }

    private void initView() {
        SyncStatisticData syncData = PrimeroApplication.getAppRuntime().loadSyncData();
        if (TextUtils.isEmpty(syncData.getLastSyncData())) {
            syncData.setLastSyncData(getResources().getString(R.string.not_sync_promote));
        }
        setDataViews(syncData.getLastSyncData(), syncData.getSyncedNumberAsString(),
                syncData.getNotSyncedNumberAsString());
        tvProduceCases.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public BaseSyncPresenter createPresenter() {
        User.Role roleType = PrimeroAppConfiguration.getCurrentUser().getRoleType();
        if (User.Role.GBV == roleType) {
            return gbvSyncPresenter;
        }
        return cpSyncPresenter;
    }

    @OnClick(R.id.btn_sync)
    public void onSyncClick() {
        if (!isNetworkAvailable()) {
            Utils.showMessageByToast(getActivity(), R.string.network_not_available, Toast.LENGTH_SHORT);
            return;
        }
        presenter.tryToSync();
    }


    @Override
    public void hideSyncProgressDialog() {
        if (syncProgressDialog != null) {
            syncProgressDialog.dismiss();
        }
    }

    @Override
    public void showSyncCancelConfirmDialog() {
        MessageDialog messageDialog = new MessageDialog(getActivity());
        messageDialog.setMessage(confirmCancelSyncMessage);
        messageDialog.setCancelable(false);
        messageDialog.setPositiveButton(R.string.stop_sync_button_text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.cancelSync();
                messageDialog.dismiss();
            }
        });
        messageDialog.setNegativeButton(R.string.continue_sync_button_text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncProgressDialog.show();
                messageDialog.dismiss();
            }
        });
        messageDialog.show();
    }

    @Override
    public void setDataViews(String syncDate, String hasSyncAmount, String notSyncAmount) {
        lastSyncTime.setText(syncDate);
        countOfLastSync.setText(hasSyncAmount);
        countOfNotSync.setText(notSyncAmount);
    }

    @Override
    public void setProgressMax(int max) {
        if (syncProgressDialog.isShowing()) {
            syncProgressDialog.setProgress(0);
            syncProgressDialog.setMax(max);
        }
    }

    @Override
    public void setNotSyncedRecordNumber(int recordNumber) {
        countOfNotSync.setText(String.valueOf(recordNumber));
    }

    @Override
    public void setProgressIncrease() {
        if (syncProgressDialog.isShowing()) {
            syncProgressDialog.incrementProgressBy(1);
        }
    }

    @Override
    public void showAttemptSyncDialog() {
        MessageDialog messageDialog = new MessageDialog(getActivity());
        messageDialog.setMessage(tryToSyncMessage);
        messageDialog.setCancelable(false);
        messageDialog.setPositiveButton(R.string.confirm_button_text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.execSync();
                messageDialog.dismiss();
            }
        });
        messageDialog.setNegativeButton(R.string.not_now_button_text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageDialog.dismiss();
            }
        });
        messageDialog.show();
    }

    @Override
    public void showSyncUploadSuccessMessage() {
        Utils.showMessageByToast(getActivity(), syncUploadSuccessMessage, Toast.LENGTH_SHORT);
    }

    @Override
    public void showSyncPullFormSuccessMessage() {
        Utils.showMessageByToast(getActivity(), syncDownloadFormSuccessMessage, Toast.LENGTH_SHORT);
    }

    @Override
    public void showRequestTimeoutSyncErrorMessage() {
        Utils.showMessageByToast(getActivity(), R.string.sync_request_time_out_error_message, Toast
                .LENGTH_SHORT);
    }

    @Override
    public void showServerNotAvailableSyncErrorMessage() {
        Utils.showMessageByToast(getActivity(), R.string.sync_server_not_available_error_message, Toast
                .LENGTH_SHORT);
    }

    @Override
    public void showDownloadingCasesSyncProgressDialog() {
        showSyncProgressDialog(getResources().getString(R.string.downloading_cases_sync_progress_msg));
    }

    @Override
    public void showDownloadingTracingsSyncProgressDialog() {
        showSyncProgressDialog(getResources().getString(R.string.downloading_tracings_sync_progress_msg));
    }

    @Override
    public void showDownloadingIncidentsSyncProgressDialog() {
        showSyncProgressDialog(getResources().getString(R.string.downloading_incidents_sync_progress_msg));
    }

    @Override
    public ProgressDialog showFetchingCaseAmountLoadingDialog() {
        BaseProgressDialog fetchingCaseProgressDialog = new BaseProgressDialog(getActivity(), R.style.ProgressDialogTheme);
        fetchingCaseProgressDialog.setMessage(getResources().getString(R.string.fetching_case_amount_msg));
        fetchingCaseProgressDialog.setCancelable(false);
        fetchingCaseProgressDialog.show();
        return fetchingCaseProgressDialog;
    }

    @Override
    public ProgressDialog showFetchingTracingAmountLoadingDialog() {
        BaseProgressDialog fetchingTracingProgressDialog = new BaseProgressDialog(getActivity(), R.style.ProgressDialogTheme);
        fetchingTracingProgressDialog.setMessage(getResources().getString(R.string.fetching_tracing_amount_msg));
        fetchingTracingProgressDialog.setCancelable(false);
        fetchingTracingProgressDialog.show();
        return fetchingTracingProgressDialog;
    }

    @Override
    public ProgressDialog showFetchingFormLoadingDialog() {
        BaseProgressDialog fetchingFormProgressDialog = new BaseProgressDialog(getActivity(), R.style.ProgressDialogTheme);
        fetchingFormProgressDialog.setMessage(getResources().getString(R.string.fetching_form_msg));
        fetchingFormProgressDialog.setCancelable(false);
        fetchingFormProgressDialog.show();
        return fetchingFormProgressDialog;
    }

    @Override
    public ProgressDialog showFetchingIncidentAmountLoadingDialog() {
        BaseProgressDialog fetchingIncidentProgressDialog = new BaseProgressDialog(getActivity(), R.style.ProgressDialogTheme);
        fetchingIncidentProgressDialog.setMessage(getResources().getString(R.string.fetching_incident_amount_msg));
        fetchingIncidentProgressDialog.setCancelable(false);
        fetchingIncidentProgressDialog.show();
        return fetchingIncidentProgressDialog;
    }

    @Override
    public void showUploadCasesSyncProgressDialog() {
        showSyncProgressDialog(getResources().getString(R.string.uploading_sync_progress_msg));
    }

    @Override
    public void showSyncDownloadSuccessMessage() {
        Utils.showMessageByToast(getActivity(), syncDownloadSuccessMessage, Toast.LENGTH_SHORT);
    }

    @Override
    public void showSyncErrorMessage() {
        Utils.showMessageByToast(getActivity(), R.string.sync_error_message, Toast.LENGTH_LONG);
    }

    public FragmentComponent getComponent() {
        return DaggerFragmentComponent.builder()
                .applicationComponent(PrimeroApplication.get(getActivity()).getComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
    }

    private void showSyncProgressDialog(String title) {
        syncProgressDialog = new BaseProgressDialog(getActivity(), R.style.ProgressDialogTheme);
        syncProgressDialog.setProgressStyle(BaseProgressDialog.STYLE_HORIZONTAL);
        syncProgressDialog.setMessage(title);
        syncProgressDialog.setCancelable(false);
        syncProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                cancelButtonText,
                (dialog, which) -> {
                    presenter.attemptCancelSync();
                });
        syncProgressDialog.show();
    }
}
