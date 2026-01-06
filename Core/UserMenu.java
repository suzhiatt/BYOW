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
    private int menuWidth = 60;
    private int menuHeight = 30;
    public long userSeed;

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
//                StdDraw.clear(Color.BLACK);
                StdDraw.textLeft(this.menuWidth / 3, this.menuHeight-15, "Hope to see you again!");
                StdDraw.pause(100);
        }
            else if (loadSavedGame){

        }
            else {
                // new menu

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
                    } else if (curr == 'S') {
                        if (sB.length() == 1) {
                            //needs to generate a random string of numbers, save it as a string, then call this.
                            String string = "";
                            loadSavedGame(string);
                        }
                        else {
                                userSeed = Long.parseLong(String.valueOf(sB));
                            try {
                                beginNewGame();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        } else {
                            System.out.println("Numbers only, please!");
                        }
                    }
                }
            StdDraw.pause(30);
        }
    }

    public void processUserInput(char choice) {
        if (choice=='Q') {
            //end game
            gameOver = true;
            welcomeFrame = false;
            drawFrame("Game Over!");
            StdDraw.pause(2000);
            System.exit(0);
        } else if (choice=='N') {
            newGame = true;
            //user is prompted to enter a "random seed" which is a long value of their choosing
            drawFrame("Enter a number, press 'S' when finished");
            receiveUserSeed();
        } else if (choice=='L') {
//            loadSavedGame = true;
            SaveGame save = new SaveGame();
            Engine engine = new Engine();
            engine.world=save.loadSavedGame(engine);
            engine.loadWorld();

        } else {
            System.out.println("Please enter one of the three choices again...");
        }
    }

    public void beginNewGame() throws IOException {
        //is called when the user enters 'S', begins the program using userSeed;
        //figure out how to be a new game with the userSeed
        File file = new File("newGame.txt");
        FileWriter fr = new FileWriter(file, false);
        fr.write("New game userSeed is: " + userSeed);
        fr.close();
        System.out.println("New game userSeed is: " + userSeed);
        Engine engine = new Engine();
        engine.createWorld(userSeed);
    }

    public void loadSavedGame(String string) {}
   
}
