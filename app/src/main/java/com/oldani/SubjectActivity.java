package com.oldani;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.oldani.adapter.EpisodeAdapter;
import com.oldani.model.Episode;
import com.oldani.model.Subject;
import java.util.ArrayList;
import java.util.List;

public class SubjectActivity extends AppCompatActivity {
    private ImageView cover;
    private TextView nameView, nameCnView, scoreView, airDateView, summaryView;
    private RecyclerView episodesGrid;
    private ProgressBar progress;
    private ScrollView scroll;
    private TextView pageName;
    private Subject subject;
    private final List<Episode> episodes = new ArrayList<>();
    private EpisodeAdapter episodeAdapter;
    private Thread loadThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        pageName = findViewById(R.id.pageName);
        pageName.setOnClickListener(v -> finish());

        cover = findViewById(R.id.cover);
        nameView = findViewById(R.id.name);
        nameCnView = findViewById(R.id.name_cn);
        scoreView = findViewById(R.id.score);
        airDateView = findViewById(R.id.air_date);
        summaryView = findViewById(R.id.summary);
        episodesGrid = findViewById(R.id.episodes_grid);
        progress = findViewById(R.id.progress);
        scroll = findViewById(R.id.scroll);

        episodesGrid.setLayoutManager(new GridLayoutManager(this, 4));
        episodeAdapter = new EpisodeAdapter(episodes, episode -> {
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("subject_id", subject.id);
            intent.putExtra("subject_name", subject.displayName());
            intent.putExtra("subject_image", subject.image);
            intent.putExtra("episode_id", episode.id);
            intent.putExtra("episode_name", episode.displayName());
            startActivity(intent);
        });
        episodesGrid.setAdapter(episodeAdapter);

        int subjectId = getIntent().getIntExtra("subject_id", 0);
        String previewName = getIntent().getStringExtra("subject_name");
        String previewImage = getIntent().getStringExtra("subject_image");
        pageName.setText(previewName != null ? previewName : "");

        progress.setVisibility(View.VISIBLE);
        scroll.setVisibility(View.GONE);

        loadThread = new Thread(() -> {
            try {
                Subject s = ((App) getApplication()).getBangumiApi().getSubject(subjectId);
                runOnUiThread(() -> {
                    subject = s;
                    bindData();
                    progress.setVisibility(View.GONE);
                    scroll.setVisibility(View.VISIBLE);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(this, "加载失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
        loadThread.start();
    }

    private void bindData() {
        pageName.setText(subject.displayName());
        nameView.setText(subject.name);
        nameCnView.setText(subject.nameCn);
        scoreView.setText(subject.score > 0 ? "评分: " + String.format("%.1f", subject.score) : "");
        airDateView.setText(subject.airDate);
        summaryView.setText(subject.summary);
        if (subject.image != null && !subject.image.isEmpty()) {
            Glide.with(this).load(subject.image).into(cover);
        }
        episodes.clear();
        episodes.addAll(subject.episodes);
        episodeAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadThread != null) loadThread.interrupt();
    }
}