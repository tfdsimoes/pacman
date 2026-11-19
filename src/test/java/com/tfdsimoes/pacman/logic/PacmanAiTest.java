package com.tfdsimoes.pacman.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.tfdsimoes.pacman.model.Direction;
import com.tfdsimoes.pacman.model.GhostMode;
import com.tfdsimoes.pacman.model.Pacman;
import com.tfdsimoes.pacman.model.Position;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class PacmanAiTest {
    @Test
    void movesTowardNearestPelletOnAnOpenPath() throws Exception {
        var board = BoardLoader.load(new ByteArrayInputStream(
                "#######\n#P..G.#\n#######".getBytes(StandardCharsets.UTF_8)));
        var game = new GameSession(board, new Pacman(board.pacmanSpawn()), new PacmanAi());

        game.tick();

        assertEquals(new Position(2, 1), game.pacman().position());
        assertEquals(Direction.RIGHT, game.pacman().direction());
        assertEquals(10, game.score());
    }

    @Test
    void movesAwayFromAnAdjacentNormalGhost() throws Exception {
        var game = game("#######\n#.....#\n#..P..#\n#..G..#\n#######");

        assertNotEquals(Direction.DOWN, new PacmanAi().chooseDirection(game));
    }

    @Test
    void avoidsEnteringADeadEndWhenThreatened() throws Exception {
        var game = game("#########\n#########\n###.#####\n#..P....#\n###G#####\n#########");

        assertEquals(Direction.LEFT, new PacmanAi().chooseDirection(game));
    }

    @Test
    void pursuesNearbyFrightenedGhosts() throws Exception {
        var game = game("#######\n#.....#\n#G.P..#\n#.....#\n#######");
        game.ghosts().getFirst().frightenFor(10);

        assertEquals(Direction.LEFT, new PacmanAi().chooseDirection(game));
    }

    private static GameSession game(String layout) throws Exception {
        var board = BoardLoader.load(new ByteArrayInputStream(layout.getBytes(StandardCharsets.UTF_8)));
        return new GameSession(board, new Pacman(board.pacmanSpawn()), new PacmanAi());
    }
}
