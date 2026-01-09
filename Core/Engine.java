package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Random;

public class Engine {
    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;
    private TERenderer ter;
    int avatarX;
    int avatarY;
    int lockedDoorX;
    int lockedDoorY;
    long startTime;
    public static int level = 1;
    long targetDuration = (25_000_000_000L) * level;
    TETile[][] world;
    UserMovement userMove;
    UserMenu userMenu;
    int flowers;

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
        setLockedDoor();
        flowers = level;
        System.out.println("Flowers are: " + flowers + " and level is: " + level);
        setFlower();
        ter.renderFrame(world);
        startTime();
        userMove = new UserMovement(avatarX, avatarY, world, this);
        System.out.println("THE LEVEL IS...." + level);
        beginGamePlay();
    }

    public void beginGamePlay() {
        checkTime();
        char input = getInput();
        if (input == ':') {
            char next = getInput();
            if (next != 'Q') {
                beginGamePlay();
            } else {
                SaveGame save = new SaveGame();
                try {
                    save.saveGame(world, level);
                    userMenu.processUserInput('Q');

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            userMove.processMove(input);
            ter.renderFrame(world);
            beginGamePlay();
        }
    }

    public void loadWorld() {
        ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(world);
        startTime();
        userMove = new UserMovement(avatarX, avatarY, world, this);
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

    public void startTime() {
        //start nanoTime
        startTime = System.nanoTime();
    }

    public void checkTime() {
        long currentTime = System.nanoTime();
        long elapsedTime = currentTime - startTime;
        if (elapsedTime >= targetDuration) {
            userMenu.processUserInput('Q');
        }
    }

    public void setLockedDoor() {
        ArrayList<RandomPlaces.Coordinate> wallCoordinates =
                RandomPlaces.placesMap
                    .entrySet()
                    .stream()
                    .filter(entry -> "endMiddle".equals(entry.getValue()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toCollection(ArrayList::new));
        Random r = new Random();
        RandomPlaces.Coordinate randomElement = wallCoordinates.get(r.nextInt(wallCoordinates.size()));
        lockedDoorX = randomElement.x;
        lockedDoorY = randomElement.y;
        world[lockedDoorX][lockedDoorY] = Tileset.LOCKED_DOOR;
        RandomPlaces.placesMap.put(new RandomPlaces.Coordinate(lockedDoorX, lockedDoorY), "lockedDoor");
    }

    public void setFlower() {
        int i = 1;
        while ( i <= level) {
            ArrayList<RandomPlaces.Coordinate> wallCoordinates =
                    RandomPlaces.placesMap
                            .entrySet()
                            .stream()
                            .filter(entry -> "roomFloor".equals(entry.getValue()))
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toCollection(ArrayList::new));
            Random r = new Random();
            RandomPlaces.Coordinate randomElement = wallCoordinates.get(r.nextInt(wallCoordinates.size()));
            world[randomElement.x][randomElement.y] = Tileset.FLOWER;
            RandomPlaces.placesMap.put(new RandomPlaces.Coordinate(randomElement.x, randomElement.y), "flower");
            i++;
        }
    }
}