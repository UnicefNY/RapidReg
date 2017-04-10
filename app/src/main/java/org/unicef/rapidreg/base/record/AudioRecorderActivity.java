package org.unicef.rapidreg.base.record;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.unicef.rapidreg.IntentSender;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.login.AccountManager;
import org.unicef.rapidreg.service.RecordService;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AudioRecorderActivity extends AppCompatActivity {
    private static final int PERMISSION_GRANTED = 1;
    private static final int NO_PERMISSION = 0;
    private static final int PERMISSION_DENIED = -1;

    public static final String TAG = AudioRecorderActivity.class.getSimpleName();
    public static final int RECORDER_MAX_DURATION_MS = 60000;
    public static final String CURRENT_STATE = "current_state";
    public static final int START_RECORDING = 0;
    public static final int START_PLAYING = 1;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 777;

    private static String mFileName = RecordService.AUDIO_FILE_PATH;

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

    private int currentMode;

    final Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;

            timerTextView.setText(getTime(millis));
            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doCloseIfNotLogin();
        setContentView(R.layout.activity_audio_record_layout);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentMode = extras.getInt(CURRENT_STATE);
            if (currentMode == START_RECORDING) {
                recordAudio();
            } else if (currentMode == START_PLAYING) {
                playAudio();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        doCloseIfNotLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doCloseIfNotLogin();
    }

    @Override
    public void onPause() {
        super.onPause();
        exitAudioRecorder();
    }

    public void startTiming() {
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    public void stopTiming() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    public void recordAudio() {
        switch (isPermissionGranted(android.Manifest.permission.RECORD_AUDIO)) {
            case PERMISSION_DENIED:
                Toast.makeText(this, "Record audio is not allowed!", Toast.LENGTH_SHORT);
                return;
            case NO_PERMISSION:
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
                return;
            case PERMISSION_GRANTED:
                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
        }
    }

    public void playAudio() {
        onPlay(mStartPlaying);
        mStartPlaying = !mStartPlaying;
    }

    @OnClick(R.id.stop_button)
    public void onStopButtonClicked() {
        exitAudioRecorder();
    }

    private void exitAudioRecorder() {
        if (currentMode == START_RECORDING) {
            exitRecording();
        } else if (currentMode == START_PLAYING) {
            exitPlaying();
        }
    }

    private void exitPlaying() {
        stopPlaying();
        finish();
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

    private void exitRecording() {
        stopRecording();
        finish();
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
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setMaxDuration(RECORDER_MAX_DURATION_MS);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
        mRecorder.start();
        mRecorder.setOnInfoListener((mr, what, extra) -> {
            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                exitAudioRecorder();
            }
        });
    }

    private void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }
    }

    private String getTime(long millis) {
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format("%d:%02d", minutes, seconds);
    }

    private void doCloseIfNotLogin() {
        if (!AccountManager.isSignIn()) {
            AccountManager.doSignOut();
            new IntentSender().showLoginActivity(this);
            finish();
            return;
        }
    }

    private int isPermissionGranted(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            return PERMISSION_GRANTED;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            return PERMISSION_DENIED;
        }

        return NO_PERMISSION;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recordAudio();
                } else {
                    Toast.makeText(this, "Record audio is not allowed!", Toast.LENGTH_SHORT);
                }
                return;
        }
    }
}
