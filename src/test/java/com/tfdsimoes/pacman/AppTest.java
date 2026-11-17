package com.tfdsimoes.pacman;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    void mainRunsWithoutErrors() {
        assertDoesNotThrow(() -> App.main(new String[0]));
    }
}
