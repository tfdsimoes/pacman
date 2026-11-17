package com.tfdsimoes.pacman.model;

import java.util.Objects;

public final class Pacman {
    private Position position;
    private Direction direction = Direction.STOPPED;
    private Direction requestedDirection = Direction.STOPPED;

    public Pacman(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    public Position position() {
        return position;
    }

    public Direction direction() {
        return direction;
    }

    public void requestDirection(Direction direction) {
        requestedDirection = Objects.requireNonNull(direction);
    }

    public void tick(Board board) {
        Objects.requireNonNull(board);
        if (!board.contains(position) || board.tileAt(position) == Tile.WALL) {
            throw new IllegalStateException("Pac-Man must be on an open board tile");
        }
        if (requestedDirection == Direction.STOPPED) {
            direction = Direction.STOPPED;
            return;
        }

        Position requestedPosition = nextPosition(board, requestedDirection);
        if (requestedPosition != null) {
            direction = requestedDirection;
            position = requestedPosition;
            return;
        }

        Position currentPosition = nextPosition(board, direction);
        if (currentPosition != null) {
            position = currentPosition;
        }
    }

    private Position nextPosition(Board board, Direction movement) {
        if (movement == Direction.STOPPED) {
            return null;
        }
        int nextX = position.x() + movement.dx();
        int nextY = position.y() + movement.dy();
        if (nextX < 0) {
            nextX = board.width() - 1;
        } else if (nextX >= board.width()) {
            nextX = 0;
        }
        Position next = new Position(nextX, nextY);
        return board.contains(next) && board.tileAt(next) != Tile.WALL ? next : null;
    }
}
