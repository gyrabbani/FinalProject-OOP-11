package com.finpro.frontend.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";

    public interface LoginCallback {
        void onSuccess(UUID uuid, String username, int highScore);
        void onError(String msg);
    }

    public interface GameOverCallback {
        void onSuccess(int newHighScore);
        void onError(String msg);
    }

    public static class LeaderboardEntry {
        public final int rank;
        public final String username;
        public final int highScore;
        public LeaderboardEntry(int rank, String username, int highScore) {
            this.rank = rank; this.username = username; this.highScore = highScore;
        }
    }

    public interface LeaderboardCallback {
        void onSuccess(List<LeaderboardEntry> entries);
        void onError(String msg);
    }

    public static void login(String username, LoginCallback cb) {
        String body = "{\"username\":\"" + escape(username) + "\"}";

        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.POST);
        req.setUrl(BASE_URL + "/players/login");
        req.setHeader("Content-Type", "application/json");
        req.setContent(body);

        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int code = httpResponse.getStatus().getStatusCode();
                String res = httpResponse.getResultAsString();
                if (code < 200 || code >= 300) { cb.onError("HTTP " + code + ": " + res); return; }

                JsonValue root = new JsonReader().parse(res);
                UUID uuid = UUID.fromString(root.getString("uuid"));
                String uname = root.getString("username");
                int hs = root.getInt("highScore");
                cb.onSuccess(uuid, uname, hs);
            }
            @Override public void failed(Throwable t) { cb.onError(t.getMessage()); }
            @Override public void cancelled() { cb.onError("cancelled"); }
        });
    }

    public static void submitGameOver(UUID uuid, int score, GameOverCallback cb) {
        String body = "{\"score\":" + score + "}";

        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.POST);
        req.setUrl(BASE_URL + "/players/" + uuid + "/gameover");
        req.setHeader("Content-Type", "application/json");
        req.setContent(body);

        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int code = httpResponse.getStatus().getStatusCode();
                String res = httpResponse.getResultAsString();
                if (code < 200 || code >= 300) { cb.onError("HTTP " + code + ": " + res); return; }

                JsonValue root = new JsonReader().parse(res);
                int newHighScore = root.getInt("highScore");
                cb.onSuccess(newHighScore);
            }
            @Override public void failed(Throwable t) { cb.onError(t.getMessage()); }
            @Override public void cancelled() { cb.onError("cancelled"); }
        });
    }

    public static void fetchLeaderboard(int limit, LeaderboardCallback cb) {
        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.GET);
        req.setUrl(BASE_URL + "/leaderboard?limit=" + limit);

        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int code = httpResponse.getStatus().getStatusCode();
                String res = httpResponse.getResultAsString();
                if (code < 200 || code >= 300) { cb.onError("HTTP " + code + ": " + res); return; }

                JsonValue arr = new JsonReader().parse(res);
                List<LeaderboardEntry> list = new ArrayList<>();
                for (JsonValue it = arr.child; it != null; it = it.next) {
                    int rank = it.getInt("rank");
                    String username = it.getString("username");
                    int hs = it.getInt("highScore");
                    list.add(new LeaderboardEntry(rank, username, hs));
                }
                cb.onSuccess(list);
            }
            @Override public void failed(Throwable t) { cb.onError(t.getMessage()); }
            @Override public void cancelled() { cb.onError("cancelled"); }
        });
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
