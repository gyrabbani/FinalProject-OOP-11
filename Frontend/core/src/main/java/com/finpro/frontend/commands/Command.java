package com.finpro.frontend.commands;

import com.finpro.frontend.PlayerShip;

@FunctionalInterface
public interface Command {
    void execute(PlayerShip player, float delta);
}

