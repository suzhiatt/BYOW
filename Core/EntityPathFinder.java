package byow.Core;

import byow.TileEngine.TETile;
import java.util.Queue;
import java.util.LinkedList;

import java.util.*;

public class EntityPathFinder {
    TETile[][] world;

    public EntityPathFinder(TETile[][] world) {
        this.world = world;
    }

    //method called by Entity class using coords of entity and avatar
    public List<RandomPlaces.Coordinate> findPath(int startX, int startY, int targetX, int targetY) {

        Set<RandomPlaces.Coordinate> visited = new HashSet<>();

        //reconstruct path once we find the target, return it as a map to follow
        Map<RandomPlaces.Coordinate, RandomPlaces.Coordinate> parent = new HashMap<>();

        Queue<RandomPlaces.Coordinate> queue = new LinkedList<>();


        RandomPlaces.Coordinate start = new RandomPlaces.Coordinate(startX, startY);
        RandomPlaces.Coordinate target = new RandomPlaces.Coordinate(targetX, targetY);

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            RandomPlaces.Coordinate current = queue.poll();

            //if the current coord match the avatar's coord, then reconstruct path
            if(current.x == target.x && current.y == target.y) {
                return reconstructPath(parent, start, target);
            }

            int[] dx = {0, 0, -1, 1};
            int[] dy = {1, -1, 0, 0};

            //for each neighbor, add to queue
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

    //checks to make sure the new coord. is within bounds and isn't a wall
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

        //begins at target, ends at start, adding to list backwards
        while (current != null) {
            path.add(0, current);
            current = parent.get(current);
        }
        return path;
    }
}

