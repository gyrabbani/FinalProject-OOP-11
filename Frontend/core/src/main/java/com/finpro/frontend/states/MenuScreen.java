package com.finpro.frontend.states;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.finpro.frontend.services.UserManager;
import com.finpro.frontend.utils.SkinGenerator;
import com.finpro.frontend.Background;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.finpro.frontend.services.ResourceManager;

public class MenuScreen implements Screen {

    private Stage stage;
    private Skin skin;
    private Background background;

    public MenuScreen() {
        stage = new Stage(new ScreenViewport());
        background = new Background();
        skin = SkinGenerator.createBasicSkin();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);


        Texture titleTexture = ResourceManager.getInstance().getTexture("spaceshooter.png");

        Image titleImage = new Image(titleTexture);


        Label userLabel = new Label("Enter Username:", skin);
        final TextField usernameField = new TextField("", skin);
        usernameField.setMessageText("Username...");
        TextButton playButton = new TextButton("START GAME", skin);

        table.add(titleImage).padBottom(50).row();

        table.add(userLabel).padBottom(10).row();
        table.add(usernameField).width(300).height(50).padBottom(20).row();
        table.add(playButton).width(200).height(60);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String username = usernameField.getText().trim();

                if (username.isEmpty()) {
                    System.out.println("Username tidak boleh kosong!");
                    return;
                }

                UserManager.getInstance().loginOrRegister(username);
                ResourceManager.getInstance().playSfx("click.wav");
                ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen());
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        ResourceManager.getInstance().playMusic("menu.mp3", true);
    }

    @Override
    public void render(float delta) {
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
