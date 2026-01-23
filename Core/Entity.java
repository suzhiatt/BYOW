package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

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
    String replaceValue = "floor";
    int count = 5;

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

//    Has a method to move along the path
    public void pathMovement() {
        if(count > 5) {
            count = 0;
            System.out.println("Storing a new path");
            System.out.println("The path size, when a new thing is called is: " + path.size());
            storePath();
        }
        if (path != null && pathIndex < path.size()) {
            next = path.get(pathIndex);
                //replace current tile
                RandomPlaces.placesMap.put(new RandomPlaces.Coordinate(currentX, currentY), replaceValue);

                if("flower".equals(replaceValue)) {
                    world[currentX][currentY]= Tileset.FLOWER;
                }
                else {
                    world[currentX][currentY] = Tileset.FLOOR;
                }
                currentX=next.x;
                currentY=next.y;
                if(world[next.x][next.y] == Tileset.AVATAR) {
                    System.out.println("The avatar is eaten by water, from the Entity class, so game should quit");
                    UserMenu userMenu = new UserMenu();
                    userMenu.eatenByEntity= true;
                    userMenu.processUserInput('Q');
                }
                //hold next tile string
                replaceValue = RandomPlaces.getValue(new RandomPlaces.Coordinate(next.x, next.y));
                //replace next tile with Entity
                RandomPlaces.placesMap.put(new RandomPlaces.Coordinate(next.x, next.y), "water");
                world[next.x][next.y]= Tileset.WATER;
            pathIndex++;
            count++;
        }
        else {
            count = 0;
            storePath();
        }
    }
}
