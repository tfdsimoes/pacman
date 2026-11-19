package com.tfdsimoes.pacman;

import java.util.List;

public final class GameConfig {
    public static final int PELLET_SCORE = 10;
    public static final int POWER_PELLET_SCORE = 50;
    public static final int GHOST_EAT_SCORE = 200;
    public static final int FRUIT_BASE_VALUE = 100;
    public static final int STARTING_LIVES = 3;

    public static final int FRIGHTENED_DURATION_TICKS = 70;
    public static final int GHOST_EATEN_RETURN_TICKS = 10;
    public static final int LEVEL_CLEAR_TICKS = 10;
    public static final long FIRST_FRUIT_SPAWN_TICK = 60;
    public static final long FRUIT_SPAWN_INTERVAL_TICKS = 120;
    public static final int FRUIT_LIFETIME_TICKS = 70;
    public static final List<Integer> GHOST_BEHAVIOR_PHASE_LENGTHS = List.of(54, 154, 54, 154, 38);

    public static final int BASE_LOGIC_TICK_MILLIS = 130;
    public static final int LEVEL_SPEEDUP_MILLIS = 8;
    public static final int MIN_LOGIC_TICK_MILLIS = 60;
    public static final int RENDER_FRAME_MILLIS = 16;
    public static final int PACMAN_GHOST_ALERT_DISTANCE = 14;
    public static final int PACMAN_DEAD_END_DANGER_PENALTY = 2;

    public static final int CELL_SIZE = 26;
    public static final int HUD_HEIGHT = 48;
    public static final int MAIN_MENU_WIDTH = 560;
    public static final int MAIN_MENU_HEIGHT = 440;
    public static final int MAIN_MENU_BUTTON_WIDTH = 300;
    public static final int MAIN_MENU_BUTTON_HEIGHT = 50;

    private GameConfig() {
    }
}
