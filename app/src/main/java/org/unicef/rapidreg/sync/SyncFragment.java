package org.unicef.rapidreg.sync;

import android.app.AlertDialog;
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
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hannesdorfmann.mosby.mvp.BuildConfig;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.R;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SyncFragment extends MvpFragment<SyncView, SyncPresenter> implements SyncView {
    private ProgressDialog syncProgressDialog;

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
    @BindString(R.string.sync_error_message)
    String syncErrorMessage;
    @BindString(R.string.try_to_sync_message)
    String tryToSyncMessage;
    @BindString(R.string.not_now_button_text)
    String notNowButtonText;
    @BindString(R.string.stop_sync_button_text)
    String stopSyncButtonText;
    @BindString(R.string.continue_sync_button_text)
    String continueSyncButtonText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sync, container, false);
        ButterKnife.bind(this, view);
        initView();
        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
            disableSyncButton();
        }
        return view;
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String syncStatisticData = sharedPreferences.getString("syncStatisticData", null);
        SyncStatisticData syncData;
        if (syncStatisticData != null) {
            syncData = new Gson().fromJson(syncStatisticData, SyncStatisticData.class);
        } else {
            syncData = new SyncStatisticData();
            syncData.setLastSyncData(getResources().getString(R.string.not_sync_promote));
        }
        setDataViews(syncData.getLastSyncData(), syncData.getSyncedNumberAsString(),
                syncData.getNotSyncedNumberAsString());

        if (!BuildConfig.DEBUG) {
//            tvProduceCases.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public SyncPresenter createPresenter() {
        return new SyncPresenter(getActivity());
    }

    @OnClick(R.id.btn_sync)
    public void onSyncClick() {
        presenter.tryToSync();
    }

    @OnClick(R.id.tv_produce_cases)
    public void onProduceCasesBtnClick() {
        final EditText tvNumber = new EditText(getActivity());
        tvNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        tvNumber.setRawInputType(Configuration.KEYBOARD_12KEY);
        tvNumber.setText("100");
        new AlertDialog.Builder(getActivity())
                .setView(tvNumber)
                .setMessage("Please enter the number.")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.produceCases(Integer.valueOf(tvNumber.getText().toString()));
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void showSyncProgressDialog(String title) {
        syncProgressDialog = new ProgressDialog(getActivity());
        syncProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        syncProgressDialog.setMessage(title);
        syncProgressDialog.setCancelable(false);
        syncProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                cancelButtonText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.attemptCancelSync();
                    }
                });
        syncProgressDialog.show();
    }

    @Override
    public void hideSyncProgressDialog() {
        if (syncProgressDialog != null) {
            syncProgressDialog.dismiss();
        }
    }

    @Override
    public void showSyncCancelConfirmDialog() {
        new AlertDialog.Builder(getActivity())
                .setMessage(confirmCancelSyncMessage)
                .setCancelable(false)
                .setNegativeButton(continueSyncButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        syncProgressDialog.show();
                    }
                })
                .setPositiveButton(stopSyncButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.cancelSync();
                    }
                })
                .show();
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
        new AlertDialog.Builder(getActivity())
                .setMessage(tryToSyncMessage)
                .setCancelable(false)
                .setNegativeButton(notNowButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(confirmButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.execSync();
                    }
                })
                .show();
    }

    @Override
    public void showSyncUploadSuccessMessage() {
        Toast.makeText(getActivity(), syncUploadSuccessMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showSyncDownloadSuccessMessage() {
        Toast.makeText(getActivity(), syncDownloadSuccessMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showSyncErrorMessage() {
        Toast.makeText(getActivity(), syncErrorMessage, Toast.LENGTH_LONG).show();
    }

}
