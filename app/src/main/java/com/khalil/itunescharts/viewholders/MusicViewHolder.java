package com.khalil.itunescharts.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.khalil.itunescharts.R;
import com.khalil.itunescharts.components.CircleImageView;
import com.khalil.itunescharts.entities.Music;

public class MusicViewHolder extends RecyclerView.ViewHolder{

    private CircleImageView imgArtwork;
    private TextView txtName;
    private TextView txtArtistName;

    public MusicViewHolder(View itemView) {
        super(itemView);

        imgArtwork = itemView.findViewById(R.id.imgArtwork);
        txtArtistName = itemView.findViewById(R.id.txtArtistName);
        txtName= itemView.findViewById(R.id.txtName);
    }

    public void setMusic(Music music) {
        if(music == null) {
            return;
        }

        imgArtwork.imageLoader(music.artworkUrl100).load();
        txtName.setText(music.name);
        txtArtistName.setText(music.artistName);
    }
}
