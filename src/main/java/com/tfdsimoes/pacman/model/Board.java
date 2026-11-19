package com.tfdsimoes.pacman.model;

import java.util.List;
import java.util.Objects;

public final class Board {
    private final Tile[][] tiles;
    private final Tile[][] initialTiles;
    private final Position pacmanSpawn;
    private final List<Position> ghostSpawns;

    public Board(Tile[][] tiles, Position pacmanSpawn, List<Position> ghostSpawns) {
        initialTiles = copyTiles(tiles);
        this.tiles = copyTiles(initialTiles);
        this.pacmanSpawn = Objects.requireNonNull(pacmanSpawn);
        this.ghostSpawns = List.copyOf(ghostSpawns);
        if (this.tiles.length == 0 || this.tiles[0].length == 0) {
            throw new IllegalArgumentException("Board dimensions must be positive");
        }
        for (Tile[] row : this.tiles) {
            if (row.length != this.tiles[0].length) {
                throw new IllegalArgumentException("Board rows must have the same width");
            }
            for (Tile tile : row) {
                Objects.requireNonNull(tile);
            }
        }
        if (!contains(pacmanSpawn) || this.ghostSpawns.stream().anyMatch(spawn -> !contains(spawn))) {
            throw new IllegalArgumentException("Spawn positions must be inside the board");
        }
        if (tileAt(pacmanSpawn) == Tile.WALL
                || this.ghostSpawns.stream().anyMatch(spawn -> tileAt(spawn) == Tile.WALL)) {
            throw new IllegalArgumentException("Spawn positions must be on open tiles");
        }
    }

    public int width() {
        return tiles[0].length;
    }

    public int height() {
        return tiles.length;
    }

    public Tile tileAt(Position position) {
        Objects.requireNonNull(position);
        return tileAt(position.x(), position.y());
    }

    public Tile tileAt(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height()) {
            throw new IndexOutOfBoundsException(
                    "Position is outside the board: Position[x=" + x + ", y=" + y + "]");
        }
        return tiles[y][x];
    }

    public boolean contains(Position position) {
        return position.x() >= 0 && position.x() < width()
                && position.y() >= 0 && position.y() < height();
    }

    public Position neighbor(Position position, Direction direction) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(direction);
        if (!contains(position) || tileAt(position) == Tile.WALL || direction == Direction.STOPPED) {
            return null;
        }
        int nextX = position.x() + direction.dx();
        int nextY = position.y() + direction.dy();
        if (nextX < 0) {
            nextX = width() - 1;
        } else if (nextX >= width()) {
            nextX = 0;
        }
        Position next = new Position(nextX, nextY);
        return contains(next) && tileAt(next) != Tile.WALL ? next : null;
    }

    public Position nearestOpenPosition(Position target) {
        Objects.requireNonNull(target);
        Position nearest = null;
        long bestDistance = Long.MAX_VALUE;
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                Position candidate = new Position(x, y);
                if (tileAt(candidate) == Tile.WALL) {
                    continue;
                }
                long distance = Math.abs((long) candidate.x() - target.x())
                        + Math.abs((long) candidate.y() - target.y());
                if (distance < bestDistance) {
                    nearest = candidate;
                    bestDistance = distance;
                }
            }
        }
        if (nearest == null) {
            throw new IllegalStateException("Board does not contain any open tiles");
        }
        return nearest;
    }

    public Position pacmanSpawn() {
        return pacmanSpawn;
    }

    public List<Position> ghostSpawns() {
        return ghostSpawns;
    }

    public int collectibleCount() {
        int count = 0;
        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                if (tile == Tile.PELLET || tile == Tile.POWER_PELLET) {
                    count++;
                }
            }
        }
        return count;
    }

    public Tile consumeCollectible(Position position) {
        Tile tile = tileAt(position);
        if (tile == Tile.PELLET || tile == Tile.POWER_PELLET) {
            tiles[position.y()][position.x()] = Tile.EMPTY;
            return tile;
        }
        return Tile.EMPTY;
    }

    public void reset() {
        for (int y = 0; y < tiles.length; y++) {
            System.arraycopy(initialTiles[y], 0, tiles[y], 0, tiles[y].length);
        }
    }

    private static Tile[][] copyTiles(Tile[][] source) {
        Objects.requireNonNull(source);
        Tile[][] copy = new Tile[source.length][];
        for (int y = 0; y < source.length; y++) {
            copy[y] = source[y].clone();
        }
        return copy;
    }
}
