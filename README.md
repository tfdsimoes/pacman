# Pac-Man

A Java 25 implementation of Pac-Man built with Maven and Swing.

## Requirements

- JDK 25 (LTS)
- Apache Maven 3.9+

No Maven Wrapper is included.

## Build and run

```shell
mvn clean verify
mvn package
java -jar target/pacman-1.0.0.jar
```

The application opens a Swing menu where you can choose to play yourself or
watch the autonomous Pac-Man AI. Both modes use the 29-by-21 maze with
antialiased arcade-style graphics. The board layout is in
`src/main/resources/boards/classic.txt`.

In human mode, use the arrow keys or WASD to move. Movement uses grid ticks with
buffered turns, wall collision, and horizontal wrap-around at the open tunnel endpoints.
The game logic keeps its level-dependent grid tick interval while rendering at
approximately 60 frames per second, interpolating sprite movement between ticks.
Pellets are worth 10 points and power pellets 50 points; the score updates as
items are collected, and the level completes when no pellets remain.
Four ghosts start in the ghost house; in human mode, touching a normal ghost
costs a life and resets the player and ghosts to their spawn positions. A
Pac-Man/ghost tile swap during one logic tick also counts as a collision.
Power pellets frighten active ghosts for 70 game ticks; eating a frightened
ghost awards 200 bonus points and sends it back to the ghost house before it
re-enters play.
Ghosts move through the maze using deterministic shortest-path pursuit and
per-ghost target behaviors. They alternate scatter and chase phases before
remaining in chase mode.
The game starts with three lives. Clearing the maze advances to a reset board
after a brief level-clear display, with movement speeding up by level; losing
all lives ends the game.
Bonus fruit appears on a timer, remains available for a limited duration, and
awards 100 points per level when Pac-Man reaches its location.
The AI mode autonomously routes to the nearest remaining collectible and
prioritizes power pellets when normal ghosts are nearby. It follows the same
collision rules as human mode: normal ghosts cost a life, while frightened
ghosts can be eaten for bonus points.

## Board layout

The UTF-8 board resource is a rectangular grid. `#` is a wall, `.` a pellet,
`o` a power pellet, `P` Pac-Man's spawn, and `G` a ghost spawn. Spawn markers
are treated as empty floor tiles by the board model.

The maze has four ghost spawn tiles, corner power pellets, and a
horizontal tunnel through its center row. Wall blocks, pellets, Pac-Man, and
ghosts are drawn with Java2D shapes and gradients; the graphics use no external
image assets. The static wall layer is cached between paints, while pellets
remain dynamic so their animations and collection state update immediately.
Rendering reuses drawing styles and sprite paths to limit per-frame allocations.
Board rendering, HUD drawing, and keyboard input are separated from the timer-driven
game panel to keep UI responsibilities focused.
Gameplay scores, timing, movement speeds, and board/menu dimensions are centralized
in `com.tfdsimoes.pacman.GameConfig`.
