package com.oldani;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.oldani.adapter.SubjectAdapter;
import com.oldani.model.Subject;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText searchInput;
    private RecyclerView recycler;
    private ProgressBar progress;
    private TextView emptyText;
    private final List<Subject> results = new ArrayList<>();
    private SubjectAdapter adapter;
    private Thread searchThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        TextView pageName = findViewById(R.id.pageName);
        pageName.setOnClickListener(v -> finish());

        searchInput = findViewById(R.id.search_input);
        progress = findViewById(R.id.progress);
        emptyText = findViewById(R.id.empty_text);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new SubjectAdapter(results, subject -> {
            Intent intent = new Intent(this, SubjectActivity.class);
            intent.putExtra("subject_id", subject.id);
            intent.putExtra("subject_name", subject.displayName());
            intent.putExtra("subject_image", subject.image);
            startActivity(intent);
        });
        recycler.setAdapter(adapter);

        View searchBtn = findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(v -> doSearch());

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch();
                return true;
            }
            return false;
        });
    }

    private void doSearch() {
        String kw = searchInput.getText().toString().trim();
        if (kw.isEmpty()) return;

        if (searchThread != null && searchThread.isAlive()) searchThread.interrupt();
        progress.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);
        recycler.setVisibility(View.GONE);

        searchThread = new Thread(() -> {
            try {
                List<Subject> list = ((App) getApplication()).getBangumiApi().search(kw);
                runOnUiThread(() -> {
                    results.clear();
                    results.addAll(list);
                    adapter.notifyDataSetChanged();
                    progress.setVisibility(View.GONE);
                    if (results.isEmpty()) {
                        emptyText.setVisibility(View.VISIBLE);
                    } else {
                        recycler.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(this, "搜索失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
        searchThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchThread != null) searchThread.interrupt();
    }
}