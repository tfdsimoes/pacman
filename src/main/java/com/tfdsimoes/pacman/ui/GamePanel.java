package com.tfdsimoes.pacman.ui;

import com.tfdsimoes.pacman.model.Board;
import com.tfdsimoes.pacman.model.Direction;
import com.tfdsimoes.pacman.model.Ghost;
import com.tfdsimoes.pacman.model.GhostMode;
import com.tfdsimoes.pacman.model.GhostType;
import com.tfdsimoes.pacman.model.Position;
import com.tfdsimoes.pacman.model.Tile;
import com.tfdsimoes.pacman.logic.GameSession;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public final class GamePanel extends JPanel {
    private static final int CELL_SIZE = 28;
    private static final int HUD_HEIGHT = 36;
    private static final int TICK_MILLIS = 130;
    private static final Color WALL_COLOR = new Color(32, 64, 255);
    private static final Color PELLET_COLOR = new Color(255, 190, 170);
    private static final Color PACMAN_COLOR = new Color(255, 220, 0);

    private final GameSession game;
    private final Timer timer;

    public GamePanel(GameSession game) {
        this.game = Objects.requireNonNull(game);
        Board board = game.board();
        setBackground(Color.BLACK);
        setFocusable(true);
        setPreferredSize(new Dimension(board.width() * CELL_SIZE, HUD_HEIGHT + board.height() * CELL_SIZE));
        configureKeyBindings();
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
        game.tick();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D canvas = (Graphics2D) graphics.create();
        try {
            drawHud(canvas);
            drawBoard(canvas);
            drawGhosts(canvas);
            drawPacman(canvas);
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
        canvas.setColor(Color.BLACK);
        canvas.fillRect(0, 0, getWidth(), HUD_HEIGHT);
        canvas.setColor(Color.WHITE);
        canvas.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        canvas.drawString("SCORE " + game.score() + "       LIVES " + game.lives() + "       LEVEL 1", 10, 23);
    }

    private void drawBoard(Graphics2D canvas) {
        Board board = game.board();
        for (int y = 0; y < board.height(); y++) {
            for (int x = 0; x < board.width(); x++) {
                int screenX = x * CELL_SIZE;
                int screenY = HUD_HEIGHT + y * CELL_SIZE;
                Tile tile = board.tileAt(new Position(x, y));
                if (tile == Tile.WALL) {
                    canvas.setColor(WALL_COLOR);
                    canvas.fillRoundRect(screenX + 1, screenY + 1, CELL_SIZE - 2, CELL_SIZE - 2, 8, 8);
                } else if (tile == Tile.PELLET || tile == Tile.POWER_PELLET) {
                    int pelletSize = tile == Tile.POWER_PELLET ? 10 : 4;
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
            canvas.setColor(ghostColor(ghost));
            canvas.fillRoundRect(position.x() * CELL_SIZE + 3, HUD_HEIGHT + position.y() * CELL_SIZE + 3,
                    CELL_SIZE - 6, CELL_SIZE - 6, 12, 12);
        }
    }

    private void drawPacman(Graphics2D canvas) {
        Position position = game.pacman().position();
        canvas.setColor(PACMAN_COLOR);
        canvas.fillOval(position.x() * CELL_SIZE + 2, HUD_HEIGHT + position.y() * CELL_SIZE + 2,
                CELL_SIZE - 4, CELL_SIZE - 4);
    }

    private Color ghostColor(Ghost ghost) {
        if (ghost.mode() == GhostMode.FRIGHTENED) {
            return new Color(45, 70, 210);
        }
        if (ghost.mode() == GhostMode.EATEN) {
            return Color.WHITE;
        }
        return switch (ghost.type()) {
            case BLINKY -> new Color(255, 40, 40);
            case PINKY -> new Color(255, 150, 200);
            case INKY -> new Color(70, 220, 230);
            case CLYDE -> new Color(255, 170, 70);
        };
    }
}
