package com.tfdsimoes.pacman.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tfdsimoes.pacman.logic.BoardLoader;
import com.tfdsimoes.pacman.logic.GameSession;
import com.tfdsimoes.pacman.model.Direction;
import com.tfdsimoes.pacman.model.Pacman;
import com.tfdsimoes.pacman.model.Position;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.junit.jupiter.api.Test;

class GamePanelTest {
    @Test
    void arrowKeyRequestsMovementAndTickMovesPlayer() throws Exception {
        var board = BoardLoader.loadResource("/boards/classic.txt");
        var pacman = new Pacman(board.pacmanSpawn());
        var game = new GameSession(board, pacman);
        var panel = new GamePanel(game);
        KeyStroke up = KeyStroke.getKeyStroke("UP");
        Object actionName = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(up);
        Action action = panel.getActionMap().get(actionName);

        action.actionPerformed(new ActionEvent(panel, ActionEvent.ACTION_PERFORMED, "up"));
        panel.tick();

        assertEquals(Direction.UP, pacman.direction());
        assertEquals(new Position(14, 16), pacman.position());
        assertEquals(10, game.score());
        assertEquals(29 * 26, panel.getPreferredSize().width);
    }

    @Test
    void paintsTheExpandedBoard() throws Exception {
        var board = BoardLoader.loadResource("/boards/classic.txt");
        var panel = new GamePanel(new GameSession(board, new Pacman(board.pacmanSpawn())));
        var preferredSize = panel.getPreferredSize();
        var image = new BufferedImage(preferredSize.width, preferredSize.height, BufferedImage.TYPE_INT_ARGB);
        panel.setSize(preferredSize);

        var graphics = image.createGraphics();
        try {
            panel.paint(graphics);
        } finally {
            graphics.dispose();
        }

        assertEquals(29 * 26, preferredSize.width);
        assertEquals(48 + 21 * 26, preferredSize.height);
    }
}
