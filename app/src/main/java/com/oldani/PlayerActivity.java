package com.oldani;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.oldani.data.HistoryDb;
import com.oldani.model.HistoryItem;

public class PlayerActivity extends AppCompatActivity {
    private ExoPlayer player;
    private PlayerView playerView;
    private EditText urlInput;
    private LinearLayout loading;
    private View playBtn;
    private HistoryDb historyDb;
    private HistoryItem history;

    private int subjectId;
    private String subjectName;
    private String subjectImage;
    private int episodeId;
    private String episodeName;
    private long duration = 0;

    private final Handler saveHandler = new Handler(Looper.getMainLooper());
    private Runnable saveRunnable;
    private boolean playing = false;
    private boolean firstPlay = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        subjectId = getIntent().getIntExtra("subject_id", 0);
        subjectName = getIntent().getStringExtra("subject_name");
        subjectImage = getIntent().getStringExtra("subject_image");
        episodeId = getIntent().getIntExtra("episode_id", 0);
        episodeName = getIntent().getStringExtra("episode_name");

        historyDb = HistoryDb.getInstance(this);
        history = historyDb.get(episodeId);

        playerView = findViewById(R.id.player_view);
        urlInput = findViewById(R.id.url_input);
        loading = findViewById(R.id.loading);
        playBtn = findViewById(R.id.play_btn);

        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                    loading.setVisibility(View.GONE);
                    if (firstPlay && history != null && history.positionMillis > 0) {
                        duration = player.getDuration();
                        if (duration > 0 && history.positionMillis < duration - 5000) {
                            player.seekTo(history.positionMillis);
                        }
                        firstPlay = false;
                    }
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                playing = isPlaying;
                if (isPlaying) {
                    startPeriodicSave();
                } else {
                    saveProgress();
                    stopPeriodicSave();
                }
            }
        });

        playBtn.setOnClickListener(v -> {
            String url = urlInput.getText().toString().trim();
            if (url.isEmpty()) {
                Toast.makeText(this, R.string.no_video_url, Toast.LENGTH_SHORT).show();
                return;
            }
            loading.setVisibility(View.VISIBLE);
            firstPlay = true;
            duration = 0;
            player.setMediaItem(MediaItem.fromUri(url));
            player.prepare();
            player.play();
        });
    }

    private void startPeriodicSave() {
        if (saveRunnable != null) return;
        saveRunnable = new Runnable() {
            @Override
            public void run() {
                if (playing) {
                    saveProgress();
                    saveHandler.postDelayed(this, 60000);
                }
            }
        };
        saveHandler.postDelayed(saveRunnable, 5000);
    }

    private void stopPeriodicSave() {
        if (saveRunnable != null) {
            saveHandler.removeCallbacks(saveRunnable);
            saveRunnable = null;
        }
    }

    private void saveProgress() {
        long pos = player.getCurrentPosition();
        long dur = player.getDuration();
        if (dur <= 0) return;
        duration = dur;
        if (pos >= dur - 5000) {
            historyDb.delete(episodeId);
            history = null;
        } else {
            historyDb.save(episodeId, pos, subjectId, subjectName, subjectImage, episodeName, dur);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player.isPlaying()) {
            player.pause();
        }
        saveProgress();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPeriodicSave();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPeriodicSave();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}