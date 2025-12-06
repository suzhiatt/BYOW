package byow.Core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.Random;

public class UserMenu {

    private boolean gameOver;
    private int menuWidth = 60;
    private int menuHeight = 30;
    private Random r;

    public UserMenu() {
        StdDraw.setCanvasSize(this.menuWidth * 16, this.menuHeight * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.menuWidth);
        StdDraw.setYscale(0, this.menuHeight);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public void drawFrame() {
        //When we want to change what is displayed on the screen, we have to clear the entire screen and redraw everything.
        StdDraw.clear(Color.BLACK);
        //draws everything necessary for the next frame
        StdDraw.setPenColor(Color.PINK);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(this.menuWidth / 2, this.menuHeight / 1.5, "Welcome to Susie's World");
        StdDraw.pause(20);
        if(!gameOver) {
            StdDraw.setPenColor(Color.PINK);
            Font fontSmall = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(fontSmall);
            StdDraw.textLeft(menuWidth/2.25,this.menuHeight-15, "(N)ew Game");
            StdDraw.textLeft(menuWidth/2.25,this.menuHeight-17, "(L)oad Game");
            StdDraw.textLeft(menuWidth/2.25,this.menuHeight-19, "(Q)uit Game");
        }
            else {
                StdDraw.clear(Color.BLACK);
                StdDraw.textLeft(this.menuWidth / 2.5, this.menuHeight / 1.5, "Game Over!!!");
                StdDraw.pause(1000);
        }
        //shows the canvas
        StdDraw.show();
        StdDraw.clear(Color.BLACK);
    }

    //wait for user input
    public char solicitUserInput(int n) {
        //this loop keeps checking for input. Returns immediately when key is pressed. Continues waiting if no key pressed.
        while(true) {
            if(StdDraw.hasNextKeyTyped()) {
                return Character.toUpperCase(StdDraw.nextKeyTyped());
            }
            //the program will make it check indefinitely, so this causes CPU to reduce usage while still responsive.
            StdDraw.pause(20);
        }
    }

    public void processUserInput(char choice) {
        if (choice=='Q') {
            //end game
            gameOver = true;
            drawFrame();
            StdDraw.pause(2000);
            System.exit(0);
        } else if (choice=='N') {
            //user is prompted to enter a "random seed" which is a long value of their choosing
            //UI should show the seed value that the user has entered so far.
            //After user has pressed the final number in their seed, they should press S to tell the system that they've
            //entered the entire seed that they want.
            //our world generator should handle any see up to 19 numbers long.
        } else if (choice=='L') {
            //need to figure out how to load a saved world. Thinking it has something to do with the username saved
            //alongside seed number of previously generated world.
        } else {
            System.out.println("Please enter one of the three choices again...");
        }
    }
}
