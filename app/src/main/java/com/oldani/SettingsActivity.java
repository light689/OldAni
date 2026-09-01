package com.oldani;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.oldani.data.HistoryDb;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView pageName = findViewById(R.id.pageName);
        pageName.setOnClickListener(v -> finish());

        findViewById(R.id.btn_clear_history).setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setMessage(R.string.confirm_clear)
                        .setPositiveButton(R.string.yes, (d, w) -> {
                            HistoryDb.getInstance(this).deleteAll();
                            Toast.makeText(this, "已清除", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(R.string.no, null)
                        .show());

        findViewById(R.id.btn_about).setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle(R.string.about)
                        .setMessage(R.string.about_text)
                        .setPositiveButton(R.string.yes, null)
                        .show());
    }
}