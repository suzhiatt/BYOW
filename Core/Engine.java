package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.io.IOException;

public class Engine {
    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;
    private TERenderer ter;
    int avatarX;
    int avatarY;
    TETile[][] world;
    UserMovement userMove;
    UserMenu userMenu;

    // creates a world background when constructor is called
    public Engine() {
        userMenu = new UserMenu();
    }

    public void createWorld(long seed) {
        RandomPlaces generate = new RandomPlaces(WIDTH, HEIGHT, seed);
        world = generate.getTiles();
        ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        avatarX = (generate.getFirstX() + 1);
        avatarY = (generate.getFirstY() + 1);
        world[avatarX][avatarY] = Tileset.AVATAR;
        ter.renderFrame(world);
        userMove = new UserMovement(avatarX, avatarY, world);
        beginGamePlay();
    }

    public void beginGamePlay() {
        char input = getInput();
        if(input == ':') {
            char next = getInput();
            if(next != 'Q') {
                beginGamePlay();
            }
            else {
                SaveGame save = new SaveGame();
                try {
                    save.saveGame(world);
                    userMenu.processUserInput('Q');

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else {
            userMove.processMove(input);
            ter.renderFrame(world);
            beginGamePlay();
        }
    }

    public void loadWorld() {
        ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(world);
        userMove = new UserMovement(avatarX, avatarY, world);
        beginGamePlay();
    }

    public char getInput() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                return Character.toUpperCase(StdDraw.nextKeyTyped());
            }
            //the program will make it check indefinitely, so this causes CPU to reduce usage while still responsive.
            StdDraw.pause(20);
        }
    }

    public void interactWithKeyboard() {
        userMenu.drawFrame("Welcome to Susie's World");
        //receive input from user and store as "choice"
        char choice = userMenu.solicitUserInput();
        //call another method to process user's choice
        userMenu.processUserInput(choice);
    }


}