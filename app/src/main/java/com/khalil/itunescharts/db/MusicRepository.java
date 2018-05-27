package com.khalil.itunescharts.db;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.khalil.itunescharts.entities.Music;
import com.khalil.itunescharts.entities.MusicChartsList;
import com.khalil.itunescharts.network.CommonNetworkActionsHandler;
import com.khalil.itunescharts.network.NetworkUI;
import com.khalil.itunescharts.network.ServiceCallback;
import com.khalil.itunescharts.network.ServiceFactory;

import java.util.List;

public class MusicRepository {

    private AppDatabase appDatabase;
    private MusicRepositoryDataCallbacks musicRepositoryDataCallbacks;
    private MusicRepositoryDelegate musicRepositoryDelegate;
    private DBOperationsCallback dbOperationsCallback;

    public interface DBOperationsCallback<T> {

        void onDBInsertCompleted();
        void onDBFetchCompleted(List<T> list);
        void onDBFetchFailed();
    }

    public interface MusicRepositoryDataCallbacks {

        void onDataAvailable(List<Music> musicList);
        void onDataNotAvailable();
        void onNetworkError(String errorMessage);
        void onNetworkCallSuccess();
    }

    public interface MusicRepositoryDelegate {

        ServiceFactory getServiceFactory();
        NetworkUI getNetworkUI();
        CommonNetworkActionsHandler getCommonNetworkActionsHandler();
        AppDatabase getAppDatabase();
    }

    public MusicRepository(@NonNull MusicRepositoryDelegate musicRepositoryDelegate, @NonNull MusicRepositoryDataCallbacks musicRepositoryDataCallbacks) {
        this.musicRepositoryDelegate = musicRepositoryDelegate;
        this.appDatabase = musicRepositoryDelegate.getAppDatabase();
        this.musicRepositoryDataCallbacks = musicRepositoryDataCallbacks;
        this.dbOperationsCallback = new DBOperationsCallback<Music>() {
            @Override
            public void onDBInsertCompleted() {
                fetchDataFromDB();
            }

            @Override
            public void onDBFetchCompleted(List<Music> list) {
                dataAvailable(list);
            }

            @Override
            public void onDBFetchFailed() {
                fetchDataFromAPI(false);
            }
        };
    }

    public void fetchMusicCharts() {
        fetchDataFromDB();
    }

    public void onRefresh() {
        fetchDataFromAPI(true);
    }

    private void dataAvailable(List<Music> musicList) {
        if(musicRepositoryDataCallbacks == null) {
            return;
        }

        musicRepositoryDataCallbacks.onDataAvailable(musicList);
    }

    private void dataNotAvailable() {
        if(musicRepositoryDataCallbacks == null) {
            return;
        }

        musicRepositoryDataCallbacks.onDataNotAvailable();
    }

    private void fetchDataFromAPI(final boolean isRefesh) {
        if(musicRepositoryDelegate == null) {
            dataNotAvailable();
            return;
        }

        musicRepositoryDelegate.getServiceFactory().getMusicChartsService().top10MusicCharts(10)
                .networkUI(musicRepositoryDelegate.getNetworkUI())
                .commonNetworkActionsHandler(musicRepositoryDelegate.getCommonNetworkActionsHandler())
                .enqueue(new ServiceCallback<MusicChartsList>() {
            @Override
            public void onSuccess(MusicChartsList response, int code) {
                persistData(response.getMusicList());
            }

            @Override
            public void onFailure(String errorMessage, int code) {
                if(musicRepositoryDataCallbacks == null) {
                    return;
                }

                musicRepositoryDataCallbacks.onNetworkError(errorMessage);
                if(isRefesh) {
                    return;
                }

                musicRepositoryDataCallbacks.onDataNotAvailable();
            }
        });
    }


    private void fetchDataFromDB() {
        new FetchDBAsync(appDatabase, dbOperationsCallback)
                .execute();
    }

    private void persistData(List<Music> musicList) {
        new PersistDataAsync(appDatabase, musicList, dbOperationsCallback)
                .execute();
    }

    private static class PersistDataAsync extends AsyncTask<Void, Void, Void> {

        private AppDatabase appDatabase;
        private List<Music> musicList;
        private DBOperationsCallback dbOperationsCallback;

        PersistDataAsync(AppDatabase appDatabase, List<Music> musicList, DBOperationsCallback dbOperationsCallback) {

            this.appDatabase = appDatabase;
            this.musicList = musicList;
            this.dbOperationsCallback = dbOperationsCallback;
        }

        @Override
        protected Void doInBackground(Void... params) {

            appDatabase.musicModel().deleteAll();
            for (Music music : musicList) {
                appDatabase.musicModel().insertMusic(music);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(dbOperationsCallback != null) dbOperationsCallback.onDBInsertCompleted();
        }
    }

    private static class FetchDBAsync extends AsyncTask<Void, Void, List<Music>> {

        private AppDatabase appDatabase;
        private DBOperationsCallback<Music> dbOperationsCallback;

        FetchDBAsync(AppDatabase appDatabase, DBOperationsCallback<Music> dbOperationsCallback) {

            this.appDatabase = appDatabase;
            this.dbOperationsCallback = dbOperationsCallback;
        }

        @Override
        protected List<Music> doInBackground(Void... params) {

            return appDatabase.musicModel().loadMusicCharts();
        }

        @Override
        protected void onPostExecute(List<Music> musicList) {
            if(dbOperationsCallback == null) {
                return;
            }

            if(musicList == null || musicList.isEmpty()) {
                dbOperationsCallback.onDBFetchFailed();
                return;
            }
            dbOperationsCallback.onDBFetchCompleted(musicList);
        }
    }
}
