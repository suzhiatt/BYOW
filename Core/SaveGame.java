package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class SaveGame {
    TETile[][] world;
    TETile tile;
    //constructor
    public SaveGame() {}

    public void saveGame(TETile[][] world) throws IOException {
        //makes a copy of the current world as saved in the text file
        File file = new File("savedGame.txt");
        FileWriter fr = new FileWriter(file, false);
        String writtenWorld = TETile.toString(world);
        fr.write(writtenWorld);
        fr.close();
        System.out.println("Just copied all of the current world in the file 'savedGame.txt'");
    }

    public TETile[][] loadSavedGame() {
        //initialize a TETile[][] with dimensions matching my world (WIDTH X HEIGHT)
        world = new TETile[50][50];
        int WIDTH = 50;
        int HEIGHT = 50;

        //create object of path class
        String filePath = "savedGame.txt";
        try {
            String fileContent = Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
            String[] lines = fileContent.isEmpty() ? new String[0] : fileContent.split("\\R");
            //go through each array of lines, parse through each character and place a tile at correct [x][y] position in to world.
            for (int i = 0; i < lines.length && i<HEIGHT; i++) {
                String line = lines[i];
                int y = (HEIGHT-1) - i;

                for (int j = 0; j<line.length() && j < WIDTH; j++) {
                    char c = line.charAt(j);
                    int x = j;

                    switch (c) {
                        case '@':
                            tile = Tileset.AVATAR;
                            break;
                        case '#':
                            tile = Tileset.WALL;
                            break;
                        case '.':
                            tile = Tileset.FLOOR;
                            break;
                        case ' ':
                            tile = Tileset.NOTHING;
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

