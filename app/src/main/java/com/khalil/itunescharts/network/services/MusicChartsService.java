package com.khalil.itunescharts.network.services;

import com.khalil.itunescharts.entities.MusicChartsList;
import com.khalil.itunescharts.network.ServiceCall;

import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MusicChartsService {

    @GET("api/v1/us/apple-music/top-songs/all/{count}/explicit.json")
    ServiceCall<MusicChartsList> top10MusicCharts (@Path("count") int count);
}
