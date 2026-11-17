package com.tfdsimoes.pacman.ui;

import com.tfdsimoes.pacman.model.Board;
import com.tfdsimoes.pacman.model.Position;
import com.tfdsimoes.pacman.model.Tile;
import java.util.List;
import java.util.Objects;

public final class BoardRenderer {
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
                    Tile tile = board.tileAt(position);
                    output.append(tile.symbol());
                }
            }
            if (y + 1 < board.height()) {
                output.append(System.lineSeparator());
            }
        }
        return output.toString();
    }
}
