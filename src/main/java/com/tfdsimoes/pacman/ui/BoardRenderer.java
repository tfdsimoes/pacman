package com.tfdsimoes.pacman.ui;

import com.tfdsimoes.pacman.logic.GameSession;
import com.tfdsimoes.pacman.model.Board;
import com.tfdsimoes.pacman.model.Direction;
import com.tfdsimoes.pacman.model.Ghost;
import com.tfdsimoes.pacman.model.GhostMode;
import com.tfdsimoes.pacman.model.GhostType;
import com.tfdsimoes.pacman.model.Position;
import com.tfdsimoes.pacman.model.Tile;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

public final class BoardRenderer {
    static final int CELL_SIZE = 26;
    static final int HUD_HEIGHT = HudRenderer.HEIGHT;
    static final Color PACMAN_COLOR = new Color(255, 220, 0);

    private static final Color FLOOR_COLOR = new Color(5, 8, 28);
    private static final Color WALL_COLOR = new Color(26, 73, 185);
    private static final Color WALL_HIGHLIGHT = new Color(75, 156, 255);
    private static final Color WALL_OUTLINE_COLOR = new Color(103, 176, 255, 210);
    private static final Color PELLET_COLOR = new Color(255, 224, 190);
    private static final Color PELLET_GLOW_COLOR = new Color(255, 208, 150, 24);
    private static final Color POWER_PELLET_GLOW_COLOR = new Color(255, 208, 150, 55);
    private static final Color GHOST_SHADOW_COLOR = new Color(0, 0, 0, 90);
    private static final Color GHOST_OUTLINE_COLOR = new Color(255, 255, 255, 95);
    private static final Color NORMAL_GHOST_EYE_COLOR = new Color(25, 82, 205);
    private static final Color EATEN_GHOST_EYE_COLOR = new Color(30, 190, 255);
    private static final Color FRIGHTENED_MOUTH_COLOR = new Color(255, 235, 245);
    private static final Color FRUIT_COLOR = new Color(255, 85, 65);
    private static final Color FRUIT_HIGHLIGHT_COLOR = new Color(255, 185, 140);
    private static final Color FRUIT_LEAF_COLOR = new Color(90, 225, 125);
    private static final Color FRUIT_STEM_COLOR = new Color(120, 225, 135);
    private static final Color PACMAN_SHADOW_COLOR = new Color(0, 0, 0, 95);
    private static final Color PACMAN_GLOW_COLOR = new Color(255, 218, 40, 55);
    private static final Color PACMAN_EYE_COLOR = new Color(38, 28, 12);
    private static final Color FRIGHTENED_GHOST_COLOR = new Color(37, 87, 220);
    private static final Color FRIGHTENED_GHOST_FLASH_COLOR = new Color(42, 62, 165);
    private static final Color BLINKY_COLOR = new Color(255, 63, 74);
    private static final Color PINKY_COLOR = new Color(255, 135, 195);
    private static final Color INKY_COLOR = new Color(68, 220, 235);
    private static final Color CLYDE_COLOR = new Color(255, 166, 68);
    private static final BasicStroke WALL_STROKE = new BasicStroke(1.2f);
    private static final BasicStroke GHOST_STROKE = new BasicStroke(1f);
    private static final BasicStroke FRIGHTENED_MOUTH_STROKE = new BasicStroke(1.5f);
    private static final BasicStroke FRUIT_STROKE = new BasicStroke(1.5f);
    private static final Path2D.Double GHOST_BODY = createGhostShape();
    private static final Path2D.Double FRIGHTENED_MOUTH = createFrightenedMouth();

    private final Point2D.Double interpolationPoint = new Point2D.Double();
    private Board cachedBoard;
    private BufferedImage staticMazeLayer;

    public String render(Board board) {
        Objects.requireNonNull(board);
        StringBuilder output = new StringBuilder();
        List<Position> ghostSpawns = board.ghostSpawns();
        for (int y = 0; y < board.height(); y++) {
            for (int x = 0; x < board.width(); x++) {
                Position position = new Position(x, y);
                if (position.equals(board.pacmanSpawn())) {
                    output.append('P');
                } else if (ghostSpawns.contains(position)) {
                    output.append('G');
                } else {
                    output.append(board.tileAt(position).symbol());
                }
            }
            if (y + 1 < board.height()) {
                output.append(System.lineSeparator());
            }
        }
        return output.toString();
    }

    void render(Graphics2D canvas, GameSession game, int animationFrame, double progress) {
        Objects.requireNonNull(canvas);
        Objects.requireNonNull(game);
        Board board = game.board();
        if (cachedBoard != board || staticMazeLayer == null) {
            cacheStaticMazeLayer(board);
        }
        canvas.drawImage(staticMazeLayer, 0, HUD_HEIGHT, null);
        drawCollectibles(canvas, board, animationFrame);
        drawFruit(canvas, game);
        drawGhosts(canvas, game.ghosts(), animationFrame, progress);
        drawPacman(canvas, game, animationFrame, progress);
    }

