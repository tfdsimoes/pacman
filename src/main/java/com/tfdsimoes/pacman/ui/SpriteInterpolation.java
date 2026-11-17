package com.tfdsimoes.pacman.ui;

import com.tfdsimoes.pacman.model.Position;
import java.awt.geom.Point2D;
import java.util.Objects;

final class SpriteInterpolation {
    private SpriteInterpolation() {
    }

    static void interpolate(Position previous, Position current, double progress, Point2D.Double result) {
        Objects.requireNonNull(previous);
        Objects.requireNonNull(current);
        Objects.requireNonNull(result);
        if (Math.abs(current.x() - previous.x()) + Math.abs(current.y() - previous.y()) != 1) {
            result.setLocation(current.x(), current.y());
            return;
        }
        double amount = Math.max(0, Math.min(1, progress));
        result.setLocation(
                previous.x() + (current.x() - previous.x()) * amount,
                previous.y() + (current.y() - previous.y()) * amount);
    }
}
