package com.finpro.frontend.states;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.finpro.frontend.Background;
import com.finpro.frontend.services.ApiClient;
import com.finpro.frontend.services.OfflineHighscoreStore;
import com.finpro.frontend.services.SyncService;
import com.finpro.frontend.services.UserManager;
import com.finpro.frontend.utils.SkinGenerator;

import java.util.List;

public class HighscoreScreen implements Screen {

    private enum Mode { ONLINE, OFFLINE }

    private final Stage stage;
    private final Skin skin;

    private final Table root;
    private final Table scoreTable;

    private final Label titleLabel;
    private final Label statusLabel;

    private final TextButton onlineButton;
    private final TextButton offlineButton;
    private final TextButton refreshButton;
    private final TextButton backButton;
    private final TextButton clearOfflineButton;

    private final ScrollPane scrollPane;

    private Background background;

    private Mode currentMode = Mode.ONLINE;

    public HighscoreScreen() {
        stage = new Stage(new ScreenViewport());
        skin = SkinGenerator.createBasicSkin();
        background = new Background();

        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        titleLabel = new Label("HIGHSCORE", skin);
        titleLabel.setAlignment(Align.center);

        statusLabel = new Label("", skin);
        statusLabel.setAlignment(Align.center);

        onlineButton = new TextButton("ONLINE", skin);
        offlineButton = new TextButton("OFFLINE", skin);
        refreshButton = new TextButton("REFRESH", skin);
        backButton = new TextButton("BACK", skin);
        clearOfflineButton = new TextButton("DELETE DATA", skin);

        scoreTable = new Table(skin);
        scrollPane = new ScrollPane(scoreTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false); // only vertical

        // ===== Layout =====
        root.top().padTop(25);

        root.add(titleLabel).padBottom(12).row();

        // row: ONLINE / OFFLINE / REFRESH
        Table topButtons = new Table();
        topButtons.add(onlineButton).width(150).height(50).padRight(10);
        topButtons.add(offlineButton).width(150).height(50).padRight(10);
        topButtons.add(refreshButton).width(150).height(50).padRight(10);
        topButtons.add(clearOfflineButton).width(150).height(50);

        root.add(topButtons).padBottom(10).row();
        root.add(statusLabel).padBottom(10).row();

        root.add(scrollPane).width(650).height(420).padBottom(18).row();
        root.add(backButton).width(160).height(55);

        // ===== Listeners =====
        onlineButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                setMode(Mode.ONLINE);
            }
        });

        offlineButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                setMode(Mode.OFFLINE);
            }
        });

        refreshButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                refreshCurrentMode();
            }
        });

        backButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
            }
        });

        clearOfflineButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                if (currentMode != Mode.OFFLINE) return;
                OfflineHighscoreStore.getInstance().clearAll();
                UserManager.getInstance().clearAllLocal();
                statusLabel.setText("OFFLINE DATA DELETED");
                loadLeaderboardOffline(); // refresh tabel offline
            }
        });

        // initial UI state
        updateButtonVisuals();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // Default buka ONLINE (sesuai yang kamu mau)
        setMode(Mode.ONLINE);
    }

    private void setMode(Mode mode) {
        this.currentMode = mode;
        updateButtonVisuals();
        refreshCurrentMode();
    }

    private void refreshCurrentMode() {
        if (currentMode == Mode.ONLINE) {
            // offline table tetap ada, tapi kalau terkoneksi harus sync ke online
            statusLabel.setText("ONLINE MODE (syncing...)");
            SyncService.trySyncOfflineToOnline();

            // load leaderboard online
            loadLeaderboardOnline();
        } else {
            loadLeaderboardOffline();
        }
    }

    private void updateButtonVisuals() {
        clearOfflineButton.setVisible(currentMode == Mode.OFFLINE);
    }

    private void loadLeaderboardOnline() {
        statusLabel.setText("ONLINE MODE (loading...)");
        scoreTable.clear();

        scoreTable.add(new Label("Rank", skin)).pad(8);
        scoreTable.add(new Label("Username", skin)).pad(8);
        scoreTable.add(new Label("Highscore", skin)).pad(8);
        scoreTable.row();

        ApiClient.fetchLeaderboard(50, new ApiClient.LeaderboardCallback() {
            @Override
            public void onSuccess(List<ApiClient.LeaderboardEntry> entries) {
                Gdx.app.postRunnable(() -> {
                    statusLabel.setText("ONLINE MODE");

                    if (entries.isEmpty()) {
                        scoreTable.add(new Label("-", skin)).pad(8);
                        scoreTable.add(new Label("No data yet", skin)).pad(8);
                        scoreTable.add(new Label("-", skin)).pad(8);
                        scoreTable.row();
                        return;
                    }

                    for (ApiClient.LeaderboardEntry e : entries) {
                        scoreTable.add(new Label(String.valueOf(e.rank), skin)).pad(8);
                        scoreTable.add(new Label(e.username, skin)).pad(8);
                        scoreTable.add(new Label(String.valueOf(e.highScore), skin)).pad(8);
                        scoreTable.row();
                    }
                });
            }

            @Override
            public void onError(String msg) {
                Gdx.app.postRunnable(() -> {
                    statusLabel.setText("OFFLINE - showing local table");
                    currentMode = Mode.OFFLINE;
                    updateButtonVisuals();
                    loadLeaderboardOffline();
                });
            }
        });
    }

    private void loadLeaderboardOffline() {
        statusLabel.setText("OFFLINE MODE (local table)");
        scoreTable.clear();

        scoreTable.add(new Label("Username", skin)).pad(8);
        scoreTable.add(new Label("Highscore", skin)).pad(8);
        scoreTable.add(new Label("Synced", skin)).pad(8);
        scoreTable.row();

        com.badlogic.gdx.utils.Array<OfflineHighscoreStore.OfflineEntry> list =
            OfflineHighscoreStore.getInstance().loadTable();

        if (list.size == 0) {
            scoreTable.add(new Label("-", skin)).pad(8);
            scoreTable.add(new Label("No offline data", skin)).pad(8);
            scoreTable.add(new Label("-", skin)).pad(8);
            scoreTable.row();
            return;
        }

        // sort desc by highScoreOffline
        for (int i = 0; i < list.size - 1; i++) {
            for (int j = i + 1; j < list.size; j++) {
                if (list.get(j).highScoreOffline > list.get(i).highScoreOffline) {
                    OfflineHighscoreStore.OfflineEntry tmp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, tmp);
                }
            }
        }

        for (int i = 0; i < list.size; i++) {
            OfflineHighscoreStore.OfflineEntry e = list.get(i);

            scoreTable.add(new Label(e.username, skin)).pad(8);
            scoreTable.add(new Label(String.valueOf(e.highScoreOffline), skin)).pad(8);
            scoreTable.add(new Label(e.needsSync() ? "NO" : "YES", skin)).pad(8);
            scoreTable.row();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        background.update(delta);
        stage.getBatch().begin();
        background.render((SpriteBatch) stage.getBatch());
        stage.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
