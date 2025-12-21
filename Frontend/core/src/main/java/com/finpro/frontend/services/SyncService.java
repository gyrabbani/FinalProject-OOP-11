package com.finpro.frontend.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import java.util.UUID;

public class SyncService {

    private static boolean syncing = false;

    public static void trySyncOfflineToOnline() {
        if (syncing) return;

        // Ping sederhana: ambil leaderboard kecil. Kalau sukses berarti online.
        ApiClient.fetchLeaderboard(1, new ApiClient.LeaderboardCallback() {
            @Override
            public void onSuccess(java.util.List<ApiClient.LeaderboardEntry> entries) {
                syncNowSequential();
            }

            @Override
            public void onError(String msg) {
                System.out.println("Sync skipped (offline): " + msg);
            }
        });
    }

    private static void syncNowSequential() {
        if (syncing) return;
        syncing = true;

        Array<OfflineHighscoreStore.OfflineEntry> table =
            OfflineHighscoreStore.getInstance().loadTable();

        // cari entry pertama yang butuh sync
        syncNext(table, 0);
    }

    private static void syncNext(Array<OfflineHighscoreStore.OfflineEntry> table, int startIndex) {
        // cari yang needsSync
        OfflineHighscoreStore.OfflineEntry target = null;
        for (int i = startIndex; i < table.size; i++) {
            OfflineHighscoreStore.OfflineEntry e = table.get(i);
            if (e == null || e.uuid == null) continue;
            if (!e.needsSync()) continue;

            target = e;
            break;
        }

        if (target == null) {
            syncing = false;
            System.out.println("Sync complete (nothing else to sync).");
            return;
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(target.uuid);
        } catch (Exception ex) {
            // uuid invalid -> skip lanjut
            syncNext(table, startIndex + 1);
            return;
        }

        final UUID uuidSnapshot = uuid;
        final String usernameSnapshot = target.username == null ? "-" : target.username;
        final int scoreSnapshot = target.highScoreOffline;

        ApiClient.submitGameOver(uuidSnapshot, scoreSnapshot, new ApiClient.GameOverCallback() {
            @Override
            public void onSuccess(int newHighScore) {
                // tandai synced sampai skor yang benar-benar terkirim
                Gdx.app.postRunnable(() ->
                    OfflineHighscoreStore.getInstance().markSyncedScore(uuidSnapshot, scoreSnapshot)
                );
                System.out.println("Synced offline -> online for " + usernameSnapshot + " score=" + scoreSnapshot);

                // lanjut sync berikutnya
                Gdx.app.postRunnable(() -> syncNext(table, startIndex + 1));
            }

            @Override
            public void onError(String msg) {
                // gagal -> stop dulu biar gak spam terus menerus
                System.out.println("Sync failed for " + usernameSnapshot + ": " + msg);
                syncing = false;
            }
        });
    }
}
