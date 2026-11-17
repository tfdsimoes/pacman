package com.tfdsimoes.pacman;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tfdsimoes.pacman.logic.BoardLoader;
import com.tfdsimoes.pacman.logic.GameSession;
import com.tfdsimoes.pacman.model.Direction;
import com.tfdsimoes.pacman.model.GhostType;
import com.tfdsimoes.pacman.model.Pacman;
import com.tfdsimoes.pacman.model.Position;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class GhostTest {
    @Test
    void createsFourGhostsAtClassicSpawnPositions() throws Exception {
        var board = BoardLoader.loadResource("/boards/classic.txt");
        var game = new GameSession(board, new Pacman(board.pacmanSpawn()));

        assertEquals(4, game.ghosts().size());
        assertEquals(GhostType.BLINKY, game.ghosts().get(0).type());
        assertEquals(new Position(6, 8), game.ghosts().get(0).position());
    }

    @Test
    void ghostCollisionCostsLifeAndResetsPlayerAndGhost() throws Exception {
        var board = BoardLoader.load(new ByteArrayInputStream(
                "######\n#PG..#\n######".getBytes(StandardCharsets.UTF_8)));
        var game = new GameSession(board, new Pacman(board.pacmanSpawn()));
        game.pacman().requestDirection(Direction.RIGHT);

        game.tick();

        assertEquals(2, game.lives());
        assertEquals(board.pacmanSpawn(), game.pacman().position());
        assertEquals(board.ghostSpawns().getFirst(), game.ghosts().getFirst().position());
    }
}
