package com.tfdsimoes.pacman.ui;

import com.tfdsimoes.pacman.logic.GameSession;
import com.tfdsimoes.pacman.model.Direction;
import java.awt.event.ActionEvent;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

final class GameInputHandler {
    private GameInputHandler() {
    }

    static void install(JComponent component, GameSession game) {
        Objects.requireNonNull(component);
        Objects.requireNonNull(game);
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = component.getActionMap();
        bindDirection(inputMap, actionMap, game, Direction.UP, "UP", "W");
        bindDirection(inputMap, actionMap, game, Direction.DOWN, "DOWN", "S");
        bindDirection(inputMap, actionMap, game, Direction.LEFT, "LEFT", "A");
        bindDirection(inputMap, actionMap, game, Direction.RIGHT, "RIGHT", "D");
    }

    private static void bindDirection(
            InputMap inputMap, ActionMap actionMap, GameSession game, Direction direction, String... keys) {
        String actionName = "move-" + direction.name().toLowerCase();
        for (String key : keys) {
            inputMap.put(KeyStroke.getKeyStroke(key), actionName);
        }
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                game.pacman().requestDirection(direction);
            }
        });
    }
}
