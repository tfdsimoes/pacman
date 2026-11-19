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
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
    void choosesDirectionClosestToChaseTarget() throws Exception {
        var board = board();
        var pacman = new Pacman(new Position(5, 3));
        var blinky = new Ghost(GhostType.BLINKY, new Position(1, 3));

        assertEquals(Direction.RIGHT,
                new GhostAi().chooseDirection(
                        board, blinky, pacman, GhostStrategyMode.CHASE, List.of(blinky)));
    }

    @Test
    void breaksDistanceTiesInUpLeftDownRightOrder() throws Exception {
        var board = board();
        var pacman = new Pacman(new Position(6, 2));
        var blinky = new Ghost(GhostType.BLINKY, new Position(5, 3));

        assertEquals(Direction.UP,
                new GhostAi().chooseDirection(
                        board, blinky, pacman, GhostStrategyMode.CHASE, List.of(blinky)));
    }

    @Test
    void neverReversesWhileAnotherDirectionIsAvailable() throws Exception {
        var board = board();
        var pacman = new Pacman(new Position(2, 3));
        var blinky = new Ghost(GhostType.BLINKY, new Position(4, 3));
        blinky.move(Direction.RIGHT, new Position(5, 3));

        assertEquals(Direction.UP,
                new GhostAi().chooseDirection(
                        board, blinky, pacman, GhostStrategyMode.CHASE, List.of(blinky)));
    }

    @Test
    void frightenedGhostChoosesRandomNonReversingDirection() throws Exception {
        var board = board();
        var pacman = new Pacman(new Position(2, 3));
        var blinky = new Ghost(GhostType.BLINKY, new Position(4, 3));
        blinky.move(Direction.RIGHT, new Position(5, 3));
        blinky.frightenFor(10);
        var ai = new GhostAi(new Random(42));
        Set<Direction> chosen = EnumSet.noneOf(Direction.class);

        for (int tick = 0; tick < 100; tick++) {
            chosen.add(ai.chooseDirection(
                    board, blinky, pacman, GhostStrategyMode.CHASE, List.of(blinky)));
        }

        assertEquals(EnumSet.of(Direction.UP, Direction.DOWN, Direction.RIGHT), chosen);
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

    @Test
    void pinkyTargetIncludesUpDirectionOverflowOffset() throws Exception {
        var board = board();
        var pacman = new Pacman(new Position(5, 3));
        pacman.requestDirection(Direction.UP);
        pacman.tick(board);
        var pinky = new Ghost(GhostType.PINKY, new Position(1, 3));

        assertEquals(new Position(1, -2), new GhostAi().targetFor(
                board, pinky, pacman, GhostStrategyMode.CHASE, List.of(pinky)));
    }

    @Test
    void inkyDoublesVectorFromBlinkyToTwoTilesAhead() throws Exception {
        var board = board();
        var pacman = new Pacman(new Position(5, 3));
        pacman.requestDirection(Direction.UP);
        pacman.tick(board);
        var blinky = new Ghost(GhostType.BLINKY, new Position(3, 1));
        var inky = new Ghost(GhostType.INKY, new Position(1, 3));

        assertEquals(new Position(7, -1), new GhostAi().targetFor(
                board, inky, pacman, GhostStrategyMode.CHASE, List.of(blinky, inky)));
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
