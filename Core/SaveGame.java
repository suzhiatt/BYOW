package byow.Core;

import byow.TileEngine.TETile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveGame {

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

    public void loadSavedGame() {
        //need to figure out how to get engine to read this file, and then create a world based on it???

    }
}
