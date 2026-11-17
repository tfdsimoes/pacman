package com.tfdsimoes.pacman.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tfdsimoes.pacman.model.Ghost;
import com.tfdsimoes.pacman.model.GhostStrategyMode;
import com.tfdsimoes.pacman.model.GhostType;
import com.tfdsimoes.pacman.model.Direction;
import com.tfdsimoes.pacman.model.Pacman;
import com.tfdsimoes.pacman.model.Position;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class GhostAiTest {
    @Test
    void assignsDifferentTargetsToEachGhostDuringChase() throws Exception {
        var board = board();
        var pacman = new Pacman(new Position(5, 3));
        pacman.requestDirection(Direction.RIGHT);
        pacman.tick(board);
        var blinky = new Ghost(GhostType.BLINKY, new Position(1, 3));
        var pinky = new Ghost(GhostType.PINKY, new Position(1, 3));
        var inky = new Ghost(GhostType.INKY, new Position(1, 3));
        var clyde = new Ghost(GhostType.CLYDE, new Position(1, 3));
        List<Ghost> ghosts = List.of(blinky, pinky, inky, clyde);
        var ai = new GhostAi();

        assertEquals(new Position(6, 3),
                ai.targetFor(board, blinky, pacman, GhostStrategyMode.CHASE, ghosts));
        assertEquals(new Position(10, 3),
                ai.targetFor(board, pinky, pacman, GhostStrategyMode.CHASE, ghosts));
        assertEquals(new Position(15, 3),
                ai.targetFor(board, inky, pacman, GhostStrategyMode.CHASE, ghosts));
        assertEquals(new Position(1, 5),
                ai.targetFor(board, clyde, pacman, GhostStrategyMode.CHASE, ghosts));
    }

    @Test
    void choosesShortestPathTowardChaseTarget() throws Exception {
        var board = board();
        var pacman = new Pacman(new Position(5, 3));
        var blinky = new Ghost(GhostType.BLINKY, new Position(1, 3));

        assertEquals(Direction.RIGHT,
                new GhostAi().chooseDirection(
                        board, blinky, pacman, GhostStrategyMode.CHASE, List.of(blinky)));
    }

    @Test
    void pinkyProjectsItsTargetThroughTheTunnel() throws Exception {
        String layout = String.join("\n",
                "#######",
                "#######",
                ".....P.",
                "#G#####",
                "#######");
        var board = BoardLoader.load(new ByteArrayInputStream(layout.getBytes(StandardCharsets.UTF_8)));
        var pacman = new Pacman(board.pacmanSpawn());
        pacman.requestDirection(Direction.RIGHT);
        pacman.tick(board);
        var pinky = new Ghost(GhostType.PINKY, board.ghostSpawns().getFirst());

        assertEquals(new Position(3, 2), new GhostAi().targetFor(
                board, pinky, pacman, GhostStrategyMode.CHASE, List.of(pinky)));
    }

    private static com.tfdsimoes.pacman.model.Board board() throws Exception {
        String layout = String.join("\n",
                "###########",
                "#.........#",
                "#.........#",
                "#G...P....#",
                "#.........#",
                "#.........#",
                "###########");
        return BoardLoader.load(new ByteArrayInputStream(layout.getBytes(StandardCharsets.UTF_8)));
    }
}
