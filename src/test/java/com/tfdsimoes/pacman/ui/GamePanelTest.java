package com.tfdsimoes.pacman.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tfdsimoes.pacman.logic.BoardLoader;
import com.tfdsimoes.pacman.model.Direction;
import com.tfdsimoes.pacman.model.Pacman;
import com.tfdsimoes.pacman.model.Position;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.junit.jupiter.api.Test;

class GamePanelTest {
    @Test
    void arrowKeyRequestsMovementAndTickMovesPlayer() throws Exception {
        var board = BoardLoader.loadResource("/boards/classic.txt");
        var pacman = new Pacman(board.pacmanSpawn());
        var panel = new GamePanel(board, pacman);
        KeyStroke up = KeyStroke.getKeyStroke("UP");
        Object actionName = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(up);
        Action action = panel.getActionMap().get(actionName);

        action.actionPerformed(new ActionEvent(panel, ActionEvent.ACTION_PERFORMED, "up"));
        panel.tick();

        assertEquals(Direction.UP, pacman.direction());
        assertEquals(new Position(8, 6), pacman.position());
        assertEquals(19 * 28, panel.getPreferredSize().width);
    }
}
