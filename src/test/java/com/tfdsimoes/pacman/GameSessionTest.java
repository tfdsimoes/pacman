package com.tfdsimoes.pacman;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tfdsimoes.pacman.logic.BoardLoader;
import com.tfdsimoes.pacman.logic.GameSession;
import com.tfdsimoes.pacman.model.Direction;
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

    private static GameSession game(String layout) throws Exception {
        var board = BoardLoader.load(new ByteArrayInputStream(layout.getBytes(StandardCharsets.UTF_8)));
        return new GameSession(board, new Pacman(board.pacmanSpawn()));
    }
}
