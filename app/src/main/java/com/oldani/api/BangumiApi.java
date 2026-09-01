package com.oldani.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.oldani.model.Episode;
import com.oldani.model.Subject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BangumiApi {
    private static final String BASE_URL = "https://api.bgm.tv";
    private static final String UA = "oldani/0.1";
    private final OkHttpClient client;

    public BangumiApi(OkHttpClient client) {
        this.client = client;
    }

    public List<Subject> search(String keyword) throws IOException, JSONException {
        String url = BASE_URL + "/search/subject/" + keyword + "?type=2&max_results=20&start=0";
        String json = get(url);
        JSONArray list = new JSONObject(json).getJSONArray("list");
        List<Subject> results = new ArrayList<>();
        for (int i = 0; i < list.length(); i++) {
            JSONObject item = list.getJSONObject(i);
            Subject s = new Subject();
            s.id = item.getInt("id");
            s.name = item.optString("name", "");
            s.nameCn = item.optString("name_cn", "");
            s.airDate = item.optString("air_date", "");
            if (item.has("images")) {
                JSONObject imgs = item.getJSONObject("images");
                s.image = imgs.optString("large", imgs.optString("common", ""));
            }
            if (item.has("score")) s.score = item.optDouble("score", 0);
            results.add(s);
        }
        return results;
    }

    public Subject getSubject(int id) throws IOException, JSONException {
        String url = BASE_URL + "/subject/" + id + "?responseGroup=large";
        String json = get(url);
        JSONObject obj = new JSONObject(json);
        Subject s = new Subject();
        s.id = obj.getInt("id");
        s.name = obj.optString("name", "");
        s.nameCn = obj.optString("name_cn", "");
        s.airDate = obj.optString("air_date", "");
        s.summary = obj.optString("summary", "");
        if (obj.has("images")) {
            JSONObject imgs = obj.getJSONObject("images");
            s.image = imgs.optString("large", imgs.optString("common", ""));
        }
        if (obj.has("rating")) {
            s.score = obj.getJSONObject("rating").optDouble("score", 0);
        }
        if (obj.has("eps")) {
            JSONArray eps = obj.getJSONArray("eps");
            for (int i = 0; i < eps.length(); i++) {
                JSONObject ep = eps.getJSONObject(i);
                Episode e = new Episode();
                e.id = ep.getInt("id");
                e.sort = (float) ep.optDouble("sort", i + 1);
                e.name = ep.optString("name", "");
                e.nameCn = ep.optString("name_cn", "");
                s.episodes.add(e);
            }
        }
        return s;
    }

    private String get(String url) throws IOException {
        Request req = new Request.Builder().url(url).header("User-Agent", UA).build();
        try (Response resp = client.newCall(req).execute()) {
            if (!resp.isSuccessful()) throw new IOException("HTTP " + resp.code());
            return resp.body() != null ? resp.body().string() : "{}";
        }
    }
}