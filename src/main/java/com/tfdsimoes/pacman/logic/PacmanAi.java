package com.tfdsimoes.pacman.logic;

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
    private static final int GHOST_ALERT_DISTANCE = 14;

    @Override
    public Direction chooseDirection(GameSession game) {
        Objects.requireNonNull(game);
        Board board = game.board();
        Position current = game.pacman().position();
        Map<Position, Integer> currentDistances = distancesFrom(board, current);
        Position target = selectTarget(game, currentDistances);
        if (target == null) {
            return Direction.STOPPED;
        }

        Direction bestDirection = Direction.STOPPED;
        int bestDistance = Integer.MAX_VALUE;
        for (Direction direction : SEARCH_ORDER) {
            Position candidate = board.neighbor(current, direction);
            if (candidate == null) {
                continue;
            }
            int distance = distancesFrom(board, candidate).getOrDefault(target, Integer.MAX_VALUE);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestDirection = direction;
            }
        }
        return bestDirection;
    }

    private Position selectTarget(GameSession game, Map<Position, Integer> distances) {
        Board board = game.board();
        boolean ghostNearby = game.ghosts().stream()
                .filter(ghost -> ghost.mode() == GhostMode.NORMAL)
                .map(ghost -> distances.get(ghost.position()))
                .filter(Objects::nonNull)
                .anyMatch(distance -> distance <= GHOST_ALERT_DISTANCE);
        Position nearestPowerPellet = nearestCollectible(board, distances, Tile.POWER_PELLET);
        if (ghostNearby && nearestPowerPellet != null) {
            return nearestPowerPellet;
        }
        return nearestCollectible(board, distances, null);
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
