package com.tfdsimoes.pacman.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.tfdsimoes.pacman.logic.BoardLoader;
import com.tfdsimoes.pacman.logic.GameSession;
import com.tfdsimoes.pacman.logic.PacmanAi;
import com.tfdsimoes.pacman.model.Direction;
import com.tfdsimoes.pacman.model.Pacman;
import com.tfdsimoes.pacman.model.Position;
import java.awt.Dimension;
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
        render(panel);

        assertEquals(29 * 26, preferredSize.width);
        assertEquals(48 + 21 * 26, preferredSize.height);
    }

    @Test
    void cachesWallsWhilePaintingCollectiblesFromCurrentBoardState() throws Exception {
        var board = BoardLoader.loadResource("/boards/classic.txt");
        var panel = new GamePanel(new GameSession(board, new Pacman(board.pacmanSpawn())));
        var before = render(panel);
        int wallPixel = before.getRGB(13, 48 + 13);
        int pelletPixel = before.getRGB(2 * 26 + 13, 48 + 1 * 26 + 13);

        board.consumeCollectible(new Position(2, 1));
        var after = render(panel);

        assertEquals(wallPixel, after.getRGB(13, 48 + 13));
        assertNotEquals(pelletPixel, after.getRGB(2 * 26 + 13, 48 + 1 * 26 + 13));
    }

    @Test
    void aiModeDoesNotRegisterHumanMovementKeys() throws Exception {
        var board = BoardLoader.loadResource("/boards/classic.txt");
        var game = new GameSession(board, new Pacman(board.pacmanSpawn()), new PacmanAi());
        var panel = new GamePanel(game, true);

        assertNull(panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .get(KeyStroke.getKeyStroke("UP")));
        panel.tick();

        assertNotEquals(board.pacmanSpawn(), game.pacman().position());
    }

    private BufferedImage render(GamePanel panel) {
        Dimension size = panel.getPreferredSize();
        var image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        panel.setSize(size);
        var graphics = image.createGraphics();
        try {
            panel.paint(graphics);
        } finally {
            graphics.dispose();
        }
        return image;
    }
}
