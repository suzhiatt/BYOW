package byow.Core;

import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;
import byow.TileEngine.Tileset;

import java.io.IOException;

public class UserMovement {
    //track position->check input-> validate move -> update files -> re-render -> repeat
    private int currentX;
    private int currentY;
    private int oldX;
    private int oldY;
    private String string;
    TETile[][] tiles;

    public UserMovement(int x, int y, TETile[][] world) {
        //create an object that receives the avatar's coordinates. It inputs them as the currentX and currentY. It then updates
        //the avatar's position to check it for appropriate movement when user inputs keys.
        this.tiles = world;
        this.currentX = x;
        this.currentY = y;
    }

 public void processMove (char input) {
        if(isValid(input)) {
//            System.out.println("Input is valid, so oldX is " + oldX + " oldY is " + oldY);
//            System.out.println("Input is valid, so currentX is " + currentX + " currentY is " + currentY);
            tiles[oldX][oldY]=Tileset.FLOOR;
            tiles[currentX][currentY] = Tileset.AVATAR;
        }
 }

    //create method that executes the flow...
//    public void processMovement() {
//        char input = userInput();
//        System.out.println("input is " + input);
//        if(isValid(input)) {
//                tiles[oldX][oldY] = Tileset.FLOOR;
//                tiles[currentX][currentY] = Tileset.AVATAR;
//        }
//    }

    //check for input, how do I run this constantly from the
//    private char userInput() {
//        //this loop keeps checking for input. Returns immediately when key is pressed. Continues waiting if no key pressed.
//        while (true) {
//            if (StdDraw.hasNextKeyTyped()) {
//                return Character.toUpperCase(StdDraw.nextKeyTyped());
//            }
//            //the program will make it check indefinitely, so this causes CPU to reduce usage while still responsive.
//            StdDraw.pause(20);
//        }
//    }

    //validate move: is this a wall? if so, can't go there
    public boolean isValid(char key) {
        if (key == 'W') {
            String string = RandomPlaces.getValue(new RandomPlaces.Coordinate(currentX, currentY + 1));
            if (!"wall".equals(string) && !"endMiddle".equals(string)) {
                System.out.println("W is found valid. Coordinate is " + RandomPlaces.getValue(new RandomPlaces.Coordinate(currentX+1, currentY)));
                keyW();
                return true;
            }
        }
        if (key == 'S') {
            String string = RandomPlaces.getValue(new RandomPlaces.Coordinate(currentX, currentY - 1));
            if (!"wall".equals(string) && !"endMiddle".equals(string)) {
//                System.out.println("S is found valid. Coordinate is " + RandomPlaces.getValue(new RandomPlaces.Coordinate(currentX+1, currentY)));
                keyS();
                return true;
            }
        }
        if (key == 'A') {
            String string = RandomPlaces.getValue(new RandomPlaces.Coordinate(currentX-1, currentY));
            if (!"wall".equals(string) && !"endMiddle".equals(string)) {
//                System.out.println("A is found valid. Coordinate is " + RandomPlaces.getValue(new RandomPlaces.Coordinate(currentX+1, currentY)));
                keyA();
                return true;
            }
        }
        if (key == 'D') {
            String string = RandomPlaces.getValue(new RandomPlaces.Coordinate(currentX+1, currentY));
            if (!"wall".equals(string) && !"endMiddle".equals(string)) {
               System.out.println("D is found valid. Coordinate is " + RandomPlaces.getValue(new RandomPlaces.Coordinate(currentX+1, currentY)));
                keyD();
                return true;
            }
            else {
                System.out.println("#####D is found invalid. Coordinate is " + RandomPlaces.getValue(new RandomPlaces.Coordinate(currentX+1, currentY)));
            }
        }
        if(key==':') {
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
//    System.out.println("A wall doesn't exist for W , so oldX is " + oldX + " oldY is " + oldY + " currentY is " + currentY);
}

private void keyS() {
    oldX = currentX;
    oldY = currentY;
    currentY = oldY - 1;
//    System.out.println("A wall doesn't exist for S, so oldX is " + oldX + " oldY is " + oldY + " currentY is " + currentY);
    }

private void keyA() {
    oldX = currentX;
    oldY = currentY;
    currentX = oldX-1;
//    System.out.println("A wall doesn't exist for A, so oldX is " + oldX + " oldY is " + oldY + " currentY is " + currentY);
}

private void keyD() {
    oldX = currentX;
    oldY = currentY;
    currentX = oldX + 1;
//    System.out.println("A wall doesn't exist for D, so oldX is " + oldX + " oldY is " + oldY + " currentY is " + currentY);
}

    //update files

    //re-render world


}
