package com.tfdsimoes.pacman.ui;

import com.tfdsimoes.pacman.GameConfig;
import com.tfdsimoes.pacman.logic.GameSession;
import com.tfdsimoes.pacman.model.Board;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Objects;
import javax.swing.JPanel;
import javax.swing.Timer;

public final class GamePanel extends JPanel {
    private static final Color FLOOR_COLOR = new Color(5, 8, 28);

    private final GameSession game;
    private final Timer timer;
    private final Timer renderTimer;
    private final boolean aiControlled;
    private final BoardRenderer boardRenderer = new BoardRenderer();
    private final HudRenderer hudRenderer = new HudRenderer();
    private int animationFrame;
    private long lastLogicTickNanos;

    public GamePanel(GameSession game) {
        this(game, false);
    }

    public GamePanel(GameSession game, boolean aiControlled) {
        this.game = Objects.requireNonNull(game);
        this.aiControlled = aiControlled;
        Board board = game.board();
        setBackground(FLOOR_COLOR);
        setFocusable(true);
        setPreferredSize(new Dimension(
                board.width() * GameConfig.CELL_SIZE,
                GameConfig.HUD_HEIGHT + board.height() * GameConfig.CELL_SIZE));
        if (!aiControlled) {
            GameInputHandler.install(this, game);
        }
        lastLogicTickNanos = System.nanoTime();
        timer = new Timer(GameConfig.BASE_LOGIC_TICK_MILLIS, event -> tick());
        timer.setCoalesce(true);
        renderTimer = new Timer(GameConfig.RENDER_FRAME_MILLIS, event -> renderFrame());
        renderTimer.setCoalesce(true);
    }

    public void start() {
        timer.start();
        renderTimer.start();
    }

    public void stop() {
        timer.stop();
        renderTimer.stop();
    }

    public void tick() {
        int previousLevel = game.level();
        game.tick();
        lastLogicTickNanos = System.nanoTime();
        if (game.level() != previousLevel) {
            boardRenderer.refreshStaticMazeLayer();
        }
        timer.setDelay(game.tickIntervalMillis());
        if (game.gameOver()) {
            stop();
        }
    }

    private void renderFrame() {
        animationFrame++;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D canvas = (Graphics2D) graphics.create();
        try {
            canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            canvas.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            hudRenderer.render(canvas, game, aiControlled, getWidth());
            boardRenderer.render(canvas, game, animationFrame, interpolationProgress());
            hudRenderer.renderStatus(canvas, game, getWidth(), getHeight());
        } finally {
            canvas.dispose();
        }
    }

    private double interpolationProgress() {
        return (System.nanoTime() - lastLogicTickNanos) / (timer.getDelay() * 1_000_000.0);
    }
}
