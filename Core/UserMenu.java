package byow.Core;

import edu.princeton.cs.algs4.StdDraw;
import java.awt.*;

public class UserMenu {

    public boolean welcomeFrame;
    private boolean loadSavedGame = false;
    private boolean gameOver = false;
    private boolean newGame = false;
    public boolean newLevel = false;
    public boolean quitEarly = false;
    public boolean outOfTime = false;
    public boolean eatenByEntity = false;
    private int menuWidth = 60;
    private int menuHeight = 30;
    public long userSeed;
    private Engine engine;

    public UserMenu() {
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
        else if (quitEarly) {
            StdDraw.setPenColor(Color.PINK);
            Font fontSmall = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(fontSmall);
            StdDraw.textLeft(this.menuWidth / 3, this.menuHeight-15, "You reached level " + Engine.level);
            StdDraw.textLeft(this.menuWidth / 3, this.menuHeight-17, "Press Load next time you get on");
            StdDraw.textLeft(this.menuWidth / 3, this.menuHeight-19, "To continue where you left off");
            StdDraw.pause(100);
            quitEarly = false;
        }
        else if(outOfTime) {
            StdDraw.setPenColor(Color.PINK);
            Font fontSmall = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(fontSmall);
            StdDraw.textLeft(this.menuWidth / 2.6, this.menuHeight-15, "Gotta be faster next time!");
            StdDraw.textLeft(this.menuWidth / 2.6, this.menuHeight-17, "Hope to see you again!");
            StdDraw.pause(100);
            outOfTime=false;
        }
        else if (eatenByEntity) {
            StdDraw.setPenColor(Color.PINK);
            Font fontSmall = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(fontSmall);
            StdDraw.textLeft(this.menuWidth / 2.6, this.menuHeight-15, "Ouch! No one likes being eaten!");
            StdDraw.textLeft(this.menuWidth / 2.6, this.menuHeight-17, "Hope to see you again!");
            StdDraw.pause(100);
            eatenByEntity = false;
        }
            else if (gameOver){
            StdDraw.setPenColor(Color.PINK);
            Font fontSmall = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(fontSmall);
            StdDraw.textLeft(this.menuWidth / 2.6, this.menuHeight-15, "We were about to have fun!");
            StdDraw.textLeft(this.menuWidth / 2.6, this.menuHeight-17, "Hope to see you again!");
            StdDraw.pause(100);
            gameOver = false;
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
            StdDraw.textLeft(this.menuWidth / 3, this.menuHeight-17, "Gobble up the flowers to unlock door!");
            StdDraw.textLeft(this.menuWidth / 3, this.menuHeight-19, "Run through the Door before time runs out!");
            StdDraw.textLeft(this.menuWidth / 3, this.menuHeight-21, "Good Luck!");
            StdDraw.pause(100);
            newGame = false;
        }
            else if (newLevel) {
            StdDraw.setPenColor(Color.PINK);
            Font fontSmall = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(fontSmall);
            StdDraw.textLeft(this.menuWidth / 3.5, this.menuHeight-1, "Congrats on passing the level!");
            StdDraw.textLeft(this.menuWidth / 3.5, this.menuHeight-3, "You're about to begin level " + Engine.level);
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
                                newGame=true;
                                drawFrame("Use 'W', 'A', 'S', 'D' to move");
                                StdDraw.pause(3000);
                                newGame=false;
                                engine.createWorld(userSeed);
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
            if(quitEarly) {
                drawFrame("Leaving so soon? :(");
                StdDraw.pause(2000);
                System.exit(0);
            }
            //end game
            gameOver = true;
            drawFrame("Game Over!");
            StdDraw.pause(2000);
            System.exit(0);
        } else if (choice=='N') {
            drawFrame("Enter a number, press 'S' when finished");
            receiveUserSeed();
        } else if (choice=='L') {
            loadSavedGame=true;
            drawFrame("Loading Saved Game!");
            StdDraw.pause(2000);
            engine.loadSavedGame();
        } else {
            System.out.println("Please enter one of the three choices again...");
            welcomeFrame = true;
            engine.interactWithKeyboard();
        }
    }
}

