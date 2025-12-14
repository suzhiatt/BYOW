package byow.Core;

import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;
import byow.TileEngine.Tileset;

import java.io.IOException;
import java.util.HashMap;

import static byow.Core.RandomPlaces.placesMap;

public class UserMovement {
    //track position->check input-> validate move -> update files -> re-render -> repeat
    private TETile avatarTile;
    private int currentX;
    private int currentY;
    private int oldX;
    private int oldY;
    private String string;
    TETile[][] tiles;

    public UserMovement(int x, int y) {
        //create an object that receives the avatar's coordinates. It inputs them as the currentX and currentY. It then updates
        //the avatar's position to check it for appropriate movement when user inputs keys.
        this.currentX = x;
        this.currentY = y;
    }

    //create method that executes the flow...
    public void processMovement() {
        char input = userInput();
        System.out.println("input is " + input);
//        if(isValid(input)) {
//                tiles[oldX][oldY] = Tileset.FLOOR;
//                tiles[currentX][currentY] = Tileset.AVATAR;
//                avatarTile = tiles[currentX][currentY];
//        }
    }

    //check for input, how do I run this constantly from the
    private char userInput() {
        //this loop keeps checking for input. Returns immediately when key is pressed. Continues waiting if no key pressed.
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                return Character.toUpperCase(StdDraw.nextKeyTyped());
            }
            //the program will make it check indefinitely, so this causes CPU to reduce usage while still responsive.
            StdDraw.pause(20);
        }
    }

    //validate move: is this a wall? if so, can't go there
    public boolean isValid(char key) throws IOException {
        if (key == 'W') {
            if (!"wall".equals(RandomPlaces.getValue(new RandomPlaces.Coordinate(currentX, currentY + 1)))) {
                keyW();
                return true;
            }
        }
        if (key == 'S') {
            if ("wall".equals(RandomPlaces.getValue(new RandomPlaces.Coordinate(currentX, currentY - 1)))) {
                keyS();
                return true;
            }
        }
        if (key == 'A') {
            if ("wall".equals(RandomPlaces.getValue(new RandomPlaces.Coordinate(currentX - 1, currentY)))) {
                keyA();
                return true;
            }
        }
        if (key == 'D') {
            if ("wall".equals(RandomPlaces.getValue(new RandomPlaces.Coordinate(currentX, currentY + 1)))) {
                keyD();
                return true;
            }
        }
        if(key=='Q') {
            UserMenu menu = new UserMenu();
            menu.processUserInput('Q');
        }
        else {
            System.out.println("Please press W, S, A, D or :Q to quit");
            return false;
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
    currentY = oldY + 1;
}

    //update files

    //re-render world


}
