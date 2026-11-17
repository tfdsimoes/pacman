package com.tfdsimoes.pacman.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tfdsimoes.pacman.model.Position;
import java.awt.geom.Point2D;
import org.junit.jupiter.api.Test;

class SpriteInterpolationTest {
    @Test
    void interpolatesBetweenAdjacentTiles() {
        var result = new Point2D.Double();
        SpriteInterpolation.interpolate(new Position(2, 3), new Position(3, 3), 0.5, result);

        assertEquals(2.5, result.x);
        assertEquals(3.0, result.y);
    }

    @Test
    void clampsProgressToTheMovementInterval() {
        var result = new Point2D.Double();
        SpriteInterpolation.interpolate(new Position(2, 3), new Position(3, 3), 2, result);

        assertEquals(3.0, result.x);
        assertEquals(3.0, result.y);
    }

    @Test
    void skipsInterpolationAcrossTunnelWrap() {
        var result = new Point2D.Double();
        SpriteInterpolation.interpolate(new Position(0, 10), new Position(28, 10), 0.5, result);

        assertEquals(28.0, result.x);
        assertEquals(10.0, result.y);
    }
}
