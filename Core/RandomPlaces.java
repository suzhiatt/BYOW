package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class RandomPlaces {
    private final long SEED = 100;
    private final Random r = new Random(SEED);
    int totalHeight;
    int totalWidth;
    int wide;
    int high;
    TETile[][] tiles;
    Random random = new Random();
    HashMap<Coordinate, String> hallwayMap = new HashMap<>();

private class Coordinate {
    private int x, y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int hashCode() {
            return Objects.hash(x, y);
        }
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            else if (!(obj instanceof Coordinate))
                return false;

            Coordinate that = (Coordinate) obj;
            return this.x == that.x && this.y == that.y;
        }
    }

    public RandomPlaces(int width, int height) {
        this.totalWidth = width;
        this.totalHeight = height;
        tiles = new TETile[width][height];
        for (int x = 0; x < width; x++) {
            //sets whole world to be tileset nothing, without this, it won't work.
            for (int y = 0; y < height; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
        //calls a random x, y to being the room. The bound is between 0 and 5 so it begins in the bottom left corner of world.
        int x = random.nextInt(5);
        int y= random.nextInt(5);
        generateRooms(x, y);
    }

    public void generateRooms(int x, int y) {
        if(isOccupied(x,y)) {
            return;
        }
        wide = random.nextInt(11-4+1)+4;
        high = random.nextInt(11-4+1)+4;
        for (int i = x; i <= (wide + x); i++) {
            for (int j = y; j <= (high + y); j++) {
               if ((isValidPosition(i, j))) {
                   //check to make sure it is ok to be called
                  if(isFloor(i,j)) {
                      //checks to make sure there isn't already a floor there. Why? If there is a floor, then skip it
                     continue;
                  }
                  if (i == x || i == (x + wide) || j == y || j == (y + high)) {
                      tiles[i][j] = Tileset.WALL;
                      }
                  else {
                            tiles[i][j] = Tileset.FLOOR;
                        }
                  if (isLastPosition(i, j)) {
                            tiles[i][j] = Tileset.WALL;
                        }
                    }
                  }
                }
        int max = x+wide-1;
        int min = x+1;
        int yMax = y+high-1;
        int yMin = y+1;
        //top hallway: y+high, max = x+wide, min = x
        addTopHallway((r.nextInt(max - min + 1) + min), y + high);
            //bottom hallway: x changes, y stays the same. newX between wide and x
        addBottomHallway((r.nextInt(max - min + 1) + min), y);
            //right wall: x+wide, newY is between y and (y+high)
        addRightHallway(max+1, (r.nextInt(yMax-yMin+1)+yMin));
            //left hallway: x stays the same, newY is between y to (y+high)
//        addLeftHallway(x, (r.nextInt((y+high)-y+1)+y));
    }

        private void addTopHallway (int x, int y) {
            int length = r.nextInt(10 + 1) + 3;
            if(!isValidPosition(x + 1, y + length) || !isValidPosition(x, y)) {
                return;
            }
            else {
                //replaces the wall tile with floor tile from generateRoom
                tiles[x][y] = Tileset.FLOOR;
                for (int i = x - 1; i < 2 + x; i++) {
                    for (int j = y; j < y + length; j++) {
                        if (!isOccupied(i, j)) {
                            Coordinate coordinate = new Coordinate(i, j);
                            if (hallwayMap.containsKey(coordinate)) {
                                if (hallwayMap.get(coordinate).equals("wall")) {
                                    //if we are not trying to put a wall, then do this:
                                    if (i != (x - 1) || i == (x + 1)) {
                                        tiles[i][j] = Tileset.LOCKED_DOOR;
                                        hallwayMap.put(coordinate, "floor");
                                    }
                                }
                            }
                            //if there isn't already a hallway there, put a floor or wall...
                            else {
                                if (i == (x - 1) || i == (x + 1)) {
                                    tiles[i][j] = Tileset.WALL;
                                    hallwayMap.put(coordinate, "wall");
                                } else {
                                    tiles[i][j] = Tileset.FLOOR;
                                    hallwayMap.put(coordinate, "floor");
                                }
                            }
                        }
                    }
                }
            }
                //use this one to add the last dot on the last position...
                if (y+length < (totalHeight - 4) && x - 2 >= 0) {
                    tiles[x][y] = Tileset.FLOOR;
                    Coordinate coordinate = new Coordinate(x, y);
                    hallwayMap.put(coordinate, "floor");
                    generateRooms(x - 2, y + length - 1);
                } else {
                    //replace the last dot with a pound sign.
                    tiles[x][y + length - 1] = Tileset.AVATAR;
                    Coordinate coordinate = new Coordinate(x, y + length - 1);
                    hallwayMap.put(coordinate, "wall");
                }
            }

        private void addBottomHallway ( int x, int y){
            //make it so x, y-1 to make the dot just below it appear. Along its sides need to be walls.
            int length = r.nextInt(10-3 + 1) + 3;
            if(!isValidPosition(x, y-length) || !isValidPosition(x,y)) {
                return;
            }
            boolean hallwayExists = false;
            for (int i = x - 1; i < x + 2; i++) {
                if (isValidPosition(i, y) && tiles[i][y]== Tileset.FLOOR) {
                    hallwayExists = true;
                    break;
                }
            }
            if(hallwayExists) {
                return;
            }
            if (isLastPosition(x, y)) {
                tiles[x][y] = Tileset.WALL;
            }
            else {
                tiles[x][y] = Tileset.FLOOR;
                for (int i = (x - 1); i < 2 + x; i++) {
                    for (int j = (y - 1); j>=y-length; j--) {
                        if ((isValidPosition(i, j))) {
//                            if(!isOccupied(i,j)) {
                                if (isLastPosition(i, j)) {
                                    tiles[i][j] = Tileset.WALL;
                                    Coordinate coordinate = new Coordinate(i,j);
                                    hallwayMap.put(coordinate, "wall");
                                } else {
                                    if (i == (x - 1) || i == (x + 1)) {
                                        tiles[i][j] = Tileset.WALL;
                                        Coordinate coordinate = new Coordinate(i,j);
                                        hallwayMap.put(coordinate, "wall");
                                    } else {
                                        tiles[i][j] = Tileset.FLOOR;
                                        Coordinate coordinate = new Coordinate(i,j);
                                        hallwayMap.put(coordinate, "floor");
                                    }
                                }
//                            }
                        }
                    }
                }
            }
            if(y-length>4 && x-1>0 && !isOccupied(x-1,y-length)) {
                    generateRooms(x - 1, y - length);
            }
            else{
                tiles[x][y - length] = Tileset.WALL;
                Coordinate coordinate = new Coordinate(x,y-length);
                hallwayMap.put(coordinate, "wall");
            }
        }

    private void addRightHallway (int x, int y){
        int length = r.nextInt(10-3 + 1) + 3;
        if(!isValidPosition(x+length, y+1) || !isValidPosition(x,y)) {
            return;
        }
        else {
            //replaces the wall tile with the floor tile from the generateRoom wall.
            tiles[x][y] = Tileset.FLOOR;
            for (int i = x+1; i < x+length; i++) {
                for (int j =y-1; j<y+2; j++) {
                    if(!isOccupied(i, j)) {
                        //if there isn't a room already there, do this...
                        Coordinate coordinate = new Coordinate(i, j);
                        //if this coordinate already has a hallway there, then check for which part of the hallway is there...
                        if (hallwayMap.containsKey(coordinate)) {
                            //if hM returns a value that is a wall...
                            if (hallwayMap.get(coordinate) == "wall") {
                                //if we are not trying to put in a wall, then do this:
                                if (j != (y - 1) || j != (y + 1)) {
                                    tiles[i][j] = Tileset.FLOOR;
                                    //change it on our hM
                                    hallwayMap.put(coordinate, "floor");
                                }
                            }
                        }
                        //if there isn't already a hallway there, put a floor or wall depending...
                        else {
                            if (j == (y - 1) || j == (y + 1)) {
                                tiles[i][j] = Tileset.WALL;
                                hallwayMap.put(coordinate, "wall");
                            } else {
                                tiles[i][j] = Tileset.FLOOR;
                                hallwayMap.put(coordinate, "floor");
                            }
                        }
                    }
                }
            }
        }
        //checks to make sure that the right Hallway isn't too high up and there isn't a room already there @ end of hallway.
        if(x+length<totalWidth && y+2<totalHeight-5 && !isOccupied(x+length, y+2)) {
            tiles[x+length][y] = Tileset.FLOOR;
            Coordinate coordinate = new Coordinate(x+length,y);
            hallwayMap.put(coordinate, "floor");
            generateRooms(x + length, y-1);
            }
        else{
            //if it is too high up, then it replaces the last
            tiles[x+length][y+1] = Tileset.FLOWER;
            Coordinate coordinate = new Coordinate(x+length,y+1);
            hallwayMap.put(coordinate, "wall");
            tiles[x+length][y] = Tileset.FLOWER;
            Coordinate coordinate1 = new Coordinate(x+length,y+1);
            hallwayMap.put(coordinate1, "wall");
            tiles[x+length][y-1] = Tileset.FLOWER;
            Coordinate coordinate2 = new Coordinate(x+length,y+1);
            hallwayMap.put(coordinate2, "wall");
        }
    }

//        private void addLeftHallway ( int x, int y){
//            tiles[x][y] = Tileset.FLOOR;
//            int width = r.nextInt(totalWidth - x);
//            for (int i = x; i > width; i--) {
//                for (int j = y - 1; j < 3; j++) {
//                    if ((isValidPosition(i, j))) {
//                        if (j == y - 1 || i == (j + 1)) {
//                            tiles[i][j] = Tileset.WALL;
//                        } else {
//                            tiles[i][j] = Tileset.FLOOR;
//                        }
//                    }
//                }
//            }
//        }

        private boolean isValidPosition ( int x, int y){
            if(x >= 0 && x <totalWidth && y >= 0 && y < totalHeight) {
                return true;
            }
            return false;
        }

        private boolean isOccupied (int x, int y) {
        if(tiles[x][y] == Tileset.NOTHING) {
            return false;
        }
        return true;
        }

        private boolean isFloor (int x, int y) {
        if(tiles[x][y] == Tileset.FLOOR) {
                return true;
            }
            return false;
        }
        private boolean isLastPosition ( int x, int y){
            if (x == 0 || x == totalWidth-1 || y == 0 || y == totalHeight-1) {
                return true;
            }
            return false;
        }

        public TETile[][] getTiles () {
            return tiles;
        }
    }

