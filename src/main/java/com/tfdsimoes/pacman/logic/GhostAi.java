package com.tfdsimoes.pacman.logic;

import com.tfdsimoes.pacman.model.Board;
import com.tfdsimoes.pacman.model.Direction;
import com.tfdsimoes.pacman.model.Ghost;
import com.tfdsimoes.pacman.model.GhostMode;
import com.tfdsimoes.pacman.model.GhostStrategyMode;
import com.tfdsimoes.pacman.model.GhostType;
import com.tfdsimoes.pacman.model.Pacman;
import com.tfdsimoes.pacman.model.Position;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

public final class GhostAi {
    private static final List<Direction> SEARCH_ORDER = List.of(
            Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT);

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
            case PINKY -> aheadOf(board, pacman, 4);
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

        boolean frightened = ghost.mode() == GhostMode.FRIGHTENED;
        Position target = frightened ? pacman.position()
                : board.nearestOpenPosition(targetFor(board, ghost, pacman, strategyMode, ghosts));
        Direction bestDirection = options.getFirst();
        int bestDistance = frightened ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (Direction direction : options) {
            Position candidate = board.neighbor(ghost.position(), direction);
            int distance = shortestDistance(board, candidate, target);
            if (frightened ? distance > bestDistance : distance < bestDistance) {
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

    private static Position inkyTarget(Board board, Pacman pacman, Position blinky) {
        Position ahead = aheadOf(board, pacman, 2);
        return normalizeTunnelTarget(board,
                new Position(2 * ahead.x() - blinky.x(), 2 * ahead.y() - blinky.y()));
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

    private static int shortestDistance(Board board, Position start, Position target) {
        Queue<Position> queue = new ArrayDeque<>();
        Map<Position, Integer> distances = new HashMap<>();
        queue.add(start);
        distances.put(start, 0);
        while (!queue.isEmpty()) {
            Position current = queue.remove();
            int distance = distances.get(current);
            if (current.equals(target)) {
                return distance;
            }
            for (Direction direction : SEARCH_ORDER) {
                Position neighbor = board.neighbor(current, direction);
                if (neighbor != null && !distances.containsKey(neighbor)) {
                    distances.put(neighbor, distance + 1);
                    queue.add(neighbor);
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    private static long squaredDistance(Position first, Position second) {
        long dx = first.x() - second.x();
        long dy = first.y() - second.y();
        return dx * dx + dy * dy;
    }
}
