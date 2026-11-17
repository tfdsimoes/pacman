package com.tfdsimoes.pacman.ui;

import com.tfdsimoes.pacman.logic.GameSession;
import com.tfdsimoes.pacman.logic.PacmanAi;
import com.tfdsimoes.pacman.model.Board;
import com.tfdsimoes.pacman.model.Pacman;
import java.util.Objects;
import javax.swing.JFrame;

public final class MainMenuWindow {
    private final Board board;
    private final JFrame frame;

    public MainMenuWindow(Board board) {
        this.board = Objects.requireNonNull(board);
        frame = new JFrame("Pac-Man");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setContentPane(new MainMenuPanel(this::startGame));
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    public void show() {
        frame.setVisible(true);
    }

    private void startGame(boolean aiControlled) {
        GameSession game = new GameSession(board, new Pacman(board.pacmanSpawn()),
                aiControlled ? new PacmanAi() : null);
        new GameWindow(game, aiControlled).show();
        frame.dispose();
    }
}
