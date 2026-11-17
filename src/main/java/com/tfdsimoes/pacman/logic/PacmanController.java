package com.tfdsimoes.pacman.logic;

import com.tfdsimoes.pacman.model.Direction;

@FunctionalInterface
public interface PacmanController {
    Direction chooseDirection(GameSession game);
}
