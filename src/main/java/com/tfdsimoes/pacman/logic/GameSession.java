package com.tfdsimoes.pacman.logic;

import com.tfdsimoes.pacman.model.Board;
import com.tfdsimoes.pacman.model.Pacman;
import com.tfdsimoes.pacman.model.Tile;
import java.util.Objects;

public final class GameSession {
    private final Board board;
    private final Pacman pacman;
    private int score;

    public GameSession(Board board, Pacman pacman) {
        this.board = Objects.requireNonNull(board);
        this.pacman = Objects.requireNonNull(pacman);
    }

    public Board board() {
        return board;
    }

    public Pacman pacman() {
        return pacman;
    }

    public int score() {
        return score;
    }

    public boolean levelComplete() {
        return board.collectibleCount() == 0;
    }

    public void tick() {
        if (levelComplete()) {
            return;
        }
        pacman.tick(board);
        Tile eaten = board.consumeCollectible(pacman.position());
        score += switch (eaten) {
            case PELLET -> 10;
            case POWER_PELLET -> 50;
            default -> 0;
        };
    }
}
