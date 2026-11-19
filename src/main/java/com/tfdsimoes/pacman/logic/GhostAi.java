package com.tfdsimoes.pacman.logic;

import com.tfdsimoes.pacman.model.Board;
import com.tfdsimoes.pacman.model.Direction;
import com.tfdsimoes.pacman.model.Ghost;
import com.tfdsimoes.pacman.model.GhostMode;
import com.tfdsimoes.pacman.model.GhostStrategyMode;
import com.tfdsimoes.pacman.model.GhostType;
import com.tfdsimoes.pacman.model.Pacman;
import com.tfdsimoes.pacman.model.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class GhostAi {
    private static final List<Direction> SEARCH_ORDER = List.of(
            Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT);

    private final Random random;

    public GhostAi() {
        this(new Random());
    }

    GhostAi(Random random) {
        this.random = Objects.requireNonNull(random);
    }

    public Position targetFor(Board board, Ghost ghost, Pacman pacman,
            GhostStrategyMode strategyMode, List<Ghost> ghosts) {
        Objects.requireNonNull(board);
        Objects.requireNonNull(ghost);
        Objects.requireNonNull(pacman);
        Objects.requireNonNull(strategyMode);
        Objects.requireNonNull(ghosts);

        if (ghost.mode() == GhostMode.EATEN) {
            return ghost.spawn();
        }
        if (strategyMode == GhostStrategyMode.SCATTER) {
            return scatterTarget(board, ghost.type());
        }
        Position player = pacman.position();
        Position target = switch (ghost.type()) {
            case BLINKY -> player;
            case PINKY -> pinkyTarget(board, pacman);
            case INKY -> inkyTarget(board, pacman, blinkyPosition(ghosts, ghost));
            case CLYDE -> squaredDistance(ghost.position(), player) > 64
                    ? player : scatterTarget(board, GhostType.CLYDE);
        };
        return normalizeTunnelTarget(board, target);
    }

    public Direction chooseDirection(Board board, Ghost ghost, Pacman pacman,
            GhostStrategyMode strategyMode, List<Ghost> ghosts) {
        List<Direction> options = new ArrayList<>();
        for (Direction direction : SEARCH_ORDER) {
            if (board.neighbor(ghost.position(), direction) != null) {
                options.add(direction);
            }
        }
        if (options.size() > 1 && ghost.direction() != Direction.STOPPED) {
            options.remove(ghost.direction().opposite());
        }
        if (options.isEmpty()) {
            return Direction.STOPPED;
        }
        if (ghost.mode() == GhostMode.FRIGHTENED) {
            return options.get(random.nextInt(options.size()));
        }

        Position target = board.nearestOpenPosition(
                targetFor(board, ghost, pacman, strategyMode, ghosts));
        Direction bestDirection = options.getFirst();
        long bestDistance = Long.MAX_VALUE;
        for (Direction direction : options) {
            Position candidate = board.neighbor(ghost.position(), direction);
            long distance = squaredDistance(candidate, target);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestDirection = direction;
            }
        }
        return bestDirection;
    }

    private static Position aheadOf(Board board, Pacman pacman, int tiles) {
        Direction direction = pacman.direction();
        return normalizeTunnelTarget(board, new Position(
                pacman.position().x() + direction.dx() * tiles,
                pacman.position().y() + direction.dy() * tiles));
    }

    private static Position pinkyTarget(Board board, Pacman pacman) {
        Position ahead = aheadOf(board, pacman, 4);
        if (pacman.direction() == Direction.UP) {
            ahead = new Position(ahead.x() - 4, ahead.y());
        }
        return ahead;
    }

    private static Position inkyTarget(Board board, Pacman pacman, Position blinky) {
        Position pivot = aheadOf(board, pacman, 2);
        int vectorX = pivot.x() - blinky.x();
        int vectorY = pivot.y() - blinky.y();
        return normalizeTunnelTarget(board,
                new Position(
                        blinky.x() + 2 * vectorX,
                        blinky.y() + 2 * vectorY));
    }

    private static Position blinkyPosition(List<Ghost> ghosts, Ghost fallback) {
        return ghosts.stream()
                .filter(ghost -> ghost.type() == GhostType.BLINKY)
                .map(Ghost::position)
                .findFirst()
                .orElse(fallback.position());
    }

    private static Position scatterTarget(Board board, GhostType type) {
        return switch (type) {
            case BLINKY -> new Position(board.width() - 2, 1);
            case PINKY -> new Position(1, 1);
            case INKY -> new Position(board.width() - 2, board.height() - 2);
            case CLYDE -> new Position(1, board.height() - 2);
        };
    }

    private static Position normalizeTunnelTarget(Board board, Position target) {
        if (target.y() < 0 || target.y() >= board.height()) {
            return target;
        }
        Position leftEdge = new Position(0, target.y());
        Position rightEdge = new Position(board.width() - 1, target.y());
        if (board.neighbor(leftEdge, Direction.LEFT) != null
                && board.neighbor(rightEdge, Direction.RIGHT) != null) {
            return new Position(Math.floorMod(target.x(), board.width()), target.y());
        }
        return target;
    }

    private static long squaredDistance(Position first, Position second) {
        long dx = first.x() - second.x();
        long dy = first.y() - second.y();
        return dx * dx + dy * dy;
    }
}
