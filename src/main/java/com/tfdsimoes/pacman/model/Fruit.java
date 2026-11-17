package com.tfdsimoes.pacman.model;

import java.util.Objects;

public final class Fruit {
    private final Position position;
    private final int value;
    private int ticksRemaining;

    public Fruit(Position position, int value, int lifetimeTicks) {
        this.position = Objects.requireNonNull(position);
        if (value <= 0 || lifetimeTicks <= 0) {
            throw new IllegalArgumentException("Fruit value and lifetime must be positive");
        }
        this.value = value;
        ticksRemaining = lifetimeTicks;
    }

    public Position position() {
        return position;
    }

    public int value() {
        return value;
    }

    public boolean advanceTick() {
        return --ticksRemaining > 0;
    }
}
