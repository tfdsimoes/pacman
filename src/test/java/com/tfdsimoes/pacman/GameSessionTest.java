package com.tfdsimoes.pacman;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tfdsimoes.pacman.logic.BoardLoader;
import com.tfdsimoes.pacman.logic.GameSession;
import com.tfdsimoes.pacman.logic.PacmanAi;
import com.tfdsimoes.pacman.model.Direction;
import com.tfdsimoes.pacman.model.GhostMode;
import com.tfdsimoes.pacman.model.Pacman;
import com.tfdsimoes.pacman.model.Position;
import com.tfdsimoes.pacman.model.Tile;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class GameSessionTest {
    @Test
    void eatingPelletAddsScoreAndRemovesItFromBoard() throws Exception {
        var game = game("#####\n#P.G#\n#####");
        game.pacman().requestDirection(Direction.RIGHT);

        game.tick();

        assertEquals(10, game.score());
        assertEquals(Tile.EMPTY, game.board().tileAt(new Position(2, 1)));
        assertTrue(game.levelComplete());
    }

    @Test
    void eatingPowerPelletAwardsFiftyPoints() throws Exception {
        var game = game("#####\n#PoG#\n#####");
        game.pacman().requestDirection(Direction.RIGHT);

        game.tick();

        assertEquals(50, game.score());
        assertTrue(game.levelComplete());
    }

    @Test
    void doesNotScoreWhenBlockedAndLevelStaysActive() throws Exception {
        var game = game("#####\n#P#G.\n#####");
        game.pacman().requestDirection(Direction.RIGHT);

        game.tick();

        assertEquals(0, game.score());
        assertFalse(game.levelComplete());
    }

    @Test
    void stopsAdvancingWhenAllCollectiblesHaveBeenEaten() throws Exception {
        var game = game("#####\n#P.G#\n#####");
        game.pacman().requestDirection(Direction.RIGHT);
        game.tick();
        Position clearedPosition = game.pacman().position();
        game.pacman().requestDirection(Direction.LEFT);

        game.tick();

        assertEquals(clearedPosition, game.pacman().position());
        assertEquals(10, game.score());
    }

    @Test
    void powerPelletFrightensGhostsAndEatingOneAwardsBonus() throws Exception {
        var game = game("#######\n#PoG###\n#######\n#.....#\n#######");
        game.pacman().requestDirection(Direction.RIGHT);

        game.tick();

        assertEquals(250, game.score());
        assertEquals(3, game.lives());
        assertEquals(GhostMode.EATEN, game.ghosts().getFirst().mode());
        assertEquals(game.board().ghostSpawns().getFirst(), game.ghosts().getFirst().position());
        game.pacman().requestDirection(Direction.LEFT);
        game.tick();
        game.pacman().requestDirection(Direction.STOPPED);

        for (int tick = 0; tick < 9; tick++) {
            game.tick();
        }

        assertEquals(GhostMode.NORMAL, game.ghosts().getFirst().mode());
    }

    @Test
    void frightenedModeExpiresAfterItsTickDuration() throws Exception {
        var game = game("######\n#PoG.#\n######");
        game.pacman().requestDirection(Direction.RIGHT);
        game.tick();
        game.pacman().requestDirection(Direction.STOPPED);

        for (int tick = 0; tick < 70; tick++) {
            game.tick();
        }

        assertEquals(GhostMode.NORMAL, game.ghosts().getFirst().mode());
    }

    @Test
    void advancesToResetBoardAndIncreasesSpeedAfterLevelClear() throws Exception {
        var game = game("#####\n#P.G#\n#####");
        game.pacman().requestDirection(Direction.RIGHT);

        game.tick();

        assertTrue(game.levelComplete());
        assertEquals(1, game.level());
        assertEquals(130, game.tickIntervalMillis());
        for (int tick = 0; tick < 9; tick++) {
            game.tick();
        }
        assertEquals(1, game.level());

        game.tick();

        assertEquals(2, game.level());
        assertFalse(game.levelComplete());
        assertEquals(122, game.tickIntervalMillis());
        assertEquals(game.board().pacmanSpawn(), game.pacman().position());
        assertEquals(1, game.board().collectibleCount());
        assertEquals(10, game.score());
    }

    @Test
    void gameOverAfterFinalLifeAndStopsFurtherUpdates() throws Exception {
        var game = game("######\n#PG..#\n######");

        for (int life = 0; life < 3; life++) {
            game.pacman().requestDirection(Direction.RIGHT);
            game.tick();
        }

        assertEquals(0, game.lives());
        assertTrue(game.gameOver());
        var position = game.pacman().position();
        game.tick();
        assertEquals(position, game.pacman().position());
        assertEquals(0, game.lives());
    }

    @Test
    void aiModeDefeatsNormalGhostsWithoutLosingLives() throws Exception {
        var board = BoardLoader.load(new ByteArrayInputStream(
                "#####\n#PG.#\n#####".getBytes(StandardCharsets.UTF_8)));
        var game = new GameSession(board, new Pacman(board.pacmanSpawn()), new PacmanAi());

        game.tick();

        assertEquals(3, game.lives());
        assertEquals(200, game.score());
        assertEquals(GhostMode.EATEN, game.ghosts().getFirst().mode());

        game.tick();

        assertTrue(game.levelComplete());
        assertEquals(210, game.score());
    }

    @Test
    void collectingTimedFruitAddsBonusToSessionScore() throws Exception {
        var game = game(String.join("\n",
                "#########",
                "#G#####P#",
                "#########",
                "#.......#",
                "#########"));
        for (int tick = 0; tick < 60; tick++) {
            game.tick();
        }
        Position fruitPosition = game.activeFruit().orElseThrow().position();
        game.pacman().reset(fruitPosition);

        game.tick();

        assertEquals(110, game.score());
        assertTrue(game.activeFruit().isEmpty());
    }

    private static GameSession game(String layout) throws Exception {
        var board = BoardLoader.load(new ByteArrayInputStream(layout.getBytes(StandardCharsets.UTF_8)));
        return new GameSession(board, new Pacman(board.pacmanSpawn()));
    }
}
