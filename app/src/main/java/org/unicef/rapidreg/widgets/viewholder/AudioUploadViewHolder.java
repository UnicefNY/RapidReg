package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.AudioRecorderActivity;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.service.cache.CaseFieldValueCache;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AudioUploadViewHolder extends BaseViewHolder {


    private static final String LOG_TAG = "AudioRecordTest";
    public static final int RECORDER_MAX_DURATION_MS = 10000;
    private static String mFileName = null;

    @BindView(R.id.record_button)
    ImageView recordButton;
    @BindView(R.id.play_button)
    ImageView playButton;
    @BindView(R.id.delete_button)
    ImageView audioDeleteButton;
    @BindView(R.id.no_file_text_view)
    TextView noFileTextView;
    private CaseActivity caseActivity;


    public AudioUploadViewHolder(Context context, View itemView) {
        super(context, itemView);
        ButterKnife.bind(this, itemView);
        caseActivity = (CaseActivity) context;

        mFileName = CaseFieldValueCache.AUDIO_FILE_PATH;

        if (!(isCaseMode(CaseActivity.CaseMode.EDIT) || isCaseMode(CaseActivity.CaseMode.ADD))) {
            initPlayAudioUI();

        }
        if (isCaseMode(CaseActivity.CaseMode.ADD)) {
            CaseFieldValueCache.clearAudioFile();
        }
        if (isFileExists(mFileName)) {
            initPlayAudioUI();
            showDeleteIconWhenIsEditMode();
        }
    }

    public boolean isCaseMode(CaseActivity.CaseMode mode) {
        CaseActivity.CaseMode caseMode = (CaseActivity.CaseMode) caseActivity.getIntent().getExtras().get(CaseActivity.INTENT_KEY_CASE_MODE);
        if (caseMode == mode) {
            return true;
        }
        return false;
    }


    @OnClick(R.id.record_button)
    public void onRecordButtonClicked() {
        Intent intent = new Intent(caseActivity, AudioRecorderActivity.class);
        intent.putExtra("currentState", "StartRecording");
        int requestCode = 1;
        caseActivity.startActivityForResult(intent, requestCode);
        recordButton.setVisibility(View.GONE);
        playButton.setVisibility(View.VISIBLE);
        showDeleteIconWhenIsEditMode();
    }

    private void showDeleteIconWhenIsEditMode() {
        if (isCaseMode(CaseActivity.CaseMode.EDIT) || isCaseMode(CaseActivity.CaseMode.ADD)) {
            audioDeleteButton.setVisibility(View.VISIBLE);
        }
    }

    private void initPlayAudioUI() {
        if (isFileExists(mFileName)) {
            recordButton.setVisibility(View.GONE);
            playButton.setVisibility(View.VISIBLE);
        } else {
            recordButton.setVisibility(View.GONE);
            playButton.setVisibility(View.GONE);
            noFileTextView.setText("No audio file exist.");
        }
    }


    @OnClick(R.id.play_button)
    public void onPlayButtonClicked() {
        Intent intent = new Intent(caseActivity, AudioRecorderActivity.class);
        intent.putExtra("currentState", "StartPlaying");
        int requestCode = 2;
        caseActivity.startActivityForResult(intent, requestCode);
    }

    private boolean isFileExists(String filePath) {

        File f = new File(filePath);
        if (f.exists() && !f.isDirectory()) {
            return true;
        }
        return false;
    }

    @OnClick(R.id.delete_button)
    public void onDeleteButtonClicked() {
        initAudioRecordUI();
        CaseFieldValueCache.clearAudioFile();
    }

    private void initAudioRecordUI() {
        recordButton.setVisibility(View.VISIBLE);
        recordButton.setImageResource(R.drawable.mic_rec_210);
        playButton.setVisibility(View.GONE);
        audioDeleteButton.setVisibility(View.GONE);
    }
}
