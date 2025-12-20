package com.finpro.frontend.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import java.util.UUID;

public class UserManager {

    private static final UserManager instance = new UserManager();
    private static final String PREF_NAME = "SpaceShooterUserPrefs";
    private static final String PREF_UUID = "uuid";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_HIGHSCORE = "highscore";

    private String currentUsername;
    private UUID currentUUID;
    private int highScore;

    private Preferences prefs;

    private UserManager() {
        prefs = Gdx.app.getPreferences(PREF_NAME);

        String savedUsername = prefs.getString(PREF_USERNAME, null);
        String savedUUID = prefs.getString(PREF_UUID, null);
        highScore = prefs.getInteger(PREF_HIGHSCORE, 0);

        if (savedUsername != null && savedUUID != null) {
            currentUsername = savedUsername;
            currentUUID = UUID.fromString(savedUUID);
        }
    }

    public static UserManager getInstance() {
        return instance;
    }

    public void setUserFromBackend(UUID uuid, String username, int highScore) {
        this.currentUUID = uuid;
        this.currentUsername = username;
        this.highScore = highScore;

        prefs.putString(PREF_UUID, uuid.toString());
        prefs.putString(PREF_USERNAME, username);
        prefs.putInteger(PREF_HIGHSCORE, highScore);
        prefs.flush();
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
        prefs.clear();
        prefs.flush();
    }
}
