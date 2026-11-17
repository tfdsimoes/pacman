package com.tfdsimoes.pacman.logic;

import com.tfdsimoes.pacman.GameConfig;
import com.tfdsimoes.pacman.model.Board;
import com.tfdsimoes.pacman.model.Ghost;
import com.tfdsimoes.pacman.model.GhostMode;
import com.tfdsimoes.pacman.model.GhostStrategyMode;
import com.tfdsimoes.pacman.model.GhostType;
import com.tfdsimoes.pacman.model.Fruit;
import com.tfdsimoes.pacman.model.Pacman;
import com.tfdsimoes.pacman.model.Tile;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class GameSession {
    private final Board board;
    private final Pacman pacman;
    private final List<Ghost> ghosts;
    private final GhostAi ghostAi = new GhostAi();
    private final FruitManager fruitManager = new FruitManager();
    private final PacmanController pacmanController;
    private int score;
    private int lives = GameConfig.STARTING_LIVES;
    private int level = 1;
    private int levelClearTicksRemaining;
    private int elapsedTicks;

    public GameSession(Board board, Pacman pacman) {
        this(board, pacman, null);
    }

    public GameSession(Board board, Pacman pacman, PacmanController pacmanController) {
        this.board = Objects.requireNonNull(board);
        this.pacman = Objects.requireNonNull(pacman);
        this.pacmanController = pacmanController;
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

    public int level() {
        return level;
    }

    public boolean gameOver() {
        return lives == 0;
    }

    public int tickIntervalMillis() {
        return (int) Math.max(GameConfig.MIN_LOGIC_TICK_MILLIS,
                GameConfig.BASE_LOGIC_TICK_MILLIS - (long) (level - 1) * GameConfig.LEVEL_SPEEDUP_MILLIS);
    }

    public List<Ghost> ghosts() {
        return ghosts;
    }

    public Optional<Fruit> activeFruit() {
        return fruitManager.activeFruit();
    }

    public GhostStrategyMode ghostBehaviorMode() {
        return GhostBehaviorSchedule.modeAt(elapsedTicks);
    }

    public boolean levelComplete() {
        return board.collectibleCount() == 0;
    }

    public void tick() {
        if (gameOver()) {
            return;
        }
        ghosts.forEach(Ghost::beginTick);
        if (levelComplete()) {
            if (levelClearTicksRemaining > 0 && --levelClearTicksRemaining == 0) {
                advanceLevel();
            }
            return;
        }
        if (pacmanController != null) {
            pacman.requestDirection(pacmanController.chooseDirection(this));
        }
        ghosts.forEach(Ghost::advanceModeTick);
        fruitManager.advance(board, level);
        pacman.tick(board);
        Tile eaten = board.consumeCollectible(pacman.position());
        score += switch (eaten) {
            case PELLET -> GameConfig.PELLET_SCORE;
            case POWER_PELLET -> GameConfig.POWER_PELLET_SCORE;
            default -> 0;
        };
        score += fruitManager.collectAt(pacman.position());
        if (eaten == Tile.POWER_PELLET) {
            ghosts.forEach(ghost -> ghost.frightenFor(GameConfig.FRIGHTENED_DURATION_TICKS));
        }
        boolean levelCleared = levelComplete();
        if (levelCleared) {
            levelClearTicksRemaining = GameConfig.LEVEL_CLEAR_TICKS;
        }
        if (resolveCollisions()) {
            elapsedTicks++;
            return;
        }
        if (levelCleared) {
            elapsedTicks++;
            return;
        }
        moveGhosts();
        resolveCollisions();
        elapsedTicks++;
    }

    private void advanceLevel() {
        level++;
        board.reset();
        pacman.reset(board.pacmanSpawn());
        ghosts.forEach(Ghost::reset);
        fruitManager.reset();
        elapsedTicks = 0;
    }

    private boolean resolveCollisions() {
        for (Ghost ghost : ghosts) {
            if (!isCollision(pacman, ghost)) {
                continue;
            }
            if (ghost.mode() == GhostMode.FRIGHTENED) {
                score += GameConfig.GHOST_EAT_SCORE;
                ghost.eat();
            } else if (ghost.mode() == GhostMode.NORMAL) {
                lives--;
                pacman.reset(board.pacmanSpawn());
                ghosts.forEach(Ghost::reset);
                return true;
            }
        }
        return false;
    }

    static boolean isCollision(Pacman pacman, Ghost ghost) {
        return ghost.position().equals(pacman.position())
                || (ghost.previousPosition().equals(pacman.position())
                        && ghost.position().equals(pacman.previousPosition()));
    }

    private void moveGhosts() {
        GhostStrategyMode behaviorMode = ghostBehaviorMode();
        for (Ghost ghost : ghosts) {
            if (ghost.mode() == GhostMode.EATEN) {
                continue;
            }
            var direction = ghostAi.chooseDirection(board, ghost, pacman, behaviorMode, ghosts);
            var nextPosition = board.neighbor(ghost.position(), direction);
            if (nextPosition != null) {
                ghost.move(direction, nextPosition);
            }
        }
    }
}
