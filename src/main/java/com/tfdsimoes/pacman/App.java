package com.tfdsimoes.pacman;

import com.tfdsimoes.pacman.logic.BoardLoader;
import com.tfdsimoes.pacman.model.Board;
import com.tfdsimoes.pacman.ui.MainMenuWindow;
import java.io.IOException;
import javax.swing.SwingUtilities;

public final class App {

    private App() {
    }

    public static void main(String[] args) {
        try {
            Board board = BoardLoader.loadResource("/boards/classic.txt");
            SwingUtilities.invokeLater(() -> new MainMenuWindow(board).show());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the game board", exception);
        }
    }
}
