package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.HashMap;
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
    HashMap<Integer, String> hallwayMap = new HashMap<>();

    public RandomPlaces(int width, int height) {
        this.totalWidth = width;
        this.totalHeight = height;
        tiles = new TETile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
        int x = random.nextInt(totalWidth/5);
        int y= random.nextInt(totalHeight/5);
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
                  if(isFloor(i,j)) {
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

        private void addTopHallway (int x, int y){
            int length = r.nextInt(10-3 + 1) + 3;
            if(!isValidPosition(x, y+length) || !isValidPosition(x,y)) {
                return;
            }
            if (isLastPosition(x, y)) {
                tiles[x][y] = Tileset.WALL;
            }
            else {
                tiles[x][y] = Tileset.FLOOR;
                for (int i = x - 1; i < 2 + x; i++) {
                    for (int j = y; j < y + length; j++) {
                        if (isValidPosition(i, j)) {
//                            if(!isOccupied(i, j)) {
                                if (isLastPosition(i, j)) {
                                    tiles[i][j] = Tileset.WALL;
                                } else {
                                    if (i == (x - 1) || i == (x + 1)) {
                                        tiles[i][j] = Tileset.WALL;
                                    } else {
                                        tiles[i][j] = Tileset.FLOOR;
                                    }

                                }
                            }
                        }
                    }
//                }
            }
            //use this one to add the last dot on the last position...
            if(y+length<totalHeight-4 && x-2>=0) {
                tiles[x][y]=Tileset.FLOOR;
                generateRooms(x - 2, y + length - 1);
            }
            else {
                //replace the last dot with a pound sign.
                tiles[x][y+length-1]=Tileset.WALL;
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
                            if(!isOccupied(i,j)) {
                                if (isLastPosition(i, j)) {
                                    tiles[i][j] = Tileset.WALL;
                                } else {
                                    if (i == (x - 1) || i == (x + 1)) {
                                        tiles[i][j] = Tileset.WALL;
                                    } else {
                                        tiles[i][j] = Tileset.FLOOR;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(y-length>4 && x-1>0 && !isOccupied(x-1,y-length)) {
                    generateRooms(x - 1, y - length);
            }
            else{
                tiles[x][y - length] = Tileset.WALL;
            }
        }

    private void addRightHallway ( int x, int y){
        int length = r.nextInt(10-3 + 1) + 3;
        if(!isValidPosition(x+length, y) || !isValidPosition(x,y)) {
            return;
        }
        boolean hallwayExists = false;
        for (int i = y - 1; i < y + 2; i++) {
            if (isValidPosition(x, i) && tiles[x][i]== Tileset.FLOOR) {
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
            for (int i = (x + 1); i < x+length; i++) {
                for (int j =y - 1; j<y+2; j++) {
                    if ((isValidPosition(i, j))) {
                        if(!isOccupied(i,j)) {
                            if (isLastPosition(i, j)) {
                                tiles[i][j] = Tileset.WALL;
                            } else {
                                if (j==(y- 1) || j== (y + 1)) {
                                    tiles[i][j] = Tileset.WALL;
                                } else {
                                    tiles[i][j] = Tileset.FLOOR;
                                }
                            }
                        }
                    }
                }
            }
        }
        if(x+length<totalWidth && y+2<totalHeight-5 && !isOccupied(x+length, y+2)) {
            tiles[x+length][y] = Tileset.FLOOR;
                generateRooms(x + length, y-1);
            }
        else{
            tiles[x+length][y+1] = Tileset.WALL;
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

