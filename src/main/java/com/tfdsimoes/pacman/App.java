package com.tfdsimoes.pacman;

import com.tfdsimoes.pacman.logic.BoardLoader;
import com.tfdsimoes.pacman.logic.GameSession;
import com.tfdsimoes.pacman.model.Board;
import com.tfdsimoes.pacman.model.Pacman;
import com.tfdsimoes.pacman.ui.GameWindow;
import java.io.IOException;
import javax.swing.SwingUtilities;

public final class App {

    private App() {
    }

    public static void main(String[] args) {
        try {
            Board board = BoardLoader.loadResource("/boards/classic.txt");
            GameSession game = new GameSession(board, new Pacman(board.pacmanSpawn()));
            SwingUtilities.invokeLater(() -> new GameWindow(game).show());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the game board", exception);
        }
    }
}
