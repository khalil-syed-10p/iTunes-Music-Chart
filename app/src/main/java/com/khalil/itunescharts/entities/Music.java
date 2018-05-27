package com.khalil.itunescharts.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Music {

    @PrimaryKey
    @NonNull
    public long id;
    public String name;
    public String artistName;
    public String artworkUrl100;
}
