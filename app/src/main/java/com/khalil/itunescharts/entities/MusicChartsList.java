package com.khalil.itunescharts.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MusicChartsList {

    @SerializedName("results")
    private List<Music> musicList;

    public List<Music> getMusicList() {
        return musicList;
    }
}
