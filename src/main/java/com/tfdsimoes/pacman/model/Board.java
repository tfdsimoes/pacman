package com.tfdsimoes.pacman.model;

import java.util.List;
import java.util.Objects;

public final class Board {
    private final Tile[][] tiles;
    private final Position pacmanSpawn;
    private final List<Position> ghostSpawns;

    public Board(Tile[][] tiles, Position pacmanSpawn, List<Position> ghostSpawns) {
        this.tiles = copyTiles(tiles);
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
    }

    public int width() {
        return tiles[0].length;
    }

    public int height() {
        return tiles.length;
    }

    public Tile tileAt(Position position) {
        Objects.requireNonNull(position);
        if (!contains(position)) {
            throw new IndexOutOfBoundsException("Position is outside the board: " + position);
        }
        return tiles[position.y()][position.x()];
    }

    public boolean contains(Position position) {
        return position.x() >= 0 && position.x() < width()
                && position.y() >= 0 && position.y() < height();
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

    private static Tile[][] copyTiles(Tile[][] source) {
        Objects.requireNonNull(source);
        Tile[][] copy = new Tile[source.length][];
        for (int y = 0; y < source.length; y++) {
            copy[y] = source[y].clone();
        }
        return copy;
    }
}
