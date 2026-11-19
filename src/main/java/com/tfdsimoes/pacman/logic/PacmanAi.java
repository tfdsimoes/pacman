package com.tfdsimoes.pacman.logic;

import com.tfdsimoes.pacman.GameConfig;
import com.tfdsimoes.pacman.model.Board;
import com.tfdsimoes.pacman.model.Direction;
import com.tfdsimoes.pacman.model.Ghost;
import com.tfdsimoes.pacman.model.GhostMode;
import com.tfdsimoes.pacman.model.Pacman;
import com.tfdsimoes.pacman.model.Position;
import com.tfdsimoes.pacman.model.Tile;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

public final class PacmanAi implements PacmanController {
    private static final List<Direction> SEARCH_ORDER = List.of(
            Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT);
    @Override
    public Direction chooseDirection(GameSession game) {
        Objects.requireNonNull(game);
        Board board = game.board();
        Position current = game.pacman().position();
        Map<Position, Integer> currentDistances = distancesFrom(board, current);
        boolean threatened = nearestNormalGhostDistance(game, currentDistances)
                <= GameConfig.PACMAN_GHOST_ALERT_DISTANCE;
        Position target = selectTarget(game, currentDistances, threatened);
        if (target == null) {
            return Direction.STOPPED;
        }

        Direction bestDirection = Direction.STOPPED;
        int bestDistance = Integer.MAX_VALUE;
        long bestSafety = Long.MIN_VALUE;
        for (Direction direction : SEARCH_ORDER) {
            Position candidate = board.neighbor(current, direction);
            if (candidate == null) {
                continue;
            }
            Map<Position, Integer> candidateDistances = distancesFrom(board, candidate);
            int distance = candidateDistances.getOrDefault(target, Integer.MAX_VALUE);
            long safety = threatened
                    ? safetyScore(game, candidateDistances, isDeadEnd(board, candidate, current))
                    : 0;
            if (threatened
                    ? safety > bestSafety || safety == bestSafety && distance < bestDistance
                    : distance < bestDistance) {
                bestSafety = safety;
                bestDistance = distance;
                bestDirection = direction;
            }
        }
        return bestDirection;
    }

    private Position selectTarget(GameSession game, Map<Position, Integer> distances, boolean threatened) {
        Board board = game.board();
        if (threatened) {
            Position nearestPowerPellet = nearestCollectible(board, distances, Tile.POWER_PELLET);
            if (nearestPowerPellet != null) {
                return nearestPowerPellet;
            }
        } else {
            Position nearestFrightenedGhost = nearestGhost(game, distances, GhostMode.FRIGHTENED);
            if (nearestFrightenedGhost != null
                    && distances.get(nearestFrightenedGhost) <= GameConfig.PACMAN_GHOST_ALERT_DISTANCE) {
                return nearestFrightenedGhost;
            }
        }
        return nearestCollectible(board, distances, null);
    }

    private static Position nearestGhost(
            GameSession game, Map<Position, Integer> distances, GhostMode mode) {
        Position nearest = null;
        int bestDistance = Integer.MAX_VALUE;
        for (Ghost ghost : game.ghosts()) {
            if (ghost.mode() != mode) {
                continue;
            }
            int distance = distances.getOrDefault(ghost.position(), Integer.MAX_VALUE);
            if (distance < bestDistance) {
                nearest = ghost.position();
                bestDistance = distance;
            }
        }
        return nearest;
    }

    private static int nearestNormalGhostDistance(GameSession game, Map<Position, Integer> distances) {
        return game.ghosts().stream()
                .filter(ghost -> ghost.mode() == GhostMode.NORMAL)
                .mapToInt(ghost -> distances.getOrDefault(ghost.position(), Integer.MAX_VALUE))
                .min()
                .orElse(Integer.MAX_VALUE);
    }

    private static long safetyScore(
            GameSession game, Map<Position, Integer> distances, boolean deadEnd) {
        int dangerDistance = nearestNormalGhostDistance(game, distances);
        if (dangerDistance == Integer.MAX_VALUE) {
            return Long.MAX_VALUE;
        }
        return dangerDistance - (deadEnd ? GameConfig.PACMAN_DEAD_END_DANGER_PENALTY : 0);
    }

    private static boolean isDeadEnd(Board board, Position position, Position previousPosition) {
        for (Direction direction : SEARCH_ORDER) {
            Position neighbor = board.neighbor(position, direction);
            if (neighbor != null && !neighbor.equals(previousPosition)) {
                return false;
            }
        }
        return true;
    }

    private Position nearestCollectible(Board board, Map<Position, Integer> distances, Tile requestedTile) {
        Position nearest = null;
        int bestDistance = Integer.MAX_VALUE;
        for (int y = 0; y < board.height(); y++) {
            for (int x = 0; x < board.width(); x++) {
                Position position = new Position(x, y);
                Tile tile = board.tileAt(position);
                if (requestedTile == null ? !isCollectible(tile) : tile != requestedTile) {
                    continue;
                }
                int distance = distances.getOrDefault(position, Integer.MAX_VALUE);
                if (distance < bestDistance) {
                    nearest = position;
                    bestDistance = distance;
                }
            }
        }
        return nearest;
    }

    private static boolean isCollectible(Tile tile) {
        return tile == Tile.PELLET || tile == Tile.POWER_PELLET;
    }

    private static Map<Position, Integer> distancesFrom(Board board, Position start) {
        Map<Position, Integer> distances = new HashMap<>();
        Queue<Position> queue = new ArrayDeque<>();
        distances.put(start, 0);
        queue.add(start);
        while (!queue.isEmpty()) {
            Position current = queue.remove();
            int distance = distances.get(current);
            for (Direction direction : SEARCH_ORDER) {
                Position neighbor = board.neighbor(current, direction);
                if (neighbor != null && !distances.containsKey(neighbor)) {
                    distances.put(neighbor, distance + 1);
                    queue.add(neighbor);
                }
            }
        }
        return distances;
    }
}
