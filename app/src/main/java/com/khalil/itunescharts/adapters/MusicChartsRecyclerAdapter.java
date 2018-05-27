package com.khalil.itunescharts.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khalil.itunescharts.R;
import com.khalil.itunescharts.entities.Music;
import com.khalil.itunescharts.viewholders.MusicViewHolder;

import java.util.List;

/**
 * Created on 5/17/17.
 */

public class MusicChartsRecyclerAdapter extends RecyclerView.Adapter<MusicViewHolder> {

    private List<Music> musicList;

    public void updateData(List<Music> musicList) {
        this.musicList = musicList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_itunes_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        if((musicList == null)
                || musicList.isEmpty()) {
            return;
        }

        holder.setMusic(musicList.get(position));
    }

    @Override
    public int getItemCount() {
        return (musicList != null) ? musicList.size() : 0;
    }
}
