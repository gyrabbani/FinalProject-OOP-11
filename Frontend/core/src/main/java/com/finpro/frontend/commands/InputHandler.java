package com.finpro.frontend.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.finpro.frontend.PlayerShip;

public class InputHandler {

    private Command moveLeft;
    private Command moveRight;
    private Command moveUp;
    private Command moveDown;
    private Command shoot;

    public InputHandler() {
        moveLeft  = (player, delta) -> player.move(-1, 0, delta);
        moveRight = (player, delta) -> player.move(1, 0, delta);
        moveUp    = (player, delta) -> player.move(0, 1, delta);
        moveDown  = (player, delta) -> player.move(0, -1, delta);

        shoot     = (player, delta) -> player.performShoot();
    }

    public void handleInput(PlayerShip player, float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveLeft.execute(player, delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveRight.execute(player, delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            moveUp.execute(player, delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            moveDown.execute(player, delta);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            shoot.execute(player, delta);
        }
    }
}

