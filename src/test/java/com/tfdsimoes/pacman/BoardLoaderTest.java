package com.tfdsimoes.pacman;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tfdsimoes.pacman.logic.BoardLoader;
import com.tfdsimoes.pacman.model.Direction;
import com.tfdsimoes.pacman.model.Position;
import com.tfdsimoes.pacman.model.Tile;
import com.tfdsimoes.pacman.ui.BoardRenderer;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BoardLoaderTest {
    @Test
    void loadsDimensionsTilesAndSpawnPoints() throws Exception {
        var board = BoardLoader.loadResource("/boards/classic.txt");

        assertEquals(19, board.width());
        assertEquals(15, board.height());
        assertEquals(new Position(8, 7), board.pacmanSpawn());
        assertEquals(java.util.List.of(
                new Position(6, 8), new Position(7, 8), new Position(8, 8), new Position(9, 8)),
                board.ghostSpawns());
        assertEquals(Tile.WALL, board.tileAt(new Position(0, 0)));
        assertEquals(Tile.POWER_PELLET, board.tileAt(new Position(1, 11)));
        assertEquals(Tile.EMPTY, board.tileAt(board.pacmanSpawn()));
    }

    @Test
    void rejectsRowsWithDifferentWidths() {
        assertThrows(IllegalArgumentException.class, () -> load("#P G#\n#.#"));
    }

    @Test
    void rejectsLayoutsMissingAPlayerSpawn() {
        assertThrows(IllegalArgumentException.class, () -> load("#####\n#..G#\n#####"));
    }

    @Test
    void rejectsUnknownSymbols() {
        assertThrows(IllegalArgumentException.class, () -> load("#####\n#PXG#\n#####"));
    }

    @Test
    void classicSpawnHasAnOpenNeighbor() throws Exception {
        var board = BoardLoader.loadResource("/boards/classic.txt");
        Position spawn = board.pacmanSpawn();

        assertTrue(java.util.stream.Stream.of(
                        new Position(spawn.x() - 1, spawn.y()),
                        new Position(spawn.x() + 1, spawn.y()),
                        new Position(spawn.x(), spawn.y() - 1),
                        new Position(spawn.x(), spawn.y() + 1))
                .filter(board::contains)
                .anyMatch(position -> board.tileAt(position) != Tile.WALL));
    }

    @Test
    void everyClassicCollectibleIsReachableFromPacmanSpawn() throws Exception {
        var board = BoardLoader.loadResource("/boards/classic.txt");
        Set<Position> reachable = new HashSet<>();
        var pending = new ArrayDeque<Position>();
        reachable.add(board.pacmanSpawn());
        pending.add(board.pacmanSpawn());
        while (!pending.isEmpty()) {
            Position current = pending.remove();
            for (Direction direction : Direction.values()) {
                Position next = board.neighbor(current, direction);
                if (next != null && reachable.add(next)) {
                    pending.add(next);
                }
            }
        }

        long reachableCollectibles = reachable.stream()
                .filter(position -> board.tileAt(position) == Tile.PELLET
                        || board.tileAt(position) == Tile.POWER_PELLET)
                .count();
        assertEquals(board.collectibleCount(), reachableCollectibles);
    }

    @Test
    void rendersTheLoadedBoardAndSpawns() throws Exception {
        var board = BoardLoader.load(new ByteArrayInputStream(
                "#####\n#P.G#\n#####".getBytes(StandardCharsets.UTF_8)));

        String expected = String.join(System.lineSeparator(), "#####", "#P.G#", "#####");
        assertEquals(expected, new BoardRenderer().render(board));
    }

    private static void load(String layout) {
        try {
            BoardLoader.load(new ByteArrayInputStream(layout.getBytes(StandardCharsets.UTF_8)));
        } catch (java.io.IOException exception) {
            throw new AssertionError(exception);
        }
    }
}
