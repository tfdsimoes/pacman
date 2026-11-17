package com.tfdsimoes.pacman.ui;

import com.tfdsimoes.pacman.model.Position;
import java.awt.geom.Point2D;
import java.util.Objects;

final class SpriteInterpolation {
    private SpriteInterpolation() {
    }

    static Point2D.Double interpolate(Position previous, Position current, double progress) {
        Objects.requireNonNull(previous);
        Objects.requireNonNull(current);
        if (Math.abs(current.x() - previous.x()) + Math.abs(current.y() - previous.y()) != 1) {
            return new Point2D.Double(current.x(), current.y());
        }
        double amount = Math.max(0, Math.min(1, progress));
        return new Point2D.Double(
                previous.x() + (current.x() - previous.x()) * amount,
                previous.y() + (current.y() - previous.y()) * amount);
    }
}
