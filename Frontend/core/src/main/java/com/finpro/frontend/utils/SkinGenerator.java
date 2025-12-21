package com.finpro.frontend.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class SkinGenerator {

    public static Skin createBasicSkin() {
        Skin skin = new Skin();

        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.5f);
        skin.add("default", font);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        TextField.TextFieldStyle fieldStyle = new TextField.TextFieldStyle();
        fieldStyle.font = skin.getFont("default");
        fieldStyle.fontColor = Color.WHITE;
        fieldStyle.cursor = skin.newDrawable("white", Color.GREEN);
        fieldStyle.selection = skin.newDrawable("white", Color.BLUE);
        fieldStyle.background = skin.newDrawable("white", Color.DARK_GRAY);
        skin.add("default", fieldStyle);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = skin.getFont("default");
        buttonStyle.up = skin.newDrawable("white", Color.GRAY);
        buttonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        buttonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        skin.add("default", buttonStyle);

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.background = skin.newDrawable("white", new Color(0, 0, 0, 0.35f)); // transparan gelap
        scrollStyle.vScroll = skin.newDrawable("white", new Color(1, 1, 1, 0.15f));    // track
        scrollStyle.vScrollKnob = skin.newDrawable("white", new Color(1, 1, 1, 0.60f)); // knob
        skin.add("default", scrollStyle);

        pixmap.dispose();
        return skin;
    }
}
