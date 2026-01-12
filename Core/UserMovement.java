package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

import static byow.Core.RandomPlaces.placesMap;

public class UserMovement {
    private final Engine engine;
    //track position->check input-> validate move -> update files -> re-render -> repeat
    private int currentX;
    private int currentY;
    private boolean flowerEaten;
    private String value;
    TETile[][] tiles;

    public UserMovement(int x, int y, TETile[][] world, Engine engine) {
        //create an object that receives the avatar's coordinates. It inputs them as the currentX and currentY. It then updates
        //the avatar's position to check it for appropriate movement when user inputs keys.
        this.tiles = world;
        this.currentX = x;
        this.currentY = y;
        this.engine = engine;
    }

    public void processMove(char input) {
        int newX = currentX;
        int newY = currentY;
        boolean change;

        switch (input) {
            case 'W': newY++; break;
            case 'S': newY--; break;
            case 'A': newX--; break;
            case 'D': newX++; break;
            default: return;
        }

        value = RandomPlaces.getValue(new RandomPlaces.Coordinate(newX, newY));
        change = checkInput(value);

        if(change) {
            // Update hashmap
            RandomPlaces.placesMap.put(new RandomPlaces.Coordinate(newX, newY), "floor");
            // Update tiles
            tiles[currentX][currentY] = Tileset.FLOOR;
            currentX = newX;
            currentY = newY;
            tiles[currentX][currentY] = Tileset.AVATAR;
            if(flowerEaten) {
                engine.flowers--;
                flowerEaten=false;
            }
        }
    }

    private boolean checkInput (String string) {
        if("flower".equals(string)) {
            flowerEaten = true;
            return true;
        }
        else if ("floor".equals(string) || "roomFloor".equals(string)) {
            return true;
        }
        else {
            checkLockedDoor(string);
        }
            return false;
        }

    private void checkLockedDoor(String string) {
        if("lockedDoor".equals(string) && Engine.flowers<=0) {
            engine.nextLevel();
        }
    }
}
