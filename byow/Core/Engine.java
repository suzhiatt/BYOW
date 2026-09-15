package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;
import java.awt.*;
import java.util.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Engine {
    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;
    private static final int HUD_ROWS = 4;
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
    private long lastHudSecond = -1;
    TETile[][] world;
    UserMovement userMove;
    UserMenu userMenu;
    public static boolean loadStartTime = false;
    Random r = new Random();
    int entityX;
    int entityY;
    Entity entity;

    // creates a world background when constructor is called
    public Engine() {
        userMenu = new UserMenu(this);
    }

    public void createWorld(long seed) {
        RandomPlaces generate = new RandomPlaces(WIDTH, HEIGHT, seed);
        world = generate.getTiles();
        ter = new TERenderer();
        // Extra rows above the world so HUD text is not drawn on top of tiles (avoids flicker).
        ter.initialize(WIDTH, HEIGHT + HUD_ROWS);
        avatarX = (generate.getFirstX() + 1);
        avatarY = (generate.getFirstY() + 1);
        world[avatarX][avatarY] = Tileset.AVATAR;
        setLockedDoor();
        setFlower();
        setEntity();
        ter.renderFrame(world);
        startTime();
        userMove = new UserMovement(avatarX, avatarY, world, this);
        entity = new Entity(entityX, entityY, world, userMove);
        entity.storePath();
        drawHUD();
        beginGamePlay();
    }

    public void beginGamePlay() {
        while(true) {
        checkTime();
        char input = getInput();
        if (input == ':') {
            char next = getInput();
            if (next == 'Q')  {
                SaveGame save = new SaveGame();
                try {
                    save.saveGame(world);
                    userMenu.quitEarly=true;
                    userMenu.processUserInput('Q');
                    return;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (input == 'R') {
            if (entity.highlightPath) {
                entity.highlightPath();
                entity.highlightPath = false;
            } else {
                entity.highlightPath();
                entity.highlightPath = true;
            }
            ter.renderFrame(world);
            drawHUD();
        }
        else {
                userMove.processMove(input);
                entity.pathMovement();
                ter.renderFrame(world);
                drawHUD();
        }
        }
    }

    public void loadWorld() {
        ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT + HUD_ROWS);
        ter.renderFrame(world);
        startTime();
        userMove = new UserMovement(avatarX, avatarY, world, this);
        entity = new Entity(entityX, entityY, world, userMove);
        entity.storePath();
        drawHUD();
        beginGamePlay();
    }

    public char getInput() {
            while (true) {
                checkTime();
                if (StdDraw.hasNextKeyTyped()) {
                    return Character.toUpperCase(StdDraw.nextKeyTyped());
                }
                // Update the timer display at most once per second; do not redraw on every poll.
                if (timeRemaining != lastHudSecond) {
                    refreshHUD();
                }
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
        ArrayList<RandomPlaces.Coordinate> wallCoordinates = filterMap("endMiddle");
        Random r = new Random();
        RandomPlaces.Coordinate randomElement = wallCoordinates.get(r.nextInt(wallCoordinates.size()));
        lockedDoorX = randomElement.x;
        lockedDoorY = randomElement.y;
        world[lockedDoorX][lockedDoorY] = Tileset.LOCKED_DOOR;
        RandomPlaces.placesMap.put(new RandomPlaces.Coordinate(lockedDoorX, lockedDoorY), "lockedDoor");
    }

    public void setFlower() {
        ArrayList<RandomPlaces.Coordinate> roomFloorCoordinates = filterMap("roomFloor");
        int i = 1;
        while (i <= level) {
            Random r = new Random();
            RandomPlaces.Coordinate randomElement = roomFloorCoordinates.get(r.nextInt(roomFloorCoordinates.size()));
            world[randomElement.x][randomElement.y] = Tileset.FLOWER;
            RandomPlaces.placesMap.put(new RandomPlaces.Coordinate(randomElement.x, randomElement.y), "flower");
            i++;
        }
        flowers = level;
    }

    public void setEntity() {
        ArrayList<RandomPlaces.Coordinate> floorCoordinates = filterMap("floor");
        int lastElement = floorCoordinates.size()-1;
        RandomPlaces.Coordinate set = floorCoordinates.get(lastElement);
        entityX = set.x;
        entityY = set.y;
        RandomPlaces.placesMap.put(new RandomPlaces.Coordinate(entityX, entityY), "entity");
        world[entityX][entityY] = Tileset.WATER;
    }

    public ArrayList<RandomPlaces.Coordinate> filterMap(String string) {
        return RandomPlaces.placesMap
                        .entrySet()
                        .stream()
                        .filter(entry -> string.equals(entry.getValue()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toCollection(ArrayList::new));
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

    public void userEaten() {
        ter.renderFrame(world);
        drawHUD();
        StdDraw.pause(1000);
        userMenu.eatenByEntity = true;
        userMenu.processUserInput('Q');
    }

    public void drawHUD() {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 16));
        // Draw in the empty margin above the world (y >= HEIGHT), not over tiles.
        StdDraw.textLeft(1, HEIGHT + 3, "Level: " + level);
        StdDraw.textLeft(1, HEIGHT + 2, "Flowers: " + flowers);
        StdDraw.textLeft(1, HEIGHT + 1, "Time: " + timeRemaining);
        StdDraw.textRight(WIDTH - 1, HEIGHT + 3, "Press 'R' to toggle entity path");
        StdDraw.textRight(WIDTH - 1, HEIGHT + 1, "Quit anytime with ':Q'");
        lastHudSecond = timeRemaining;
        StdDraw.show();
    }

    public void refreshHUD() {
     //black out only the HUD band: Height+1-3   
     StdDraw.setPenColor(Color.BLACK);
     StdDraw.filledRectangle(WIDTH/2.0, HEIGHT + HUD_ROWS/2.0, WIDTH/2.0, HUD_ROWS/2.0);
     StdDraw.setPenColor(Color.WHITE);
     StdDraw.setFont(new Font("Monaco", Font.BOLD, 16));
     StdDraw.textLeft(1, HEIGHT + 3, "Level: " + level);
     StdDraw.textLeft(1, HEIGHT + 2, "Flowers: " + flowers);
     StdDraw.textLeft(1, HEIGHT + 1, "Time: " + timeRemaining);
     StdDraw.textRight(WIDTH - 1, HEIGHT + 3, "Press 'R' to toggle entity path");
     StdDraw.textRight(WIDTH - 1, HEIGHT + 1, "Quit anytime with ':Q'");
     lastHudSecond = timeRemaining;
     StdDraw.show();
    }
}