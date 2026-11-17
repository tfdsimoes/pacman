package com.tfdsimoes.pacman.logic;

import com.tfdsimoes.pacman.model.Board;
import com.tfdsimoes.pacman.model.Ghost;
import com.tfdsimoes.pacman.model.GhostMode;
import com.tfdsimoes.pacman.model.GhostType;
import com.tfdsimoes.pacman.model.Pacman;
import com.tfdsimoes.pacman.model.Tile;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class GameSession {
    private static final int FRIGHTENED_DURATION_TICKS = 70;
    private static final int GHOST_EAT_SCORE = 200;

    private final Board board;
    private final Pacman pacman;
    private final List<Ghost> ghosts;
    private int score;
    private int lives = 3;

    public GameSession(Board board, Pacman pacman) {
        this.board = Objects.requireNonNull(board);
        this.pacman = Objects.requireNonNull(pacman);
        List<Ghost> ghosts = new ArrayList<>();
        for (int index = 0; index < board.ghostSpawns().size(); index++) {
            if (index >= GhostType.values().length) {
                throw new IllegalArgumentException("Board supports at most four ghost spawns");
            }
            ghosts.add(new Ghost(GhostType.values()[index], board.ghostSpawns().get(index)));
        }
        this.ghosts = List.copyOf(ghosts);
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

    public int lives() {
        return lives;
    }

    public List<Ghost> ghosts() {
        return ghosts;
    }

    public boolean levelComplete() {
        return board.collectibleCount() == 0;
    }

    public void tick() {
        if (levelComplete() || lives == 0) {
            return;
        }
        ghosts.forEach(Ghost::advanceModeTick);
        pacman.tick(board);
        Tile eaten = board.consumeCollectible(pacman.position());
        score += switch (eaten) {
            case PELLET -> 10;
            case POWER_PELLET -> 50;
            default -> 0;
        };
        if (eaten == Tile.POWER_PELLET) {
            ghosts.forEach(ghost -> ghost.frightenFor(FRIGHTENED_DURATION_TICKS));
        }
        for (Ghost ghost : ghosts) {
            if (!ghost.position().equals(pacman.position())) {
                continue;
            }
            if (ghost.mode() == GhostMode.FRIGHTENED) {
                score += GHOST_EAT_SCORE;
                ghost.eat();
            } else if (ghost.mode() == GhostMode.NORMAL) {
                lives--;
                pacman.reset(board.pacmanSpawn());
                ghosts.forEach(Ghost::reset);
                break;
            }
        }
    }
}
