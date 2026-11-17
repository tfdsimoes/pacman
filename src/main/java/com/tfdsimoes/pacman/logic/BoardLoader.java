package com.tfdsimoes.pacman.logic;

import com.tfdsimoes.pacman.model.Board;
import com.tfdsimoes.pacman.model.Position;
import com.tfdsimoes.pacman.model.Tile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class BoardLoader {
    private BoardLoader() {
    }

    public static Board loadResource(String resourceName) throws IOException {
        Objects.requireNonNull(resourceName);
        InputStream input = BoardLoader.class.getResourceAsStream(resourceName);
        if (input == null) {
            throw new IOException("Board resource not found: " + resourceName);
        }
        try (input) {
            return load(input);
        }
    }

    public static Board load(InputStream input) throws IOException {
        Objects.requireNonNull(input);
        List<String> rows = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))
                .lines()
                .toList();
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("Board layout must not be empty");
        }

        int width = rows.get(0).length();
        if (width == 0) {
            throw new IllegalArgumentException("Board rows must not be empty");
        }

        Tile[][] tiles = new Tile[rows.size()][width];
        Position pacmanSpawn = null;
        List<Position> ghostSpawns = new ArrayList<>();
        for (int y = 0; y < rows.size(); y++) {
            String row = rows.get(y);
            if (row.length() != width) {
                throw new IllegalArgumentException("Board row " + (y + 1)
                        + " has width " + row.length() + "; expected " + width);
            }
            for (int x = 0; x < width; x++) {
                char symbol = row.charAt(x);
                Position position = new Position(x, y);
                tiles[y][x] = switch (symbol) {
                    case '#' -> Tile.WALL;
                    case '.' -> Tile.PELLET;
                    case 'o' -> Tile.POWER_PELLET;
                    case ' ' -> Tile.EMPTY;
                    case 'P' -> {
                        if (pacmanSpawn != null) {
                            throw invalidSymbol(symbol, position, "only one Pac-Man spawn is allowed");
                        }
                        pacmanSpawn = position;
                        yield Tile.EMPTY;
                    }
                    case 'G' -> {
                        ghostSpawns.add(position);
                        yield Tile.EMPTY;
                    }
                    default -> throw invalidSymbol(symbol, position, "unknown board symbol");
                };
            }
        }
        if (pacmanSpawn == null) {
            throw new IllegalArgumentException("Board layout must contain a Pac-Man spawn (P)");
        }
        if (ghostSpawns.isEmpty()) {
            throw new IllegalArgumentException("Board layout must contain at least one ghost spawn (G)");
        }
        return new Board(tiles, pacmanSpawn, ghostSpawns);
    }

    private static IllegalArgumentException invalidSymbol(char symbol, Position position, String reason) {
        return new IllegalArgumentException(reason + " '" + symbol + "' at " + position);
    }
}
