package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Queue;
import java.util.LinkedList;

import java.util.*;

public class EntityPathFinder {
    private int[] edgeTo;
    TETile[][] world;
    Engine engine;
    public List path;


    public EntityPathFinder(Engine engine, TETile[][] world) {
        this.engine = engine;
        this.world = world;
    }

    public List<RandomPlaces.Coordinate> findPath(int startX, int startY, int targetX, int targetY) {
        //visited set- use HashSet of Coordinates
        Set<RandomPlaces.Coordinate> visited = new HashSet<>();

        //parent map to reconstruct path
        Map<RandomPlaces.Coordinate, RandomPlaces.Coordinate> parent = new HashMap<>();
        //Queue for BFS
        Queue<RandomPlaces.Coordinate> queue = new LinkedList<>();

        RandomPlaces.Coordinate start = new RandomPlaces.Coordinate(startX, startY);
        RandomPlaces.Coordinate target = new RandomPlaces.Coordinate(targetX, targetY);

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            RandomPlaces.Coordinate current = queue.poll();

            if(current.x == target.x && current.y == target.y) {
                return reconstructPath(parent, start, target);
            }

            //check all 4 neighbors
            int[] dx = {0, 0, -1, 1};
            int[] dy = {1, -1, 0, 0};

            for (int i = 0; i < 4; i++) {
                int newX = current.x + dx[i];
                int newY = current.y + dy[i];
                RandomPlaces.Coordinate neighbor = new RandomPlaces.Coordinate(newX, newY);

                if(isValidAndWalkable(newX, newY) && !visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        return null;
    }

    private boolean isValidAndWalkable(int x, int y) {
        if(x < 0 || x >= world.length || y < 0 || y >= world[0].length) {
            return false;
        }
        String tile = RandomPlaces.placesMap.get(new RandomPlaces.Coordinate(x, y));
        return "floor".equals(tile) || "roomFloor".equals(tile) || "flower".equals(tile);
    }

    private List<RandomPlaces.Coordinate> reconstructPath(Map<RandomPlaces.Coordinate, RandomPlaces.Coordinate> parent,
                                                          RandomPlaces.Coordinate start,
                                                          RandomPlaces.Coordinate target) {
        List<RandomPlaces.Coordinate> path = new ArrayList<>();
        RandomPlaces.Coordinate current = target;

        //work backwards from target to start
        while (current != null) {
            path.add(0, current); //add to front (reverse order)
            current = parent.get(current);
        }
        return path;
    }
}

