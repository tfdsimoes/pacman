package com.tfdsimoes.pacman.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tfdsimoes.pacman.model.Position;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class FruitManagerTest {
    @Test
    void spawnsOnScheduleAndExpiresAfterItsLifetime() throws Exception {
        var board = board();
        var fruitManager = new FruitManager();

        for (int tick = 0; tick < 59; tick++) {
            fruitManager.advance(board, 1);
        }
        assertTrue(fruitManager.activeFruit().isEmpty());

        fruitManager.advance(board, 1);
        var fruit = fruitManager.activeFruit().orElseThrow();
        assertEquals(new Position(4, 3), fruit.position());
        for (int tick = 0; tick < 69; tick++) {
            fruitManager.advance(board, 1);
        }
        assertTrue(fruitManager.activeFruit().isPresent());

        fruitManager.advance(board, 1);

        assertFalse(fruitManager.activeFruit().isPresent());
    }

    @Test
    void collectionAwardsLevelScaledValueAndRemovesFruit() throws Exception {
        var board = board();
        var fruitManager = new FruitManager();
        for (int tick = 0; tick < 60; tick++) {
            fruitManager.advance(board, 3);
        }
        Position position = fruitManager.activeFruit().orElseThrow().position();

        assertEquals(0, fruitManager.collectAt(new Position(0, 0)));
        assertEquals(300, fruitManager.collectAt(position));
        assertTrue(fruitManager.activeFruit().isEmpty());
    }

    private static com.tfdsimoes.pacman.model.Board board() throws Exception {
        String layout = String.join("\n",
                "#########",
                "#G#####P#",
                "#########",
                "#.......#",
                "#########");
        return BoardLoader.load(new ByteArrayInputStream(layout.getBytes(StandardCharsets.UTF_8)));
    }
}
