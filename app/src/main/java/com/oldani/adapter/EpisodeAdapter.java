package com.oldani.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.oldani.R;
import com.oldani.model.Episode;
import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.Holder> {
    private final List<Episode> items;
    private final OnEpisodeClickListener listener;

    public interface OnEpisodeClickListener {
        void onEpisodeClick(Episode episode);
    }

    public EpisodeAdapter(List<Episode> items, OnEpisodeClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_episode, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int pos) {
        Episode e = items.get(pos);
        h.name.setText(e.displayName());
        h.itemView.setOnClickListener(v -> listener.onEpisodeClick(e));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        TextView name;
        Holder(View v) {
            super(v);
            name = v.findViewById(R.id.episode_name);
        }
    }
}