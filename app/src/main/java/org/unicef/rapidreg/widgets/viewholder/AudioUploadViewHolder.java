package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.media.AudioRecorderActivity;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.service.cache.CaseFieldValueCache;
import org.unicef.rapidreg.utils.StreamUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AudioUploadViewHolder extends BaseViewHolder {
    private static String mFileName = null;

    @BindView(R.id.record_button)
    ImageView recordButton;
    @BindView(R.id.play_button)
    ImageView playButton;
    @BindView(R.id.delete_button)
    ImageView audioDeleteButton;
    @BindView(R.id.no_file_text_view)
    TextView noFileTextView;

    private CaseActivity activity;

    public AudioUploadViewHolder(Context context, View itemView) {
        super(context, itemView);
        ButterKnife.bind(this, itemView);
        activity = (CaseActivity) context;

        mFileName = CaseFieldValueCache.AUDIO_FILE_PATH;

        if (!activity.getCurrentFeature().isInEditMode()) {
            initPlayAudioUI();
        }
        if (activity.getCurrentFeature().isInAddMode()) {
            CaseFieldValueCache.clearAudioFile();
        }
        if (StreamUtil.isFileExists(mFileName)) {
            initPlayAudioUI();
            showDeleteIconWhenIsEditMode();
        }
    }

    @Override
    public void setValue(Object field) {

    }

    @Override
    public void setOnClickListener(Object field) {

    }

    @Override
    protected String getResult() {
        return null;
    }

    @Override
    public void setFieldEditable(boolean editable) {

    }

    @OnClick(R.id.play_button)
    public void onPlayButtonClicked() {
        Intent intent = new Intent(activity, AudioRecorderActivity.class);
        intent.putExtra(AudioRecorderActivity.CURRENT_STATE, AudioRecorderActivity.START_PLAYING);
        int requestCode = 2;
        activity.startActivityForResult(intent, requestCode);
    }

    @OnClick(R.id.delete_button)
    public void onDeleteButtonClicked() {
        initAudioRecordUI();
        CaseFieldValueCache.clearAudioFile();
    }

    @OnClick(R.id.record_button)
    public void onRecordButtonClicked() {
        Intent intent = new Intent(activity, AudioRecorderActivity.class);
        intent.putExtra(AudioRecorderActivity.CURRENT_STATE, AudioRecorderActivity.START_RECORDING);
        int requestCode = 1;
        activity.startActivityForResult(intent, requestCode);
        recordButton.setVisibility(View.GONE);
        playButton.setVisibility(View.VISIBLE);
        showDeleteIconWhenIsEditMode();
    }

    private void showDeleteIconWhenIsEditMode() {
        if (activity.getCurrentFeature().isInEditMode()) {
            audioDeleteButton.setVisibility(View.VISIBLE);
        }
    }

    private void initPlayAudioUI() {
        if (StreamUtil.isFileExists(mFileName)) {
            recordButton.setVisibility(View.GONE);
            playButton.setVisibility(View.VISIBLE);
        } else {
            recordButton.setVisibility(View.GONE);
            playButton.setVisibility(View.GONE);
            noFileTextView.setText(R.string.audio_file_not_exists);
        }
    }

    private void initAudioRecordUI() {
        recordButton.setVisibility(View.VISIBLE);
        recordButton.setImageResource(R.drawable.mic_rec_210);
        playButton.setVisibility(View.GONE);
        audioDeleteButton.setVisibility(View.GONE);
    }
}
