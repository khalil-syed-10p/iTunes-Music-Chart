package com.khalil.itunescharts.activities;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

import com.khalil.itunescharts.R;
import com.khalil.itunescharts.activities.base.BaseActivity;
import com.khalil.itunescharts.adapters.MusicChartsRecyclerAdapter;
import com.khalil.itunescharts.db.AppDatabase;
import com.khalil.itunescharts.db.MusicRepository;
import com.khalil.itunescharts.entities.Music;
import com.khalil.itunescharts.network.CommonNetworkActionsHandler;
import com.khalil.itunescharts.network.NetworkUI;
import com.khalil.itunescharts.network.ServiceCall;
import com.khalil.itunescharts.network.ServiceFactory;


public class MainActivity extends BaseActivity implements MusicRepository.MusicRepositoryDataCallbacks,
        MusicRepository.MusicRepositoryDelegate, CommonNetworkActionsHandler {

    private RecyclerView recyclerPodcastCharts;
    private TextView txtNoData;
    private Button btnRefresh;
    private MusicChartsRecyclerAdapter musicChartsRecyclerAdapter;

    private MusicRepository musicRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerPodcastCharts = findViewById(R.id.recyclerMusicCharts);
        txtNoData = findViewById(R.id.txtNoData);
        btnRefresh = findViewById(R.id.btnRefresh);


        musicChartsRecyclerAdapter = new MusicChartsRecyclerAdapter();
        recyclerPodcastCharts.setLayoutManager(new LinearLayoutManager(this));
        recyclerPodcastCharts.setAdapter(musicChartsRecyclerAdapter);
        recyclerPodcastCharts.addItemDecoration(new DividerItemDecoration(this, 1));

        musicRepository = new MusicRepository(this, this);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                musicRepository.onRefresh();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        musicRepository.fetchMusicCharts();
        btnRefresh.setEnabled(isInternetConnected());
    }

    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        if(connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public View getContentView() {
        return findViewById(R.id.mainLayout);
    }

    @Override
    protected void onDestroy() {
        AppDatabase.destroyInstance();
        super.onDestroy();
    }

    @Override
    public void onDataAvailable(List<Music> musicList) {
        recyclerPodcastCharts.setVisibility(View.VISIBLE);
        txtNoData.setVisibility(View.GONE);
        musicChartsRecyclerAdapter.updateData(musicList);
    }

    @Override
    public void onDataNotAvailable() {
        recyclerPodcastCharts.setVisibility(View.GONE);
        txtNoData.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNetworkError(String errorMessage) {
        if(TextUtils.isEmpty(errorMessage)) {
            return;
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNetworkCallSuccess() {
        btnRefresh.setEnabled(true);
    }

    @Override
    public ServiceFactory getServiceFactory() {
        return super.getServiceFactory();
    }

    @Override
    public NetworkUI getNetworkUI() {
        return this;
    }

    @Override
    public CommonNetworkActionsHandler getCommonNetworkActionsHandler() {
        return this;
    }

    @Override
    public AppDatabase getAppDatabase() {
        return AppDatabase.getDatabase(this);
    }

    @Override
    public void onUnAuthorized(ServiceCall serviceCall, String errorMessage, int code, View contentView) {

    }

    @Override
    public void onInternetConnectionError(ServiceCall serviceCall, View contentView) {
        btnRefresh.setEnabled(false);
        Toast.makeText(this, getString(R.string.error_internet_connection), Toast.LENGTH_LONG).show();
    }
}
