package com.tfdsimoes.pacman.model;

import java.util.Objects;

public final class Ghost {
    private static final int EATEN_RETURN_TICKS = 10;

    private final GhostType type;
    private final Position spawn;
    private Position position;
    private Position previousPosition;
    private Direction direction = Direction.STOPPED;
    private GhostMode mode = GhostMode.NORMAL;
    private int frightenedTicksRemaining;
    private int eatenTicksRemaining;

    public Ghost(GhostType type, Position spawn) {
        this.type = Objects.requireNonNull(type);
        this.spawn = Objects.requireNonNull(spawn);
        position = spawn;
        previousPosition = spawn;
    }

    public GhostType type() {
        return type;
    }

    public Position position() {
        return position;
    }

    public Position previousPosition() {
        return previousPosition;
    }

    public GhostMode mode() {
        return mode;
    }

    public Position spawn() {
        return spawn;
    }

    public Direction direction() {
        return direction;
    }

    public void move(Direction direction, Position position) {
        this.direction = Objects.requireNonNull(direction);
        previousPosition = this.position;
        this.position = Objects.requireNonNull(position);
    }

    public void beginTick() {
        previousPosition = position;
    }

    public void frightenFor(int ticks) {
        if (ticks <= 0) {
            throw new IllegalArgumentException("Frightened duration must be positive");
        }
        if (mode != GhostMode.EATEN) {
            mode = GhostMode.FRIGHTENED;
            frightenedTicksRemaining = ticks;
        }
    }

    public void advanceModeTick() {
        if (mode == GhostMode.FRIGHTENED && --frightenedTicksRemaining <= 0) {
            mode = GhostMode.NORMAL;
        } else if (mode == GhostMode.EATEN && --eatenTicksRemaining <= 0) {
            mode = GhostMode.NORMAL;
        }
    }

    public void eat() {
        position = spawn;
        previousPosition = spawn;
        mode = GhostMode.EATEN;
        frightenedTicksRemaining = 0;
        eatenTicksRemaining = EATEN_RETURN_TICKS;
    }

    public void reset() {
        position = spawn;
        previousPosition = spawn;
        direction = Direction.STOPPED;
        mode = GhostMode.NORMAL;
        frightenedTicksRemaining = 0;
        eatenTicksRemaining = 0;
    }
}
