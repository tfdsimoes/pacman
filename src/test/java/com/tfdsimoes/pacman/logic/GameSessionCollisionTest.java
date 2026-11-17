package com.tfdsimoes.pacman.logic;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tfdsimoes.pacman.model.Direction;
import com.tfdsimoes.pacman.model.Ghost;
import com.tfdsimoes.pacman.model.GhostType;
import com.tfdsimoes.pacman.model.Pacman;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class GameSessionCollisionTest {
    @Test
    void detectsPacmanAndGhostSwappingTiles() throws Exception {
        var board = BoardLoader.load(new ByteArrayInputStream(
                "#####\n#PG.#\n#####".getBytes(StandardCharsets.UTF_8)));
        var pacman = new Pacman(board.pacmanSpawn());
        var ghost = new Ghost(GhostType.BLINKY, board.ghostSpawns().getFirst());

        pacman.requestDirection(Direction.RIGHT);
        pacman.tick(board);
        ghost.beginTick();
        ghost.move(Direction.LEFT, board.neighbor(ghost.position(), Direction.LEFT));

        assertTrue(GameSession.isCollision(pacman, ghost));
    }
}
