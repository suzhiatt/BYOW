package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
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
    public static int flowers = 1;
    long targetDuration = 20_000_000_000L;
    public static long timeRemaining;
    TETile[][] world;
    UserMovement userMove;
    UserMenu userMenu;
    public static boolean loadStartTime = false;
    Random r = new Random();

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
        System.out.println("Engine.level set to: " + Engine.level);
        System.out.println("Flowers are: " + flowers + " and level is: " + level);
        setFlower();
        ter.renderFrame(world);
        startTime();
        userMove = new UserMovement(avatarX, avatarY, world, this);
        drawHUD();
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
                    userMenu.quitEarly=true;
                    userMenu.processUserInput('Q');

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            userMove.processMove(input);
            ter.renderFrame(world);
            drawHUD();
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
            checkTime();
            ter.renderFrame(world);  // Re-render
            drawHUD();
            if (StdDraw.hasNextKeyTyped()) {
                return Character.toUpperCase(StdDraw.nextKeyTyped());
            }
            //the program will make it check indefinitely, so this causes CPU to reduce usage while still responsive.
            StdDraw.pause(20);
        }
    }

    public void interactWithKeyboard() {
        userMenu.welcomeFrame=true;
        userMenu.drawFrame("Welcome to Susie's World");
        //receive input from user and store as "choice"
        char choice = userMenu.solicitUserInput();
        //call another method to process user's choice
        userMenu.processUserInput(choice);
    }

    public void startTime() {
        //start nanoTime
        if(loadStartTime) {
            loadStartTime = false;
        }
        else {
            startTime = System.nanoTime();
        }
    }

    public void checkTime() {
        long currentTime = System.nanoTime();
        long elapsedTime = currentTime - startTime;
        targetDuration = getTargetDuration();
        timeRemaining = (int)Math.max(0, ((targetDuration - elapsedTime)/1_000_000_000L));
        if (elapsedTime >= targetDuration) {
            userMenu.outOfTime=true;
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
        flowers=level;
    }

    public void nextLevel() {
        long seed = r.nextLong();
        level++;
        printSeed(seed);
        userMenu.newLevel=true;
        userMenu.drawFrame("");
        StdDraw.pause(3000);
        createWorld(seed);
    }

    public void loadSavedGame() {
        SaveGame saveGame = new SaveGame();
        world=saveGame.loadSavedGame(this);
        long targetDuration = getTargetDuration();
        long elapsedTime = targetDuration - (timeRemaining * 1_000_000_000L);
        startTime = System.nanoTime()-elapsedTime;
        loadWorld();
    }

    public long getTargetDuration() {
        return 25_000_000_000L + (level*(3_000_000_000L));
    }

    public void printSeed(long seed) {
        System.out.println("For level: " + level + " This is the seed: " + seed);
    }

    public void drawHUD() {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 16));
        StdDraw.textLeft(1, HEIGHT - 1, "Level: " + level);
        StdDraw.textLeft(1, HEIGHT - 2, "Flowers: " + flowers);
        StdDraw.textLeft(1, HEIGHT-3,"Time: " + timeRemaining);
        StdDraw.show();  // Make sure to show the updates
    }
}