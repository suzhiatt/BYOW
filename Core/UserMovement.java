package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class UserMovement {
    //track position->check input-> validate move -> update files -> re-render -> repeat
    private int currentX;
    private int currentY;
    private int oldX;
    private int oldY;
    private String string;
    TETile[][] tiles;
    boolean lockedDoor = false;
    Random r = new Random();

    public UserMovement(int x, int y, TETile[][] world) {
        //create an object that receives the avatar's coordinates. It inputs them as the currentX and currentY. It then updates
        //the avatar's position to check it for appropriate movement when user inputs keys.
        this.tiles = world;
        this.currentX = x;
        this.currentY = y;
    }

 public void processMove (char input) {
        if(isValid(input)) {
            tiles[oldX][oldY]=Tileset.FLOOR;
            tiles[currentX][currentY] = Tileset.AVATAR;
        }
 }

    //validate move: is this a wall? if so, can't go there
    public boolean isValid(char key) {

        if (key == 'W') {
            String string = RandomPlaces.getValue(new RandomPlaces.Coordinate(currentX, currentY + 1));
            checkLockedDoor(string);
            if (!"wall".equals(string) && !"endMiddle".equals(string)) {
                keyW();
                return true;
            }
        }
        if (key == 'S') {
            String string = RandomPlaces.getValue(new RandomPlaces.Coordinate(currentX, currentY - 1));
            checkLockedDoor(string);
            if (!"wall".equals(string) && !"endMiddle".equals(string)) {
                keyS();
                return true;
            }
        }
        if (key == 'A') {
            String string = RandomPlaces.getValue(new RandomPlaces.Coordinate(currentX-1, currentY));
            checkLockedDoor(string);
            if (!"wall".equals(string) && !"endMiddle".equals(string)) {
                keyA();
                return true;
            }
        }
        if (key == 'D') {
            String string = RandomPlaces.getValue(new RandomPlaces.Coordinate(currentX+1, currentY));
            checkLockedDoor(string);
            if (!"wall".equals(string) && !"endMiddle".equals(string)) {
                keyD();
                return true;
            }
            else {
                System.out.println("#####D is found invalid. Coordinate is " + RandomPlaces.getValue(new RandomPlaces.Coordinate(currentX+1, currentY)));
            }
        }
        return true;
    }

private void keyW() {
    oldX =currentX;
    oldY =currentY;
    currentY =oldY + 1;
    }

private void keyS() {
    oldX = currentX;
    oldY = currentY;
    currentY = oldY - 1;
    }

private void keyA() {
    oldX = currentX;
    oldY = currentY;
    currentX = oldX-1;
    }

private void keyD() {
    oldX = currentX;
    oldY = currentY;
    currentX = oldX + 1;
    }

private boolean checkLockedDoor(String string) {
        if("lockedDoor".equals(string)) {
            nextLevel();
            return lockedDoor=true;
        }
        return false;
    }

private void nextLevel() {
        //use a random generator number and call the engine new World thing
    long seed = r.nextLong();
    Engine engine = new Engine();
    engine.level++;
    engine.createWorld(seed);
    }

}
