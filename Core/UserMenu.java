package byow.Core;

import edu.princeton.cs.algs4.StdDraw;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class UserMenu {

    private boolean welcomeFrame;
    private boolean loadSavedGame = false;
    private boolean gameOver = false;
    private boolean newGame = false;
    private boolean newLevel = false;
    private boolean receiveSeed = false;
    private int menuWidth = 60;
    private int menuHeight = 30;
    public long userSeed;
    private Engine engine;

    public UserMenu() {
        welcomeFrame=true;
        StdDraw.setCanvasSize(this.menuWidth * 16, this.menuHeight * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.menuWidth);
        StdDraw.setYscale(0, this.menuHeight);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public void drawFrame(String string) {
        //When we want to change what is displayed on the screen, we have to clear the entire screen and redraw everything.
        StdDraw.clear(Color.BLACK);
        //draws everything necessary for the next frame
        StdDraw.setPenColor(Color.PINK);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(this.menuWidth / 2, this.menuHeight / 1.5, string);
        StdDraw.pause(20);
        if(welcomeFrame) {
            StdDraw.setPenColor(Color.PINK);
            Font fontSmall = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(fontSmall);
            StdDraw.textLeft(menuWidth/2.25,this.menuHeight-15, "(N)ew Game");
            StdDraw.textLeft(menuWidth/2.25,this.menuHeight-17, "(L)oad Game");
            StdDraw.textLeft(menuWidth/2.25,this.menuHeight-19, "(Q)uit Game");
            welcomeFrame = false;
        }
            else if (gameOver){
            StdDraw.setPenColor(Color.PINK);
            Font fontSmall = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(fontSmall);
            StdDraw.textLeft(this.menuWidth / 2.6, this.menuHeight-15, "You reached level " + Engine.level);
            StdDraw.textLeft(this.menuWidth / 2.6, this.menuHeight-17, "Hope to see you again!");
            StdDraw.pause(100);
            gameOver = false;
        }
            else if (receiveSeed) {
        }
            else if (loadSavedGame){
            StdDraw.setPenColor(Color.PINK);
            Font fontSmall = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(fontSmall);
            StdDraw.textLeft(this.menuWidth / 3, this.menuHeight-15, "Get ready for some fun...");
            StdDraw.pause(100);
            loadSavedGame = false;
        }
            else if (newGame){
            StdDraw.setPenColor(Color.PINK);
            Font fontSmall = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(fontSmall);
            StdDraw.textLeft(this.menuWidth / 3, this.menuHeight-15, "Your avatar is '@'");
            StdDraw.textLeft(this.menuWidth / 3, this.menuHeight-17, "Gobble up the flowers!");
            StdDraw.textLeft(this.menuWidth / 3, this.menuHeight-19, "Run through locked Door before time runs out!");
            StdDraw.textLeft(this.menuWidth / 3, this.menuHeight-21, "Good Luck!");
            StdDraw.pause(100);
            newGame = false;
        }
            else if (newLevel) {
            StdDraw.setPenColor(Color.PINK);
            Font fontSmall = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(fontSmall);
            StdDraw.textLeft(this.menuWidth / 3, this.menuHeight-15, "Congrats on passing the level!");
            StdDraw.textLeft(this.menuWidth / 3, this.menuHeight-15, "You're about to begin level " + Engine.level);
                newLevel = false;
        }
        //shows the canvas
        StdDraw.show();
        StdDraw.clear(Color.BLACK);
    }



    //wait for user input, this is called by the engine class
    public char solicitUserInput() {
        //this loop keeps checking for input. Returns immediately when key is pressed. Continues waiting if no key pressed.
        while(true) {
            if(StdDraw.hasNextKeyTyped()) {
                return Character.toUpperCase(StdDraw.nextKeyTyped());
            }
            //the program will make it check indefinitely, so this causes CPU to reduce usage while still responsive.
            StdDraw.pause(20);
        }
    }


    public void receiveUserSeed() {
        StringBuilder sB = new StringBuilder();
        while (true) { //this needs to be said so that it will keep checking continuously
            //my previous error is that it only checked once, and then stopped.
            if(StdDraw.hasNextKeyTyped()) { //checks if key is typed
                char curr = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (Character.isDigit(curr)) {
                    sB.append(curr);
                    //UI should show the seed value that the user has entered so far.
                    drawFrame(String.valueOf(sB));
                } else {
                    if (curr == 'Q') {
                        processUserInput('Q');
                    }
                    else if (curr == 'S') {
                                userSeed = Long.parseLong(String.valueOf(sB));
                            try {
                                receiveSeed=false;
                                beginNewGame();
                            } catch (IOException e) {
                                System.out.println("Error creating new game: " + e.getMessage());
                            }
                        }
                    else {
                            System.out.println("Numbers only, please!");
                        }
                    }
                }
            StdDraw.pause(30);
        }
    }

    public void processUserInput(char choice) {
        if (engine == null) {
            engine = new Engine();
        }
        if (choice=='Q') {
            //end game
            welcomeFrame = false;
            gameOver = true;
            drawFrame("Game Over!");
            StdDraw.pause(2000);
            System.exit(0);
        } else if (choice=='N') {
            receiveSeed = true;
            drawFrame("Enter a number, press 'S' when finished");
            receiveUserSeed();
        } else if (choice=='L') {
            loadSavedGame=true;
            drawFrame("Loading Saved Game!");
            StdDraw.pause(2000);
            SaveGame save = new SaveGame();
            engine.world = save.loadSavedGame(engine);
            engine.loadWorld();
        } else {
            System.out.println("Please enter one of the three choices again...");
        }
    }

    public void beginNewGame() throws IOException {
        //is called when the user enters 'S', begins the program using userSeed;
        File file = new File("newGame.txt");
        FileWriter fr = new FileWriter(file, false);
        fr.write("New game userSeed is: " + userSeed);
        fr.close();
        System.out.println("New game userSeed is: " + userSeed);
        engine.createWorld(userSeed);
    }
    }

