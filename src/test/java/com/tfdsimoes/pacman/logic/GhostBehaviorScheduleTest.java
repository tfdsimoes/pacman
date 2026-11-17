package com.tfdsimoes.pacman.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tfdsimoes.pacman.model.GhostStrategyMode;
import org.junit.jupiter.api.Test;

class GhostBehaviorScheduleTest {
    @Test
    void alternatesScatterAndChaseThenStaysInChase() {
        assertEquals(GhostStrategyMode.SCATTER, GhostBehaviorSchedule.modeAt(0));
        assertEquals(GhostStrategyMode.SCATTER, GhostBehaviorSchedule.modeAt(53));
        assertEquals(GhostStrategyMode.CHASE, GhostBehaviorSchedule.modeAt(54));
        assertEquals(GhostStrategyMode.SCATTER, GhostBehaviorSchedule.modeAt(208));
        assertEquals(GhostStrategyMode.CHASE, GhostBehaviorSchedule.modeAt(454));
    }

    @Test
    void rejectsNegativeElapsedTicks() {
        assertThrows(IllegalArgumentException.class, () -> GhostBehaviorSchedule.modeAt(-1));
    }
}
