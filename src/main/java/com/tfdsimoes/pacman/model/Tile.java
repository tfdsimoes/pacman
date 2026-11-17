package com.tfdsimoes.pacman.model;

public enum Tile {
    WALL('#'),
    PELLET('.'),
    POWER_PELLET('o'),
    EMPTY(' ');

    private final char symbol;

    Tile(char symbol) {
        this.symbol = symbol;
    }

    public char symbol() {
        return symbol;
    }
}
