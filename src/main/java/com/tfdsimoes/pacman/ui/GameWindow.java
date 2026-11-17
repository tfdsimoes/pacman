package com.tfdsimoes.pacman.ui;

import com.tfdsimoes.pacman.logic.GameSession;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import javax.swing.JFrame;

public final class GameWindow {
    private final JFrame frame;
    private final GamePanel gamePanel;

    public GameWindow(GameSession game) {
        gamePanel = new GamePanel(Objects.requireNonNull(game));
        frame = new JFrame("Pac-Man");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                gamePanel.stop();
            }
        });
    }

    public void show() {
        frame.setVisible(true);
        gamePanel.start();
        gamePanel.requestFocusInWindow();
    }
}
