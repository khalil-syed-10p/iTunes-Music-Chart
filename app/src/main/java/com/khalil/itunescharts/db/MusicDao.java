package com.khalil.itunescharts.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.khalil.itunescharts.entities.Music;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface MusicDao {

    @Insert(onConflict = IGNORE)
    void insertMusic(Music music);

    @Query("DELETE FROM Music")
    void deleteAll();

    @Query("SELECT * FROM Music")
    List<Music> loadMusicCharts();
}
