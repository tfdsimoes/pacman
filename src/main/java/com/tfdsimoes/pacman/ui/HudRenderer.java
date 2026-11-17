package com.tfdsimoes.pacman.ui;

import com.tfdsimoes.pacman.GameConfig;
import com.tfdsimoes.pacman.logic.GameSession;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

final class HudRenderer {
    private static final Color BACKGROUND_COLOR = new Color(8, 12, 38);
    private static final Color SEPARATOR_COLOR = new Color(48, 86, 170);
    private static final Color SCORE_COLOR = new Color(255, 224, 100);
    private static final Color GAME_OVER_COLOR = new Color(255, 70, 70);
    private static final Color STATUS_OVERLAY_COLOR = new Color(0, 0, 0, 190);
    private static final Font HUD_FONT = new Font(Font.MONOSPACED, Font.BOLD, 13);
    private static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    private static final Font STATUS_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 28);

    void render(Graphics2D canvas, GameSession game, boolean aiControlled, int width) {
        canvas.setColor(BACKGROUND_COLOR);
        canvas.fillRect(0, 0, width, GameConfig.HUD_HEIGHT);
        canvas.setColor(SEPARATOR_COLOR);
        canvas.drawLine(0, GameConfig.HUD_HEIGHT - 1, width, GameConfig.HUD_HEIGHT - 1);
        canvas.setFont(HUD_FONT);
        canvas.setColor(SCORE_COLOR);
        canvas.drawString("SCORE " + game.score(), 12, 20);
        String lives = "LIVES " + game.lives();
        int livesWidth = canvas.getFontMetrics().stringWidth(lives);
        canvas.setColor(Color.WHITE);
        canvas.drawString(lives, (width - livesWidth) / 2, 20);
        String level = "LEVEL " + game.level();
        int levelWidth = canvas.getFontMetrics().stringWidth(level);
        canvas.drawString(level, width - levelWidth - 12, 20);
        canvas.setFont(TITLE_FONT);
        canvas.setColor(BoardRenderer.PACMAN_COLOR);
        String title = aiControlled ? "PAC-MAN / AI" : "PAC-MAN";
        int titleWidth = canvas.getFontMetrics().stringWidth(title);
        canvas.drawString(title, (width - titleWidth) / 2, 41);
    }

    void renderStatus(Graphics2D canvas, GameSession game, int width, int height) {
        String message = game.gameOver() ? "GAME OVER" : game.levelComplete() ? "LEVEL COMPLETE" : null;
        if (message == null) {
            return;
        }
        canvas.setColor(STATUS_OVERLAY_COLOR);
        canvas.fillRect(0, GameConfig.HUD_HEIGHT, width, height - GameConfig.HUD_HEIGHT);
        canvas.setColor(game.gameOver() ? GAME_OVER_COLOR : Color.WHITE);
        canvas.setFont(STATUS_FONT);
        int textWidth = canvas.getFontMetrics().stringWidth(message);
        canvas.drawString(message, (width - textWidth) / 2, height / 2 + 14);
    }
}
