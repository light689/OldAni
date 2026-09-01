package com.oldani;

import android.app.Application;
import com.oldani.api.BangumiApi;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class App extends Application {
    private OkHttpClient httpClient;
    private BangumiApi bangumiApi;

    @Override
    public void onCreate() {
        super.onCreate();
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        bangumiApi = new BangumiApi(httpClient);
    }

    public OkHttpClient getHttpClient() { return httpClient; }
    public BangumiApi getBangumiApi() { return bangumiApi; }
}