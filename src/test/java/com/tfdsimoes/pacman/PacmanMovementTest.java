package com.tfdsimoes.pacman;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tfdsimoes.pacman.logic.BoardLoader;
import com.tfdsimoes.pacman.model.Direction;
import com.tfdsimoes.pacman.model.Pacman;
import com.tfdsimoes.pacman.model.Position;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class PacmanMovementTest {
    @Test
    void movesOneTileWhenDirectionIsRequested() throws Exception {
        var board = board("######\n#P..G#\n######");
        var pacman = new Pacman(board.pacmanSpawn());
        pacman.requestDirection(Direction.RIGHT);

        pacman.tick(board);

        assertEquals(new Position(2, 1), pacman.position());
        assertEquals(Direction.RIGHT, pacman.direction());
    }

    @Test
    void cannotMoveThroughAWall() throws Exception {
        var board = board("#####\n#P#G#\n#####");
        var pacman = new Pacman(board.pacmanSpawn());
        pacman.requestDirection(Direction.RIGHT);

        pacman.tick(board);

        assertEquals(new Position(1, 1), pacman.position());
    }

    @Test
    void keepsMovingUntilABufferedTurnBecomesPossible() throws Exception {
        var board = board("######\n#P..G#\n######");
        var pacman = new Pacman(board.pacmanSpawn());
        pacman.requestDirection(Direction.RIGHT);
        pacman.tick(board);
        pacman.requestDirection(Direction.UP);

        pacman.tick(board);

        assertEquals(new Position(3, 1), pacman.position());
        assertEquals(Direction.RIGHT, pacman.direction());
    }

    @Test
    void stopsWhenStopIsExplicitlyRequested() throws Exception {
        var board = board("######\n#P..G#\n######");
        var pacman = new Pacman(board.pacmanSpawn());
        pacman.requestDirection(Direction.RIGHT);
        pacman.tick(board);
        pacman.requestDirection(Direction.STOPPED);

        pacman.tick(board);

        assertEquals(new Position(2, 1), pacman.position());
        assertEquals(Direction.STOPPED, pacman.direction());
    }

    @Test
    void wrapsAcrossOpenHorizontalTunnelEndpoints() throws Exception {
        var board = board("#####\nP...G\n#####");
        var pacman = new Pacman(board.pacmanSpawn());
        pacman.requestDirection(Direction.LEFT);

        pacman.tick(board);

        assertEquals(new Position(4, 1), pacman.position());
    }

    private static com.tfdsimoes.pacman.model.Board board(String layout) throws Exception {
        return BoardLoader.load(new ByteArrayInputStream(layout.getBytes(StandardCharsets.UTF_8)));
    }
}
