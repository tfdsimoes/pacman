package com.tfdsimoes.pacman.ui;

import com.tfdsimoes.pacman.model.Board;
import com.tfdsimoes.pacman.model.Direction;
import com.tfdsimoes.pacman.model.Ghost;
import com.tfdsimoes.pacman.model.GhostMode;
import com.tfdsimoes.pacman.model.GhostType;
import com.tfdsimoes.pacman.model.Position;
import com.tfdsimoes.pacman.model.Tile;
import com.tfdsimoes.pacman.logic.GameSession;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public final class GamePanel extends JPanel {
    private static final int CELL_SIZE = 26;
    private static final int HUD_HEIGHT = 48;
    private static final int TICK_MILLIS = 130;
    private static final Color FLOOR_COLOR = new Color(5, 8, 28);
    private static final Color WALL_COLOR = new Color(26, 73, 185);
    private static final Color WALL_HIGHLIGHT = new Color(75, 156, 255);
    private static final Color PELLET_COLOR = new Color(255, 224, 190);
    private static final Color PACMAN_COLOR = new Color(255, 220, 0);

    private final GameSession game;
    private final Timer timer;
    private final boolean aiControlled;
    private int animationFrame;

    public GamePanel(GameSession game) {
        this(game, false);
    }

    public GamePanel(GameSession game, boolean aiControlled) {
        this.game = Objects.requireNonNull(game);
        this.aiControlled = aiControlled;
        Board board = game.board();
        setBackground(FLOOR_COLOR);
        setFocusable(true);
        setPreferredSize(new Dimension(board.width() * CELL_SIZE, HUD_HEIGHT + board.height() * CELL_SIZE));
        if (!aiControlled) {
            configureKeyBindings();
        }
        timer = new Timer(TICK_MILLIS, event -> tick());
        timer.setCoalesce(true);
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    public void tick() {
        animationFrame++;
        game.tick();
        timer.setDelay(game.tickIntervalMillis());
        if (game.gameOver()) {
            timer.stop();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D canvas = (Graphics2D) graphics.create();
        try {
            canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            canvas.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            drawHud(canvas);
            drawBoard(canvas);
            drawFruit(canvas);
            drawGhosts(canvas);
            drawPacman(canvas);
            drawStatus(canvas);
        } finally {
            canvas.dispose();
        }
    }

    private void configureKeyBindings() {
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        bindDirection(inputMap, actionMap, Direction.UP, "UP", "W");
        bindDirection(inputMap, actionMap, Direction.DOWN, "DOWN", "S");
        bindDirection(inputMap, actionMap, Direction.LEFT, "LEFT", "A");
        bindDirection(inputMap, actionMap, Direction.RIGHT, "RIGHT", "D");
    }

    private void bindDirection(InputMap inputMap, ActionMap actionMap, Direction direction, String... keys) {
        String actionName = "move-" + direction.name().toLowerCase();
        for (String key : keys) {
            inputMap.put(KeyStroke.getKeyStroke(key), actionName);
        }
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                game.pacman().requestDirection(direction);
            }
        });
    }

    private void drawHud(Graphics2D canvas) {
        canvas.setColor(new Color(8, 12, 38));
        canvas.fillRect(0, 0, getWidth(), HUD_HEIGHT);
        canvas.setColor(new Color(48, 86, 170));
        canvas.drawLine(0, HUD_HEIGHT - 1, getWidth(), HUD_HEIGHT - 1);
        canvas.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
        canvas.setColor(new Color(255, 224, 100));
        canvas.drawString("SCORE " + game.score(), 12, 20);
        String lives = "LIVES " + game.lives();
        int livesWidth = canvas.getFontMetrics().stringWidth(lives);
        canvas.setColor(Color.WHITE);
        canvas.drawString(lives, (getWidth() - livesWidth) / 2, 20);
        String level = "LEVEL " + game.level();
        int levelWidth = canvas.getFontMetrics().stringWidth(level);
        canvas.drawString(level, getWidth() - levelWidth - 12, 20);
        canvas.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        canvas.setColor(PACMAN_COLOR);
        String title = aiControlled ? "PAC-MAN / AI" : "PAC-MAN";
        int titleWidth = canvas.getFontMetrics().stringWidth(title);
        canvas.drawString(title, (getWidth() - titleWidth) / 2, 41);
    }

    private void drawBoard(Graphics2D canvas) {
        Board board = game.board();
        canvas.setColor(FLOOR_COLOR);
        canvas.fillRect(0, HUD_HEIGHT, board.width() * CELL_SIZE, board.height() * CELL_SIZE);
        for (int y = 0; y < board.height(); y++) {
            for (int x = 0; x < board.width(); x++) {
                int screenX = x * CELL_SIZE;
                int screenY = HUD_HEIGHT + y * CELL_SIZE;
                Tile tile = board.tileAt(new Position(x, y));
                if (tile == Tile.WALL) {
                    canvas.setPaint(new GradientPaint(screenX, screenY, WALL_HIGHLIGHT,
                            screenX + CELL_SIZE, screenY + CELL_SIZE, WALL_COLOR));
                    canvas.fillRoundRect(screenX + 2, screenY + 2, CELL_SIZE - 4, CELL_SIZE - 4, 9, 9);
                    canvas.setColor(new Color(103, 176, 255, 210));
                    canvas.setStroke(new BasicStroke(1.2f));
                    canvas.drawRoundRect(screenX + 2, screenY + 2, CELL_SIZE - 4, CELL_SIZE - 4, 9, 9);
                } else if (tile == Tile.PELLET || tile == Tile.POWER_PELLET) {
                    int pelletSize = tile == Tile.POWER_PELLET ? 8 + animationFrame % 3 : 4;
                    int glowSize = pelletSize + 8;
                    canvas.setColor(new Color(255, 208, 150, tile == Tile.POWER_PELLET ? 55 : 24));
                    canvas.fillOval(screenX + (CELL_SIZE - glowSize) / 2,
                            screenY + (CELL_SIZE - glowSize) / 2, glowSize, glowSize);
                    canvas.setColor(PELLET_COLOR);
                    canvas.fillOval(screenX + (CELL_SIZE - pelletSize) / 2,
                            screenY + (CELL_SIZE - pelletSize) / 2, pelletSize, pelletSize);
                }
            }
        }
    }

    private void drawGhosts(Graphics2D canvas) {
        for (Ghost ghost : game.ghosts()) {
            Position position = ghost.position();
            int x = position.x() * CELL_SIZE + 3;
            int y = HUD_HEIGHT + position.y() * CELL_SIZE + 3;
            int size = CELL_SIZE - 6;
            if (ghost.mode() == GhostMode.EATEN) {
                drawGhostEyes(canvas, x, y, size, true);
                continue;
            }

            canvas.setColor(new Color(0, 0, 0, 90));
            canvas.fillOval(x + 2, y + size - 2, size - 2, 5);
            Path2D body = ghostShape(x, y, size);
            canvas.setColor(ghostColor(ghost));
            canvas.fill(body);
            canvas.setColor(new Color(255, 255, 255, 95));
            canvas.setStroke(new BasicStroke(1f));
            canvas.draw(body);
            if (ghost.mode() == GhostMode.FRIGHTENED) {
                drawFrightenedFace(canvas, x, y, size);
            } else {
                drawGhostEyes(canvas, x, y, size, false);
            }
        }
    }

    private Path2D ghostShape(int x, int y, int size) {
        double bottom = y + size;
        Path2D body = new Path2D.Double();
        body.moveTo(x, bottom);
        body.lineTo(x, y + size * 0.43);
        body.curveTo(x, y + size * 0.16, x + size * 0.19, y + 2, x + size * 0.5, y + 2);
        body.curveTo(x + size * 0.81, y + 2, x + size, y + size * 0.16, x + size, y + size * 0.43);
        body.lineTo(x + size, bottom);
        double segment = size / 3.0;
        for (int scallop = 0; scallop < 3; scallop++) {
            double endX = x + size - (scallop + 1) * segment;
            double controlX = x + size - (scallop + 0.5) * segment;
            body.quadTo(controlX, bottom - 5, endX, bottom);
        }
        body.closePath();
        return body;
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
        canvas.setColor(eaten ? new Color(30, 190, 255) : new Color(25, 82, 205));
        canvas.fillOval(firstEyeX + eyeWidth / 3, eyeY + eyeHeight / 3, eyeWidth / 2, eyeHeight / 2);
        canvas.fillOval(secondEyeX + eyeWidth / 3, eyeY + eyeHeight / 3, eyeWidth / 2, eyeHeight / 2);
    }

    private void drawFrightenedFace(Graphics2D canvas, int x, int y, int size) {
        canvas.setColor(Color.WHITE);
        canvas.fillOval(x + size / 4, y + size / 3, 3, 4);
        canvas.fillOval(x + size * 2 / 3, y + size / 3, 3, 4);
        canvas.setColor(new Color(255, 235, 245));
        canvas.setStroke(new BasicStroke(1.5f));
        Path2D mouth = new Path2D.Double();
        mouth.moveTo(x + size / 4.0, y + size * 0.72);
        for (int point = 1; point <= 4; point++) {
            double px = x + size / 4.0 + point * size / 8.0;
            double py = y + size * (point % 2 == 0 ? 0.68 : 0.78);
            mouth.lineTo(px, py);
        }
        canvas.draw(mouth);
    }

    private void drawFruit(Graphics2D canvas) {
        game.activeFruit().ifPresent(fruit -> {
            Position position = fruit.position();
            int x = position.x() * CELL_SIZE;
            int y = HUD_HEIGHT + position.y() * CELL_SIZE;
            canvas.setColor(new Color(255, 85, 65));
            canvas.fillOval(x + 6, y + 8, CELL_SIZE - 12, CELL_SIZE - 13);
            canvas.setColor(new Color(255, 185, 140));
            canvas.fillOval(x + 8, y + 10, 5, 4);
            canvas.setColor(new Color(90, 225, 125));
            canvas.fillArc(x + CELL_SIZE / 2 - 1, y + 2, 10, 9, 25, 145);
            canvas.setColor(new Color(120, 225, 135));
            canvas.setStroke(new BasicStroke(1.5f));
            canvas.drawLine(x + CELL_SIZE / 2, y + 9, x + CELL_SIZE / 2 + 3, y + 4);
        });
    }

    private void drawPacman(Graphics2D canvas) {
        Position position = game.pacman().position();
        int x = position.x() * CELL_SIZE + 2;
        int y = HUD_HEIGHT + position.y() * CELL_SIZE + 2;
        int size = CELL_SIZE - 4;
        canvas.setColor(new Color(0, 0, 0, 95));
        canvas.fillOval(x + 2, y + size - 1, size - 3, 4);
        canvas.setColor(new Color(255, 218, 40, 55));
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
        canvas.setColor(new Color(38, 28, 12));
        canvas.fillOval(x + size * 2 / 3, y + size / 3, 2, 2);
    }

    private void drawStatus(Graphics2D canvas) {
        String message = game.gameOver() ? "GAME OVER" : game.levelComplete() ? "LEVEL COMPLETE" : null;
        if (message == null) {
            return;
        }
        canvas.setColor(new Color(0, 0, 0, 190));
        canvas.fillRect(0, HUD_HEIGHT, getWidth(), getHeight() - HUD_HEIGHT);
        canvas.setColor(game.gameOver() ? new Color(255, 70, 70) : Color.WHITE);
        canvas.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        int textWidth = canvas.getFontMetrics().stringWidth(message);
        canvas.drawString(message, (getWidth() - textWidth) / 2, getHeight() / 2 + 14);
    }

    private Color ghostColor(Ghost ghost) {
        if (ghost.mode() == GhostMode.FRIGHTENED) {
            return animationFrame % 8 < 4 ? new Color(37, 87, 220) : new Color(42, 62, 165);
        }
        return switch (ghost.type()) {
            case BLINKY -> new Color(255, 63, 74);
            case PINKY -> new Color(255, 135, 195);
            case INKY -> new Color(68, 220, 235);
            case CLYDE -> new Color(255, 166, 68);
        };
    }
}
