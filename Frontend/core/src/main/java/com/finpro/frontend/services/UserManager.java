package com.finpro.frontend.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import java.util.UUID;

public class UserManager {
    private static final UserManager instance = new UserManager();
    private static final String PREF_NAME = "SpaceShooterUserPrefs";

    private String currentUsername;
    private String currentUUID;
    private Preferences prefs;

    private UserManager() {
        prefs = Gdx.app.getPreferences(PREF_NAME);
    }

    public static UserManager getInstance() {
        return instance;
    }

    public void loginOrRegister(String username) {
        this.currentUsername = username;

        String existingUUID = prefs.getString(username, null);

        if (existingUUID != null) {
            this.currentUUID = existingUUID;
            System.out.println("Login Berhasil! Welcome back, " + username + " (" + currentUUID + ")");
        } else {
            this.currentUUID = UUID.randomUUID().toString();

            prefs.putString(username, this.currentUUID);
            prefs.flush();

            System.out.println("User Baru Dibuat! Hello, " + username + " (" + currentUUID + ")");
        }
    }

    public String getCurrentUsername() { return currentUsername; }
    public String getCurrentUUID() { return currentUUID; }
}
