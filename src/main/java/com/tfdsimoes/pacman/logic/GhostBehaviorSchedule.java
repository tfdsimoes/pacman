package com.tfdsimoes.pacman.logic;

import com.tfdsimoes.pacman.model.GhostStrategyMode;

public final class GhostBehaviorSchedule {
    private static final int[] PHASE_LENGTHS = {54, 154, 54, 154, 38};

    private GhostBehaviorSchedule() {
    }

    public static GhostStrategyMode modeAt(int elapsedTicks) {
        if (elapsedTicks < 0) {
            throw new IllegalArgumentException("Elapsed ticks must not be negative");
        }
        int remaining = elapsedTicks;
        for (int phase = 0; phase < PHASE_LENGTHS.length; phase++) {
            if (remaining < PHASE_LENGTHS[phase]) {
                return phase % 2 == 0 ? GhostStrategyMode.SCATTER : GhostStrategyMode.CHASE;
            }
            remaining -= PHASE_LENGTHS[phase];
        }
        return GhostStrategyMode.CHASE;
    }
}
