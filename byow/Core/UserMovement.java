package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class UserMovement {
    final Engine engine;
    //track position->check input-> validate move -> update files -> re-render -> repeat
    public int currentX;
    public int currentY;
    private boolean flowerEaten;
    private String value;
    TETile[][] tiles;
    int count = 0;

    public UserMovement(int x, int y, TETile[][] world, Engine engine) {
        //create an object that receives the avatar's coordinates. It inputs them as the currentX and currentY. It then updates
        //the avatar's position to check it for appropriate movement when user inputs keys.
        this.tiles = world;
        this.currentX = x;
        this.currentY = y;
        this.engine = engine;
    }

    public void processMove(char input) {
        count++;
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
            // Update hashmap at old position
            RandomPlaces.placesMap.put(new RandomPlaces.Coordinate(currentX, currentY), "floor");
            //update tiles at old position
            tiles[currentX][currentY] = Tileset.FLOOR;
            //move to new position
            currentX = newX;
            currentY = newY;
            // Update tiles
            RandomPlaces.placesMap.put(new RandomPlaces.Coordinate(currentX, currentY), "avatar");
            tiles[currentX][currentY] = Tileset.AVATAR;
            if(flowerEaten) {
                Engine.flowers--;
                flowerEaten=false;
            }
        }
    }

    private boolean checkInput (String string) {
        // If hashmap says something unexpected, check the actual tile
        if (string == null || "water".equals(string)) {
            // Check actual tile in world array
            // If it's FLOOR, allow movement
        }
        if("flower".equals(string)) {
            flowerEaten = true;
            return true;
        }
        else if ("water".equals(string)) {
            engine.userMenu.eatenByEntity = true;
            engine.userMenu.processUserInput('Q');
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