    void refreshStaticMazeLayer() {
        if (cachedBoard != null) {
            cacheStaticMazeLayer(cachedBoard);
        }
    }

    private void cacheStaticMazeLayer(Board board) {
        var image = new BufferedImage(board.width() * CELL_SIZE, board.height() * CELL_SIZE,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D canvas = image.createGraphics();
        try {
            canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            canvas.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            canvas.setColor(FLOOR_COLOR);
            canvas.fillRect(0, 0, image.getWidth(), image.getHeight());
            for (int y = 0; y < board.height(); y++) {
                for (int x = 0; x < board.width(); x++) {
                    if (board.tileAt(x, y) != Tile.WALL) {
                        continue;
                    }
                    int screenX = x * CELL_SIZE;
                    int screenY = y * CELL_SIZE;
                    canvas.setPaint(new GradientPaint(screenX, screenY, WALL_HIGHLIGHT,
                            screenX + CELL_SIZE, screenY + CELL_SIZE, WALL_COLOR));
                    canvas.fillRoundRect(screenX + 2, screenY + 2, CELL_SIZE - 4, CELL_SIZE - 4, 9, 9);
                    canvas.setColor(WALL_OUTLINE_COLOR);
                    canvas.setStroke(WALL_STROKE);
                    canvas.drawRoundRect(screenX + 2, screenY + 2, CELL_SIZE - 4, CELL_SIZE - 4, 9, 9);
                }
            }
        } finally {
            canvas.dispose();
        }
        cachedBoard = board;
        staticMazeLayer = image;
    }

    private void drawCollectibles(Graphics2D canvas, Board board, int animationFrame) {
        for (int y = 0; y < board.height(); y++) {
            for (int x = 0; x < board.width(); x++) {
                Tile tile = board.tileAt(x, y);
                if (tile != Tile.PELLET && tile != Tile.POWER_PELLET) {
                    continue;
                }
                int screenX = x * CELL_SIZE;
                int screenY = HUD_HEIGHT + y * CELL_SIZE;
                int pelletSize = tile == Tile.POWER_PELLET ? 8 + animationFrame % 3 : 4;
                int glowSize = pelletSize + 8;
                canvas.setColor(tile == Tile.POWER_PELLET ? POWER_PELLET_GLOW_COLOR : PELLET_GLOW_COLOR);
                canvas.fillOval(screenX + (CELL_SIZE - glowSize) / 2,
                        screenY + (CELL_SIZE - glowSize) / 2, glowSize, glowSize);
                canvas.setColor(PELLET_COLOR);
                canvas.fillOval(screenX + (CELL_SIZE - pelletSize) / 2,
                        screenY + (CELL_SIZE - pelletSize) / 2, pelletSize, pelletSize);
            }
        }
    }

    private void drawFruit(Graphics2D canvas, GameSession game) {
        game.activeFruit().ifPresent(fruit -> {
            Position position = fruit.position();
            int x = position.x() * CELL_SIZE;
            int y = HUD_HEIGHT + position.y() * CELL_SIZE;
            canvas.setColor(FRUIT_COLOR);
            canvas.fillOval(x + 6, y + 8, CELL_SIZE - 12, CELL_SIZE - 13);
            canvas.setColor(FRUIT_HIGHLIGHT_COLOR);
            canvas.fillOval(x + 8, y + 10, 5, 4);
            canvas.setColor(FRUIT_LEAF_COLOR);
            canvas.fillArc(x + CELL_SIZE / 2 - 1, y + 2, 10, 9, 25, 145);
            canvas.setColor(FRUIT_STEM_COLOR);
            canvas.setStroke(FRUIT_STROKE);
            canvas.drawLine(x + CELL_SIZE / 2, y + 9, x + CELL_SIZE / 2 + 3, y + 4);
        });
    }

    private void drawGhosts(Graphics2D canvas, List<Ghost> ghosts, int animationFrame, double progress) {
        for (Ghost ghost : ghosts) {
            interpolate(ghost.previousPosition(), ghost.position(), progress);
            int x = (int) Math.round(interpolationPoint.x * CELL_SIZE) + 3;
            int y = HUD_HEIGHT + (int) Math.round(interpolationPoint.y * CELL_SIZE) + 3;
            int size = CELL_SIZE - 6;
            if (ghost.mode() == GhostMode.EATEN) {
                drawGhostEyes(canvas, x, y, size, true);
                continue;
            }

            canvas.setColor(GHOST_SHADOW_COLOR);
            canvas.fillOval(x + 2, y + size - 2, size - 2, 5);
            canvas.translate(x, y);
            canvas.setColor(ghostColor(ghost, animationFrame));
            canvas.fill(GHOST_BODY);
            canvas.setColor(GHOST_OUTLINE_COLOR);
            canvas.setStroke(GHOST_STROKE);
            canvas.draw(GHOST_BODY);
            canvas.translate(-x, -y);
            if (ghost.mode() == GhostMode.FRIGHTENED) {
                drawFrightenedFace(canvas, x, y, size);
            } else {
                drawGhostEyes(canvas, x, y, size, false);
            }
        }
    }

    private void drawPacman(Graphics2D canvas, GameSession game, int animationFrame, double progress) {
        interpolate(game.pacman().previousPosition(), game.pacman().position(), progress);
        int x = (int) Math.round(interpolationPoint.x * CELL_SIZE) + 2;
        int y = HUD_HEIGHT + (int) Math.round(interpolationPoint.y * CELL_SIZE) + 2;
        int size = CELL_SIZE - 4;
        canvas.setColor(PACMAN_SHADOW_COLOR);
        canvas.fillOval(x + 2, y + size - 1, size - 3, 4);
        canvas.setColor(PACMAN_GLOW_COLOR);
        canvas.fillOval(x - 2, y - 2, size + 4, size + 4);
        canvas.setColor(PACMAN_COLOR);
        int mouthAngle = game.pacman().direction() == Direction.STOPPED ? 12 : animationFrame % 2 == 0 ? 52 : 20;
        int startAngle = switch (game.pacman().direction()) {
            case UP -> 90;
            case DOWN -> 270;
            case LEFT -> 180;
            case RIGHT, STOPPED -> 0;
        };
        canvas.fillArc(x, y, size, size, startAngle + mouthAngle / 2, 360 - mouthAngle);
        canvas.setColor(PACMAN_EYE_COLOR);
        canvas.fillOval(x + size * 2 / 3, y + size / 3, 2, 2);
    }

    private void drawGhostEyes(Graphics2D canvas, int x, int y, int size, boolean eaten) {
        int eyeY = y + size / 3;
        int eyeWidth = size / 3;
        int eyeHeight = size / 3;
        int firstEyeX = x + size / 6;
        int secondEyeX = x + size / 2;
        canvas.setColor(Color.WHITE);
        canvas.fillOval(firstEyeX, eyeY, eyeWidth, eyeHeight);
        canvas.fillOval(secondEyeX, eyeY, eyeWidth, eyeHeight);
        canvas.setColor(eaten ? EATEN_GHOST_EYE_COLOR : NORMAL_GHOST_EYE_COLOR);
        canvas.fillOval(firstEyeX + eyeWidth / 3, eyeY + eyeHeight / 3, eyeWidth / 2, eyeHeight / 2);
        canvas.fillOval(secondEyeX + eyeWidth / 3, eyeY + eyeHeight / 3, eyeWidth / 2, eyeHeight / 2);
    }

    private void drawFrightenedFace(Graphics2D canvas, int x, int y, int size) {
        canvas.setColor(Color.WHITE);
        canvas.fillOval(x + size / 4, y + size / 3, 3, 4);
        canvas.fillOval(x + size * 2 / 3, y + size / 3, 3, 4);
        canvas.setColor(FRIGHTENED_MOUTH_COLOR);
        canvas.setStroke(FRIGHTENED_MOUTH_STROKE);
        canvas.translate(x, y);
        canvas.draw(FRIGHTENED_MOUTH);
        canvas.translate(-x, -y);
    }

    private void interpolate(Position previous, Position current, double progress) {
        SpriteInterpolation.interpolate(previous, current, progress, interpolationPoint);
    }

    private Color ghostColor(Ghost ghost, int animationFrame) {
        if (ghost.mode() == GhostMode.FRIGHTENED) {
            return animationFrame % 8 < 4 ? FRIGHTENED_GHOST_COLOR : FRIGHTENED_GHOST_FLASH_COLOR;
        }
        return switch (ghost.type()) {
            case BLINKY -> BLINKY_COLOR;
            case PINKY -> PINKY_COLOR;
            case INKY -> INKY_COLOR;
            case CLYDE -> CLYDE_COLOR;
        };
    }

    private static Path2D.Double createGhostShape() {
        int size = CELL_SIZE - 6;
        double bottom = size;
        Path2D.Double body = new Path2D.Double();
        body.moveTo(0, bottom);
        body.lineTo(0, size * 0.43);
        body.curveTo(0, size * 0.16, size * 0.19, 2, size * 0.5, 2);
        body.curveTo(size * 0.81, 2, size, size * 0.16, size, size * 0.43);
        body.lineTo(size, bottom);
        double segment = size / 3.0;
        for (int scallop = 0; scallop < 3; scallop++) {
            double endX = size - (scallop + 1) * segment;
            double controlX = size - (scallop + 0.5) * segment;
            body.quadTo(controlX, bottom - 5, endX, bottom);
        }
        body.closePath();
        return body;
    }

    private static Path2D.Double createFrightenedMouth() {
        int size = CELL_SIZE - 6;
        Path2D.Double mouth = new Path2D.Double();
        mouth.moveTo(size / 4.0, size * 0.72);
        for (int point = 1; point <= 4; point++) {
            double px = size / 4.0 + point * size / 8.0;
            double py = size * (point % 2 == 0 ? 0.68 : 0.78);
            mouth.lineTo(px, py);
        }
        return mouth;
    }
}
