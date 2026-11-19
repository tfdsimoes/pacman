package com.tfdsimoes.pacman.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class BoardTest {
    @Test
    void rejectsPacmanSpawnOnWall() {
        Tile[][] tiles = {
            {Tile.WALL, Tile.WALL, Tile.WALL},
            {Tile.WALL, Tile.EMPTY, Tile.WALL},
            {Tile.WALL, Tile.WALL, Tile.WALL}
        };

        assertThrows(IllegalArgumentException.class,
                () -> new Board(tiles, new Position(0, 0), List.of(new Position(1, 1))));
    }

    @Test
    void rejectsGhostSpawnOnWall() {
        Tile[][] tiles = {
            {Tile.WALL, Tile.WALL, Tile.WALL},
            {Tile.WALL, Tile.EMPTY, Tile.WALL},
            {Tile.WALL, Tile.WALL, Tile.WALL}
        };

        assertThrows(IllegalArgumentException.class,
                () -> new Board(tiles, new Position(1, 1), List.of(new Position(0, 0))));
    }
}
