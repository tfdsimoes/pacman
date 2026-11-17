package com.tfdsimoes.pacman.model;

import java.util.Objects;

public final class Ghost {
    private final GhostType type;
    private final Position spawn;
    private Position position;

    public Ghost(GhostType type, Position spawn) {
        this.type = Objects.requireNonNull(type);
        this.spawn = Objects.requireNonNull(spawn);
        position = spawn;
    }

    public GhostType type() {
        return type;
    }

    public Position position() {
        return position;
    }

    public void moveTo(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    public void reset() {
        position = spawn;
    }
}
