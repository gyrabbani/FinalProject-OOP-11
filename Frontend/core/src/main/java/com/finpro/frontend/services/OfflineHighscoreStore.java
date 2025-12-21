package com.finpro.frontend.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OfflineHighscoreStore {

    private static final OfflineHighscoreStore instance = new OfflineHighscoreStore();
    private static final String PREF_NAME = "SpaceShooterOfflineHS";
    private static final String KEY_TABLE_JSON = "offline_table_json";

    private final Preferences prefs;
    private final Json json;

    private OfflineHighscoreStore() {
        prefs = Gdx.app.getPreferences(PREF_NAME);
        json = new Json();
    }

    public static OfflineHighscoreStore getInstance() {
        return instance;
    }

    public static class OfflineEntry {
        public String uuid;
        public String username;
        public int highScoreOffline;   // skor terbaik offline
        public int lastSyncedScore;    // skor terakhir yang sukses terkirim ke online

        public OfflineEntry() {}

        public OfflineEntry(String uuid, String username, int highScoreOffline, int lastSyncedScore) {
            this.uuid = uuid;
            this.username = username;
            this.highScoreOffline = highScoreOffline;
            this.lastSyncedScore = lastSyncedScore;
        }

        public boolean needsSync() {
            return highScoreOffline > lastSyncedScore;
        }
    }

    public void recordScore(UUID uuid, String username, int score) {
        Array<OfflineEntry> table = loadTable();

        // cari entry by uuid
        OfflineEntry found = null;
        for (OfflineEntry e : table) {
            if (e.uuid != null && e.uuid.equals(uuid.toString())) {
                found = e;
                break;
            }
        }

        if (found == null) {
            // user baru di offline table
            table.add(new OfflineEntry(uuid.toString(), username, score, 0));
        } else {
            // update best offline
            if (score > found.highScoreOffline) found.highScoreOffline = score;
            // keep username updated
            found.username = username;
        }

        saveTable(table);
    }

    public void markSynced(UUID uuid) {
        Array<OfflineEntry> table = loadTable();
        for (OfflineEntry e : table) {
            if (e.uuid != null && e.uuid.equals(uuid.toString())) {
                e.lastSyncedScore = e.highScoreOffline; // sudah sinkron sampai best offline
                break;
            }
        }
        saveTable(table);
    }

    public void markSyncedScore(UUID uuid, int syncedScore) {
        Array<OfflineEntry> table = loadTable();
        for (OfflineEntry e : table) {
            if (e.uuid != null && e.uuid.equals(uuid.toString())) {
                // pastikan lastSyncedScore tidak mundur
                if (syncedScore > e.lastSyncedScore) {
                    e.lastSyncedScore = syncedScore;
                }
                break;
            }
        }
        saveTable(table);
    }

    public Array<OfflineEntry> loadTable() {
        String s = prefs.getString(KEY_TABLE_JSON, "");
        if (s == null || s.isEmpty()) return new Array<>();
        try {
            return json.fromJson(Array.class, OfflineEntry.class, s);
        } catch (Exception e) {
            return new Array<>();
        }
    }

    private void saveTable(Array<OfflineEntry> table) {
        prefs.putString(KEY_TABLE_JSON, json.toJson(table));
        prefs.flush();
    }

    public void clearAll() {
        prefs.clear();
        prefs.flush();
    }
}
