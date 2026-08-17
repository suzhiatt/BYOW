package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;

public class Entity {

    public int currentX;
    public int currentY;
    EntityPathFinder pathFinder;
    List<RandomPlaces.Coordinate> path;
    TETile[][] world;
    public int pathIndex;
    UserMovement userMovement;
    RandomPlaces.Coordinate next;
    String replaceValue;
    int count = 0;
    boolean highlightPath = true;
    List<RandomPlaces.Coordinate> highlightedPathList;

    public Entity(int x, int y, TETile[][] world, UserMovement userMovement) {
        this.currentX = x;
        this.currentY = y;
        this.world = world;
        this.userMovement = userMovement;
        pathFinder = new EntityPathFinder(this.world);
    }

//    Stores current path (list of coordinates)
    public void storePath() {
        pathIndex = 1;
        path = pathFinder.findPath(currentX, currentY, userMovement.currentX, userMovement.currentY);
    }

    public void highlightPath() {
        if(path == null) {
            return;
        }
        if(highlightPath) {
           if(highlightedPathList!=null) {
               highlightedPathList.clear();
           }
            storePath();
            int avatarIndex = path.indexOf(new RandomPlaces.Coordinate(userMovement.currentX, userMovement.currentY));
            int entityIndex = path.indexOf(new RandomPlaces.Coordinate(currentX, currentY));
            highlightedPathList = new ArrayList<>(path.subList(entityIndex+1, avatarIndex));

            for(RandomPlaces.Coordinate coord : highlightedPathList) {
                if(world[coord.x][coord.y] != Tileset.FLOWER) {
                    world[coord.x][coord.y] = Tileset.FLOORS;
                    }
                }
            }

        else {
            if (highlightedPathList != null) {
                for (RandomPlaces.Coordinate coord : highlightedPathList) {
                    if ((coord.x != currentX || coord.y != currentY) && (coord.x != userMovement.currentX || coord.y != userMovement.currentY)) {
                        if (world[coord.x][coord.y] != Tileset.FLOWER) {
                            world[coord.x][coord.y] = Tileset.FLOOR;
                        }
                    }
                }
            }
        }
    }

//    Has a method to move along the path
    public void pathMovement() {
        if(!highlightPath) {
            //restore old highlights
            highlightPath();
            //clear old lists
            if(highlightedPathList!=null) {
                highlightedPathList.clear();
            }
            //highlightPath();
            highlightPath = true;
            highlightPath();
            highlightPath = false;
        }
        if(count > 3) {
            count = 0;
            storePath();
        }
        if (path != null && pathIndex < path.size()) {
            next = path.get(pathIndex);

            if(replaceValue == null) {
                replaceValue = "floor";
            }
                //restore the OLD position (using placeValue from previous iteration)
                RandomPlaces.placesMap.put(new RandomPlaces.Coordinate(currentX, currentY), replaceValue);

                if("flower".equals(replaceValue)) {
                    world[currentX][currentY]= Tileset.FLOWER;
                }
                else {
                    world[currentX][currentY] = Tileset.FLOOR;
                }

            //save what is at new position before moving there...
                replaceValue = RandomPlaces.getValue(new RandomPlaces.Coordinate(next.x, next.y));
                //move into new position
                currentX = next.x;
                currentY = next.y;

                //check for collision
                if(world[currentX][currentY] == Tileset.AVATAR) {
                    System.out.println("The avatar is eaten by water, from the Entity class, so game should quit");
                    userMovement.engine.userMenu.eatenByEntity = true;
                    userMovement.engine.userMenu.processUserInput('Q');
                }

                //place entity at new position
                RandomPlaces.placesMap.put(new RandomPlaces.Coordinate(currentX, currentY), "water");
                world[currentX][currentY]= Tileset.WATER;
            pathIndex++;
            count++;
        }
        else {
            System.out.println("Made it to the else on line 117");
            count = 0;
            storePath();
        }
    }
}
