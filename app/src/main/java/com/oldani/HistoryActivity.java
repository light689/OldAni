package com.oldani;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.oldani.adapter.HistoryAdapter;
import com.oldani.data.HistoryDb;
import com.oldani.model.HistoryItem;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recycler;
    private TextView emptyText;
    private HistoryDb historyDb;
    private final List<HistoryItem> items = new ArrayList<>();
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        TextView pageName = findViewById(R.id.pageName);
        pageName.setOnClickListener(v -> finish());

        View clearBtn = findViewById(R.id.clear_btn);
        clearBtn.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setMessage(R.string.confirm_clear)
                .setPositiveButton(R.string.yes, (d, w) -> {
                    historyDb.deleteAll();
                    refreshList();
                    Toast.makeText(this, "已清除", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no, null)
                .show());

        emptyText = findViewById(R.id.empty_text);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        historyDb = HistoryDb.getInstance(this);
        adapter = new HistoryAdapter(items, item -> {
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("subject_id", item.subjectId);
            intent.putExtra("subject_name", item.subjectName);
            intent.putExtra("subject_image", item.subjectImageUrl);
            intent.putExtra("episode_id", item.episodeId);
            intent.putExtra("episode_name", item.episodeName);
            startActivity(intent);
        });
        recycler.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        items.clear();
        items.addAll(historyDb.getAll());
        adapter.notifyDataSetChanged();
        emptyText.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        recycler.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
    }
}