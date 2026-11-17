package com.tfdsimoes.pacman.logic;

import com.tfdsimoes.pacman.model.Board;
import com.tfdsimoes.pacman.model.Fruit;
import com.tfdsimoes.pacman.model.Position;
import java.util.Objects;
import java.util.Optional;

public final class FruitManager {
    private static final long FIRST_SPAWN_TICK = 60;
    private static final long SPAWN_INTERVAL_TICKS = 120;
    private static final int FRUIT_LIFETIME_TICKS = 70;
    private static final int BASE_FRUIT_VALUE = 100;

    private long elapsedTicks;
    private long nextSpawnTick = FIRST_SPAWN_TICK;
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
                    Math.multiplyExact(BASE_FRUIT_VALUE, level), FRUIT_LIFETIME_TICKS);
            nextSpawnTick += SPAWN_INTERVAL_TICKS;
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
        nextSpawnTick = FIRST_SPAWN_TICK;
        fruit = null;
    }
}
