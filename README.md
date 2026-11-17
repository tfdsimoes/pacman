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

The application currently loads and prints the maze in the terminal. The game is
being implemented in stages; the board layout is in
`src/main/resources/boards/classic.txt`.

Pac-Man movement is modeled in grid ticks with buffered turns, wall collision,
and horizontal wrap-around at open tunnel endpoints.

## Board layout

The UTF-8 board resource is a rectangular grid. `#` is a wall, `.` a pellet,
`o` a power pellet, `P` Pac-Man's spawn, and `G` a ghost spawn. Spawn markers
are treated as empty floor tiles by the board model.
