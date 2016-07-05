package org.unicef.rapidreg.sync;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.R;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SyncFragment extends MvpFragment<SyncView, SyncPresenter> implements SyncView{

    @BindView(R.id.button_sync) Button syncButton;
    @BindView(R.id.last_sync_time) TextView lastSyncTime;
    @BindView(R.id.record_count_for_last_sync) TextView countOfLastSync;
    @BindView(R.id.record_count_for_not_sync) TextView countOfNotSync;
    private ProgressDialog syncProgressDialog;

    @BindString(R.string.start_sync_message) String startSyncMessage;
    @BindString(R.string.confirm_cancel_sync_message) String confirmCancelSyncMessage;
    @BindString(R.string.deny_button_text) String denyButtonText;
    @BindString(R.string.confirm_button_text) String confirmButtonText;
    @BindString(R.string.cancel_button_text) String cancelButtonText;
    @BindString(R.string.sync_success_message) String syncSuccessMessage;
    @BindString(R.string.sync_error_message) String syncErrorMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sync, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public SyncPresenter createPresenter() {
        return new SyncPresenter(getActivity());
    }

    @OnClick(R.id.button_sync)
    public void onSyncClick() {
        presenter.doSync();
    }

    @Override
    public void showSyncProgressDialog() {
        syncProgressDialog = new ProgressDialog(getActivity());
        syncProgressDialog.setMessage(startSyncMessage);
        syncProgressDialog.setCancelable(false);
        syncProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                cancelButtonText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.doAttemptCancelSync();
                    }
                });
        syncProgressDialog.show();
    }

    @Override
    public void hideSyncProgressDialog() {
        syncProgressDialog.dismiss();
    }

    @Override
    public void showSyncCancelConfirmDialog() {
        new AlertDialog.Builder(getActivity())
                .setMessage(confirmCancelSyncMessage)
                .setCancelable(false)
                .setNegativeButton(denyButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        syncProgressDialog.show();
                    }
                })
                .setPositiveButton(confirmButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.doCancelSync();
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
    public void showSyncSuccessMessage() {
        Toast.makeText(getActivity(), syncSuccessMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSyncErrorMessage() {
        Toast.makeText(getActivity(), syncErrorMessage, Toast.LENGTH_SHORT).show();
    }

}
