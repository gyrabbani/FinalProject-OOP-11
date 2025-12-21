package com.finpro.frontend.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.UUID;

public class UserManager {
    private static final UserManager instance = new UserManager();
    private static final String PREF_NAME = "SpaceShooterUserPrefs";

    // simpan last session
    private static final String PREF_UUID = "uuid";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_HIGHSCORE = "highscore";

    // map username -> uuid offline (biar user yang sama punya uuid yang sama)
    private static final String KEY_USER_UUID_PREFIX = "user_uuid_";

    private String currentUsername;
    private UUID currentUUID;
    private int highScore;

    private final Preferences prefs;

    private UserManager() {
        prefs = Gdx.app.getPreferences(PREF_NAME);

        String savedUsername = prefs.getString(PREF_USERNAME, null);
        String savedUUID = prefs.getString(PREF_UUID, null);
        highScore = prefs.getInteger(PREF_HIGHSCORE, 0);

        if (savedUsername != null && savedUUID != null) {
            try {
                currentUsername = savedUsername;
                currentUUID = UUID.fromString(savedUUID);
            } catch (Exception ignored) {
                currentUsername = null;
                currentUUID = null;
            }
        }
    }

    public static UserManager getInstance() {
        return instance;
    }

    // === ONLINE RESULT ===
    public void setUserFromBackend(UUID uuid, String username, int highScoreFromServer) {
        this.currentUUID = uuid;
        this.currentUsername = username;
        this.highScore = highScoreFromServer;

        // update session
        prefs.putString(PREF_UUID, uuid.toString());
        prefs.putString(PREF_USERNAME, username);
        prefs.putInteger(PREF_HIGHSCORE, highScoreFromServer);

        // also keep mapping for offline fallback
        prefs.putString(KEY_USER_UUID_PREFIX + username, uuid.toString());

        prefs.flush();
    }

    // === OFFLINE FALLBACK ===
    public void loginOrRegisterOffline(String username) {
        this.currentUsername = username;

        // ambil uuid yang pernah dipakai
        String stored = prefs.getString(KEY_USER_UUID_PREFIX + username, null);

        UUID uuid;
        if (stored != null) {
            try {
                uuid = UUID.fromString(stored);
            } catch (Exception e) {
                uuid = UUID.randomUUID();
            }
        } else {
            uuid = UUID.randomUUID();
        }

        this.currentUUID = uuid;

        // simpan mapping & last session
        prefs.putString(KEY_USER_UUID_PREFIX + username, uuid.toString());
        prefs.putString(PREF_UUID, uuid.toString());
        prefs.putString(PREF_USERNAME, username);

        // NOTE: highScore offline table kamu ada di OfflineHighscoreStore,
        // jadi PREF_HIGHSCORE ini optional. Biar aman tetap simpan.
        prefs.flush();

        System.out.println("OFFLINE LOGIN: " + username + " (" + uuid + ")");
    }

    public boolean isLoggedIn() {
        return currentUUID != null;
    }

    public UUID getCurrentUUID() {
        return currentUUID;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public int getHighScore() {
        return highScore;
    }

    public void updateHighScore(int newHighScore) {
        if (newHighScore > highScore) {
            highScore = newHighScore;
            prefs.putInteger(PREF_HIGHSCORE, newHighScore);
            prefs.flush();
        }
    }

    public void logout() {
        currentUUID = null;
        currentUsername = null;
        highScore = 0;

        prefs.remove(PREF_UUID);
        prefs.remove(PREF_USERNAME);
        prefs.remove(PREF_HIGHSCORE);
        prefs.flush();
    }

    public void clearAllLocal() {
        prefs.clear();
        prefs.flush();
        currentUUID = null;
        currentUsername = null;
        highScore = 0;
    }

}
