package byow.TileEngine;

import java.awt.Color;


public class Tileset {
    public static final TETile ELEPHANT = new TETile('@', Color.white, Color.black, "you", "byow/images/elephant.png");
    public static final TETile COW = new TETile('@', Color.white, Color.black, "you", "byow/images/cow.png");
    public static final TETile GUY = new TETile('@', Color.white, Color.black, "you", "byow/images/guy.png");
    public static final TETile KANGAROO = new TETile('@', Color.white, Color.black, "you", "byow/images/kangaroo.png");
    public static final TETile RAT = new TETile('@', Color.white, Color.black, "you", "byow/images/rat.png");
    public static final TETile WALRUS = new TETile('@', Color.white, Color.black, "you", "byow/images/walrus.png");
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall", "byow/images/wall.png");
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black,
            "floor", "byow/images/floor.png");
    public static final TETile FLOORS = new TETile('.', Color.red, Color.black, "floor");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water", "byow/images/demon.png");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.red, Color.black, "sand", "byow/images/sand.png");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");
}


