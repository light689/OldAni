package com.oldani.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.oldani.R;
import com.oldani.model.HistoryItem;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.Holder> {
    private final List<HistoryItem> items;
    private final OnItemClickListener listener;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public interface OnItemClickListener {
        void onItemClick(HistoryItem item);
    }

    public HistoryAdapter(List<HistoryItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_history, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int pos) {
        HistoryItem item = items.get(pos);
        h.subjectName.setText(item.subjectName != null ? item.subjectName : "");
        h.episodeName.setText(item.episodeName != null ? item.episodeName : "");
        int pct = item.progressPercent();
        h.playTime.setText(sdf.format(new Date(item.updatedAtMillis)) + "  " + pct + "%");
        if (item.subjectImageUrl != null && !item.subjectImageUrl.isEmpty()) {
            Glide.with(h.cover).load(item.subjectImageUrl).into(h.cover);
        }
        h.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView subjectName, episodeName, playTime;
        Holder(View v) {
            super(v);
            cover = v.findViewById(R.id.cover);
            subjectName = v.findViewById(R.id.subject_name);
            episodeName = v.findViewById(R.id.episode_name);
            playTime = v.findViewById(R.id.play_time);
        }
    }
}