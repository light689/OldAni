package com.oldani;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView pageName = findViewById(R.id.pageName);
        pageName.setText(R.string.app_name);

        findViewById(R.id.btn_search).setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));

        findViewById(R.id.btn_history).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));

        findViewById(R.id.btn_settings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }
}