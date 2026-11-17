package com.tfdsimoes.pacman.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tfdsimoes.pacman.model.Pacman;
import org.junit.jupiter.api.Test;

class PacmanAiTest {
    @Test
    void clearsTheClassicBoardWithoutLosingAllLives() throws Exception {
        var board = BoardLoader.loadResource("/boards/classic.txt");
        var game = new GameSession(board, new Pacman(board.pacmanSpawn()), new PacmanAi());
        int ticks = 0;
        while (!game.levelComplete() && !game.gameOver() && ticks < 15_000) {
            game.tick();
            ticks++;
        }

        assertTrue(game.levelComplete(), "AI should clear every collectible on the classic board");
        assertFalse(game.gameOver());
        assertEquals(3, game.lives(), "AI mode should not lose lives when defeating normal ghosts");
        assertTrue(ticks < 15_000, "AI should finish within the test's tick limit");
    }
}
