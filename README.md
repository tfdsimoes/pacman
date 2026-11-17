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

The application opens a Swing window with the maze and Pac-Man. The board layout
is in `src/main/resources/boards/classic.txt`.

Use the arrow keys or WASD to move. Movement uses grid ticks with buffered turns,
wall collision, and horizontal wrap-around at the open tunnel endpoints.

## Board layout

The UTF-8 board resource is a rectangular grid. `#` is a wall, `.` a pellet,
`o` a power pellet, `P` Pac-Man's spawn, and `G` a ghost spawn. Spawn markers
are treated as empty floor tiles by the board model.
