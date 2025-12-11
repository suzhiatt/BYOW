package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.io.IOException;

public class Engine {
    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;
    private TERenderer ter;

    // creates a world background when constructor is called
    public Engine() {
    }

    public void createWorld(long seed) {
        RandomPlaces generate = new RandomPlaces(WIDTH, HEIGHT, seed);
        TETile[][] world = generate.getTiles();
        ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(world);
    }
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() throws IOException {
        UserMenu userMenu = new UserMenu();
        userMenu.drawFrame("Welcome to Susie's World");
        //receive input from user and store as "choice"
        char choice = userMenu.solicitUserInput();
        //call another method to process user's choice
        userMenu.processUserInput(choice);

    }


    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
//    public TETile[][] interactWithInputString(String input) {
//        // TODO: Fill out this method so that it run the engine using the input
//        // passed in as an argument, and return a 2D tile representation of the
//        // world that would have been drawn if the same inputs had been given
//        // to interactWithKeyboard().
//        //
//        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
//        // that works for many different input types.
//        createWorld();
//
//        TETile[][] finalWorldFrame = null;
//        return finalWorldFrame;
//    }
}
