package com.tfdsimoes.pacman.logic;

import com.tfdsimoes.pacman.GameConfig;
import com.tfdsimoes.pacman.model.Board;
import com.tfdsimoes.pacman.model.Fruit;
import com.tfdsimoes.pacman.model.Position;
import java.util.Objects;
import java.util.Optional;

public final class FruitManager {
    private long elapsedTicks;
    private long nextSpawnTick = GameConfig.FIRST_FRUIT_SPAWN_TICK;
    private Fruit fruit;

    public Optional<Fruit> activeFruit() {
        return Optional.ofNullable(fruit);
    }

    public void advance(Board board, int level) {
        Objects.requireNonNull(board);
        if (level <= 0) {
            throw new IllegalArgumentException("Level must be positive");
        }
        elapsedTicks++;
        if (fruit != null && !fruit.advanceTick()) {
            fruit = null;
        }
        if (fruit == null && elapsedTicks >= nextSpawnTick) {
            Position center = new Position(board.width() / 2, board.height() / 2);
            fruit = new Fruit(board.nearestOpenPosition(center),
                    Math.multiplyExact(GameConfig.FRUIT_BASE_VALUE, level), GameConfig.FRUIT_LIFETIME_TICKS);
            nextSpawnTick += GameConfig.FRUIT_SPAWN_INTERVAL_TICKS;
        }
    }

    public int collectAt(Position position) {
        Objects.requireNonNull(position);
        if (fruit == null || !fruit.position().equals(position)) {
            return 0;
        }
        int value = fruit.value();
        fruit = null;
        return value;
    }

    public void reset() {
        elapsedTicks = 0;
        nextSpawnTick = GameConfig.FIRST_FRUIT_SPAWN_TICK;
        fruit = null;
    }
}
