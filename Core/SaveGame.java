package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SaveGame {
    TETile[][] world;
    TETile tile;

    public SaveGame() {}

    public void saveGame(TETile[][] world, int level) throws IOException {
        //makes a copy of the current world as saved in the text file
        File file = new File("savedGame.txt");
        FileWriter fr = new FileWriter(file, false);
        String writtenWorld = TETile.toString(world);
        fr.write(writtenWorld);
        fr.write("\nlevel:" + level);
        fr.close();
        System.out.println("Just copied all of the current world in the file 'savedGame.txt'");
    }

    public TETile[][] loadSavedGame(Engine engine) {
        //initialize a TETile[][] with dimensions matching my world (WIDTH X HEIGHT)
        world = new TETile[50][50];
        int WIDTH = 50;
        int HEIGHT = 50;

        //create object of path class
        String filePath = "savedGame.txt";
        try {
            String fileContent = Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
            String[] lines = fileContent.split("\\R");
            //go through each array of lines, parse through each character and place a tile at correct [x][y] position in to world.
            for (int i = 0; i < lines.length && i<HEIGHT; i++) {
                String line = lines[i];
                if(lines[i].startsWith("level:")) {
                    String levelLine = lines[i];
                    String levelStr = levelLine.substring("LEVEL:".length());
                    engine.level = Integer.parseInt(levelStr);
                }
                int y = (HEIGHT-1) - i;

                for (int j = 0; j<line.length() && j < WIDTH; j++) {
                    char c = line.charAt(j);
                    int x = j;

                    switch (c) {
                        case '❀':
                            tile = Tileset.FLOWER;
                            RandomPlaces.placesMap.put(new RandomPlaces.Coordinate(x,y), "flower");
                            engine.avatarX = x;
                            engine.avatarY = y;
                            break;
                        case '@':
                            tile = Tileset.AVATAR;
                            RandomPlaces.placesMap.put(new RandomPlaces.Coordinate(x,y), "avatar");
                            engine.avatarX = x;
                            engine.avatarY = y;
                            break;
                        case '#':
                            tile = Tileset.WALL;
                            RandomPlaces.placesMap.put(new RandomPlaces.Coordinate(x,y), "wall");
                            break;
                        case '·':
                            tile = Tileset.FLOOR;
                            RandomPlaces.placesMap.put(new RandomPlaces.Coordinate(x,y), "floor");
                            break;
                        case ' ':
                            tile = Tileset.NOTHING;
                            break;
                        case '█':
                            tile = Tileset.LOCKED_DOOR;
                            RandomPlaces.placesMap.put(new RandomPlaces.Coordinate(x,y), "lockedDoor");
                            break;
                    }
                    world[x][y]= tile;
                }
            }
            String writtenWorld = TETile.toString(world);
            System.out.println(writtenWorld);
            return world;
        }
            catch (IOException e) {
            e.printStackTrace();
            return null;
            }
        }

    }

