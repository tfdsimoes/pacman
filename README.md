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
java -jar target/pacman-1.0-SNAPSHOT.jar
```

The application opens a Swing window with an expanded 29-by-21 maze and a
refreshed, antialiased arcade-style presentation. The board layout is in
`src/main/resources/boards/classic.txt`.

Use the arrow keys or WASD to move. Movement uses grid ticks with buffered turns,
wall collision, and horizontal wrap-around at the open tunnel endpoints.
Pellets are worth 10 points and power pellets 50 points; the score updates as
items are collected, and the level completes when no pellets remain.
Four stationary ghosts occupy the ghost house; touching one costs a life and
resets the player and ghosts to their spawn positions.
Power pellets frighten active ghosts for 70 game ticks; eating a frightened
ghost awards 200 bonus points and sends it back to the ghost house before it
re-enters play.
Ghosts now move through the maze with deterministic shortest-path pursuit and
per-ghost target behaviors. They alternate scatter and chase phases before
remaining in chase mode.
The game starts with three lives. Clearing the maze advances to a reset board
after a brief level-clear display, with movement speeding up by level; losing
all lives ends the game.
Bonus fruit appears on a timer, remains available for a limited duration, and
awards 100 points per level when Pac-Man reaches its location.

## Board layout

The UTF-8 board resource is a rectangular grid. `#` is a wall, `.` a pellet,
`o` a power pellet, `P` Pac-Man's spawn, and `G` a ghost spawn. Spawn markers
are treated as empty floor tiles by the board model.

The expanded maze has four ghost spawn tiles, corner power pellets, and a
horizontal tunnel through its center row. Wall blocks, pellets, Pac-Man, and
ghosts are drawn with Java2D shapes and gradients; the graphics use no external
image assets.
