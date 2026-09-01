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
import com.oldani.model.Subject;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.Holder> {
    private final List<Subject> items;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Subject subject);
    }

    public SubjectAdapter(List<Subject> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_subject, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int pos) {
        Subject s = items.get(pos);
        h.name.setText(s.displayName());
        if (s.nameCn != null && !s.nameCn.isEmpty()) {
            h.nameCn.setText(s.name);
        } else {
            h.nameCn.setText("");
        }
        h.airDate.setText(s.airDate);
        h.score.setText(s.score > 0 ? String.format("%.1f", s.score) : "");
        if (s.image != null && !s.image.isEmpty()) {
            Glide.with(h.cover).load(s.image).into(h.cover);
        }
        h.itemView.setOnClickListener(v -> listener.onItemClick(s));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView name, nameCn, airDate, score;
        Holder(View v) {
            super(v);
            cover = v.findViewById(R.id.cover);
            name = v.findViewById(R.id.name);
            nameCn = v.findViewById(R.id.name_cn);
            airDate = v.findViewById(R.id.air_date);
            score = v.findViewById(R.id.score);
        }
    }
}