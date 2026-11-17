package com.tfdsimoes.pacman;

import com.tfdsimoes.pacman.logic.BoardLoader;
import com.tfdsimoes.pacman.ui.BoardRenderer;
import java.io.IOException;

public final class App {

    private App() {
    }

    public static void main(String[] args) {
        try {
            System.out.println(new BoardRenderer().render(BoardLoader.loadResource("/boards/classic.txt")));
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the game board", exception);
        }
    }
}
