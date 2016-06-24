package org.unicef.rapidreg.widgets.viewholder;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.service.cache.CaseFieldValueCache;

import java.io.File;
import java.io.IOException;

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
    @BindView(R.id.audio_timer)
    TextView timerTextView;
    @BindView(R.id.audio_timer_total)
    TextView timerTotalTextView;
    private CaseActivity caseActivity;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private boolean mStartRecording = true;
    private boolean mStartPlaying = true;

    private long startTime = 0;
    private int audioDuration = 0;


    final Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;

            timerTextView.setText(getTime(millis));

            timerHandler.postDelayed(this, 500);
        }
    };

    private String getTime(long millis) {
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format("%d:%02d", minutes, seconds);
    }


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
        if (caseMode ==  mode) {
            return true;
        }
        return false;
    }


    @OnClick(R.id.record_button)
    public void onRecordButtonClicked() {
        onRecord(mStartRecording);
        if (mStartRecording) {
            recordButton.setImageResource(R.drawable.pause_210_with_color_boarder);
        } else {
            initPlayAudioUI();
            showDeleteIconWhenIsEditMode();
        }
        mStartRecording = !mStartRecording;
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
            timerTextView.setVisibility(View.GONE);
            timerTotalTextView.setText("No audio file exist.");
        }
    }

    @OnClick(R.id.play_button)
    public void onPlayButtonClicked() {
        onPlay(mStartPlaying);
        if (mStartPlaying) {
            playButton.setImageResource(R.drawable.pause_210_with_color_boarder);
            audioDeleteButton.setVisibility(View.GONE);
        } else {
            playButton.setImageResource(R.drawable.play_210);
            showDeleteIconWhenIsEditMode();
        }
        mStartPlaying = !mStartPlaying;
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
        timerTextView.setText("0:00");
        timerTotalTextView.setText("/1:00");
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
            startTiming();
        } else {
            stopTiming();
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
            startTiming();
        } else {
            stopTiming();
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mPlayer != null) {
                        onPlayButtonClicked();
                    }
                }
            });
            mPlayer.prepare();
            audioDuration = mPlayer.getDuration();
            timerTotalTextView.setText("/" + getTime(audioDuration));
            mPlayer.start();

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setMaxDuration(RECORDER_MAX_DURATION_MS);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        mRecorder.start();
        mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    onRecordButtonClicked();
                }
            }
        });
    }

    private void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }


    public void startTiming() {
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    public void stopTiming() {
        timerHandler.removeCallbacks(timerRunnable);
    }

}
