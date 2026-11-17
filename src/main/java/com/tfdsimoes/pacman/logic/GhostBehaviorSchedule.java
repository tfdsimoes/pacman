package com.tfdsimoes.pacman.logic;

import com.tfdsimoes.pacman.GameConfig;
import com.tfdsimoes.pacman.model.GhostStrategyMode;

public final class GhostBehaviorSchedule {
    private GhostBehaviorSchedule() {
    }

    public static GhostStrategyMode modeAt(int elapsedTicks) {
        if (elapsedTicks < 0) {
            throw new IllegalArgumentException("Elapsed ticks must not be negative");
        }
        int remaining = elapsedTicks;
        for (int phase = 0; phase < GameConfig.GHOST_BEHAVIOR_PHASE_LENGTHS.size(); phase++) {
            if (remaining < GameConfig.GHOST_BEHAVIOR_PHASE_LENGTHS.get(phase)) {
                return phase % 2 == 0 ? GhostStrategyMode.SCATTER : GhostStrategyMode.CHASE;
            }
            remaining -= GameConfig.GHOST_BEHAVIOR_PHASE_LENGTHS.get(phase);
        }
        return GhostStrategyMode.CHASE;
    }
}
