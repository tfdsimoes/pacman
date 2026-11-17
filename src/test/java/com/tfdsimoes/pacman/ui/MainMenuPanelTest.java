package com.tfdsimoes.pacman.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

class MainMenuPanelTest {
    @Test
    void menuOffersHumanAndAiModes() throws Exception {
        AtomicReference<Boolean> selectedMode = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            var menu = new MainMenuPanel(selectedMode::set);
            menu.playButton().doClick(0);
            assertFalse(selectedMode.get());
            menu.aiButton().doClick(0);
            assertTrue(selectedMode.get());
        });
    }
}
