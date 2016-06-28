package org.unicef.rapidreg.childcase;


import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.service.cache.CaseFieldValueCache;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AudioRecorderActivity extends AppCompatActivity {
    private static final String LOG_TAG = "AudioRecordTest";
    public static final int RECORDER_MAX_DURATION_MS = 60000;
    private static String mFileName = null;

    @BindView(R.id.stop_button)
    ImageView stopButton;
    @BindView(R.id.audio_timer)
    TextView timerTextView;
    @BindView(R.id.audio_timer_total)
    TextView timerTotalTextView;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private boolean mStartRecording = true;
    private boolean mStartPlaying = true;

    private long startTime = 0;
    private int audioDuration = 0;
    private long passedTime = 0;


    final Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;

            timerTextView.setText(getTime(millis));
            passedTime = millis;
            timerHandler.postDelayed(this, 500);
        }
    };

    private String getTime(long millis) {
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format("%d:%02d", minutes, seconds);
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record_layout);
        ButterKnife.bind(this);
        mFileName = CaseFieldValueCache.AUDIO_FILE_PATH;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("currentState");
            if (TextUtils.equals(value, "StartRecording")) {
                recordAudio();
            } else if (TextUtils.equals(value, "StartPlaying")) {
                playAudio();
            }
        }
    }

    @OnClick(R.id.stop_button)
    public void onStopButtonClicked() {
        exitAudioRecorder();
    }

    private void exitAudioRecorder() {
        if (passedTime > 1000) {
            stopRecording();
            stopPlaying();
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        exitAudioRecorder();
    }

    public void recordAudio() {
        onRecord(mStartRecording);
        mStartRecording = !mStartRecording;
    }


    public void playAudio() {
        onPlay(mStartPlaying);
        mStartPlaying = !mStartPlaying;
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
                    exitAudioRecorder();
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
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
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
                    exitAudioRecorder();
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
