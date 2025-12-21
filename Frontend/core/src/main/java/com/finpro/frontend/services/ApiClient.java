package com.finpro.frontend.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ApiClient {

    // kalau nanti kamu mau ganti ke IP LAN, tinggal ubah di sini
    private static final String BASE_URL = "http://localhost:8080/api";

    // biar offline cepat ketauan
    private static final int TIMEOUT_MS = 2000;

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
            this.rank = rank;
            this.username = username;
            this.highScore = highScore;
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
        req.setTimeOut(TIMEOUT_MS);

        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int code = httpResponse.getStatus().getStatusCode();
                String res = safeBody(httpResponse);

                if (code < 200 || code >= 300) {
                    cb.onError("Login failed: HTTP " + code + " - " + shorten(res));
                    return;
                }

                try {
                    JsonValue root = new JsonReader().parse(res);
                    UUID uuid = UUID.fromString(root.getString("uuid"));
                    String uname = root.getString("username");
                    int hs = root.getInt("highScore", 0);
                    cb.onSuccess(uuid, uname, hs);
                } catch (Exception e) {
                    cb.onError("Login parse error: " + e.getMessage() + " | body=" + shorten(res));
                }
            }

            @Override
            public void failed(Throwable t) {
                cb.onError("Login network error: " + explainThrowable(t));
            }

            @Override
            public void cancelled() {
                cb.onError("Login cancelled");
            }
        });
    }

    public static void submitGameOver(UUID uuid, int score, GameOverCallback cb) {
        String body = "{\"score\":" + score + "}";

        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.POST);
        req.setUrl(BASE_URL + "/players/" + uuid + "/gameover");
        req.setHeader("Content-Type", "application/json");
        req.setContent(body);
        req.setTimeOut(TIMEOUT_MS);

        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int code = httpResponse.getStatus().getStatusCode();
                String res = safeBody(httpResponse);

                if (code < 200 || code >= 300) {
                    cb.onError("GameOver failed: HTTP " + code + " - " + shorten(res));
                    return;
                }

                try {
                    JsonValue root = new JsonReader().parse(res);
                    int newHighScore = root.getInt("highScore", 0);
                    cb.onSuccess(newHighScore);
                } catch (Exception e) {
                    cb.onError("GameOver parse error: " + e.getMessage() + " | body=" + shorten(res));
                }
            }

            @Override
            public void failed(Throwable t) {
                cb.onError("GameOver network error: " + explainThrowable(t));
            }

            @Override
            public void cancelled() {
                cb.onError("GameOver cancelled");
            }
        });
    }

    public static void fetchLeaderboard(int limit, LeaderboardCallback cb) {
        Net.HttpRequest req = new Net.HttpRequest(Net.HttpMethods.GET);
        req.setUrl(BASE_URL + "/leaderboard?limit=" + limit);
        req.setTimeOut(TIMEOUT_MS);

        Gdx.net.sendHttpRequest(req, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int code = httpResponse.getStatus().getStatusCode();
                String res = safeBody(httpResponse);

                if (code < 200 || code >= 300) {
                    cb.onError("Leaderboard failed: HTTP " + code + " - " + shorten(res));
                    return;
                }

                try {
                    JsonValue arr = new JsonReader().parse(res);
                    List<LeaderboardEntry> list = new ArrayList<>();

                    for (JsonValue it = arr.child; it != null; it = it.next) {
                        int rank = it.getInt("rank", 0);
                        String username = it.getString("username", "-");
                        int hs = it.getInt("highScore", 0);
                        list.add(new LeaderboardEntry(rank, username, hs));
                    }

                    cb.onSuccess(list);
                } catch (Exception e) {
                    cb.onError("Leaderboard parse error: " + e.getMessage() + " | body=" + shorten(res));
                }
            }

            @Override
            public void failed(Throwable t) {
                cb.onError("Leaderboard network error: " + explainThrowable(t));
            }

            @Override
            public void cancelled() {
                cb.onError("Leaderboard cancelled");
            }
        });
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String safeBody(Net.HttpResponse r) {
        try {
            String s = r.getResultAsString();
            return (s == null) ? "" : s;
        } catch (Exception e) {
            return "";
        }
    }

    private static String shorten(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.length() <= 200) return s;
        return s.substring(0, 200) + "...";
    }

    private static String explainThrowable(Throwable t) {
        if (t == null) return "unknown";
        String msg = t.getMessage();
        if (msg == null || msg.trim().isEmpty()) msg = t.toString();
        return msg;
    }
}
