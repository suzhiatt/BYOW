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
            int hash = Objects.hash(x, y);
            return hash;
        }

        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            else if (!(obj instanceof Coordinate))
                return false;
            Coordinate that = (Coordinate) obj;
            boolean result = this.x == that.x && this.y == that.y;
            return result;
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
        int y = random.nextInt(5);
        generateRooms(x, y);
    }

    public void generateRooms(int x, int y) {
        if (isOccupied(x, y)) {
            return;
        }
        wide = random.nextInt(11 - 4 + 1) + 4;
        high = random.nextInt(11 - 4 + 1) + 4;
        for (int i = x; i <= (wide + x); i++) {
            for (int j = y; j <= (high + y); j++) {
                //check to make sure it is ok to be called
                if ((isValidPosition(i, j))) {
                    //check if a wall needs to be put down...
                    if (i == x || i == (x + wide) || j == y || j == (y + high)) {
                        Coordinate coordinate = new Coordinate(i,j);
                        //check if there is already a floor tile there...
                        if(hallwayMap.containsKey(coordinate)) {
                                if(hallwayMap.get(coordinate).equals("floor") || hallwayMap.get(coordinate).equals("endMiddle")) {
                                    tiles[i][j]=Tileset.FLOOR;
                            }
                        }
                        else {
                            tiles[i][j] = Tileset.WALL;
                        }
                    }
                    else {
                        tiles[i][j] = Tileset.AVATAR;
                    }
                    if (isLastPosition(i, j)) {
                        tiles[i][j] = Tileset.UNLOCKED_DOOR;
                    }
                }
            }
        }
        int max = x + wide - 1;
        int min = x + 1;
        int yMax = y + high - 1;
        int yMin = y + 1;
        //top hallway: y+high, max = x+wide, min = x
        addTopHallway((r.nextInt(max - min + 1) + min), y+high);
        //bottom hallway: x changes, y stays the same. newX between wide and x
        //addBottomHallway((r.nextInt(max - min + 1) + min), y);
        //right wall: x+wide, newY is between y and (y+high)
        addRightHallway(max + 1, (r.nextInt(yMax - yMin + 1) + yMin));
        //left hallway: x stays the same, newY is between y to (y+high)
//        addLeftHallway(x, (r.nextInt((y+high)-y+1)+y));
    }

    private void addTopHallway(int x, int y) {
        //calculate length of hallway
        int length = r.nextInt(10 + 1) + 3;
        //if it is invalid, then return
        if (isValidPosition(x + 1, y + length) && isValidPosition(x, y)) {
            //replaces the wall tile with floor tile from generateRoom
            tiles[x][y] = Tileset.FLOOR;
            Coordinate coordinate = new Coordinate(x, y);
            hallwayMap.put(coordinate, "floor");
            for (int i = x - 1; i < 2 + x; i++) {
                for (int j = y; j < y + length; j++) {
                    if (!isOccupied(i, j)) {
                        coordinate = new Coordinate(i, j);
                        if (i == (x - 1) || i == (x + 1)) {
                            boolean contains = hallwayMap.containsKey(coordinate);
                            if (!contains) {
                                tiles[i][j] = Tileset.WALL;
                                hallwayMap.put(coordinate, "wall");
                            }
                        } else {
                            tiles[i][j] = Tileset.FLOOR;
                            hallwayMap.put(coordinate, "floor");
                        }
                        //if there isn't already a hallway there, put a floor or wall...
                    }
                }
            }
            //use this one to add the last dot on the last position...
            if (y + length < (totalHeight - 4) && x - 2 >= 0 && !isOccupied(y + length, x - 2)) {
                generateRooms(x - 2, y + length - 1);
            } else {
                tiles[x][y + length - 1] = Tileset.MOUNTAIN;
                coordinate = new Coordinate(x, y + length - 1);
                hallwayMap.put(coordinate, "endMiddle");
            }
        }
    }


    // private void addBottomHallway(int x, int y) {
    //     int length = r.nextInt(10 - 3 + 1) + 3;
    //     if (isValidPosition(x, y - length) && isValidPosition(x, y)) {
    //         tiles[x][y] = Tileset.FLOOR;
    //         for (int i = (x - 1); i < 2 + x; i++) {
    //             for (int j = (y - 1); j >= y - length; j--) {
    //                 if (!isOccupied(i, j)) {
    //                     Coordinate coordinate = new Coordinate(i, j);
    //                     if (hallwayMap.containsKey(coordinate)) {
    //                         if (hallwayMap.get(coordinate).equals("wall")) {
    //                             if (i != (x - 1) || i != (x + 1)) {
    //                                 tiles[i][j] = Tileset.FLOOR;
    //                                 hallwayMap.put(coordinate, "floor");
    //                             }
    //                         } else {
    //                             if (i == (x - 1) || i == (x + 1)) {
    //                                 tiles[i][j] = Tileset.WALL;
    //                                 hallwayMap.put(coordinate, "wall");
    //                             } else {
    //                                 tiles[i][j] = Tileset.FLOOR;
    //                                 hallwayMap.put(coordinate, "floor");
    //                             }
    //                         }
    //                     }
    //                 }
    //             }
    //         }
    //         if (y - length > 4 && x - 1 > 0 && !isOccupied(x - 1, y - length)) {
    //             tiles[x][y - length] = Tileset.AVATAR;
    //             Coordinate coordinate = new Coordinate(x, y - length);
    //             hallwayMap.put(coordinate, "floor");
    //             generateRooms(x - 1, y - length);
    //         } else {
    //             tiles[x - 1][y - length + 1] = Tileset.UNLOCKED_DOOR;
    //             Coordinate coordinate = new Coordinate(x - 1, y - length + 1);
    //             hallwayMap.put(coordinate, "wall");

    //             tiles[x][y - length + 1] = Tileset.UNLOCKED_DOOR;
    //             Coordinate coordinate1 = new Coordinate(x, y - length + 1);
    //             hallwayMap.put(coordinate1, "wall");

    //             tiles[x + 1][y - length + 1] = Tileset.UNLOCKED_DOOR;
    //             Coordinate coordinate2 = new Coordinate(x + 1, y - length + 1);
    //             hallwayMap.put(coordinate2, "wall");
    //         }
    //     }
    // }

    private void addRightHallway(int x, int y) {
        System.out.println("Starting Right Hallway at: " + x + "," + y);
        System.out.println("HallwayMap contains: " + hallwayMap.size() + " entries");
        
        // Let's print the actual contents of hallwayMap
        for (Coordinate c : hallwayMap.keySet()) {
            System.out.println("Map contains: " + c.x + "," + c.y + " = " + hallwayMap.get(c));
        }
        
        int length = r.nextInt(10 - 3 + 1) + 3;
        if (isValidPosition(x + length - 1, y + 1) && isValidPosition(x, y)) {
            // Start with the entrance
            tiles[x][y] = Tileset.FLOOR;
            hallwayMap.put(new Coordinate(x, y), "floor");
            
            // Create the hallway
            for (int i = x + 1; i < x + length; i++) {
                for (int j = y - 1; j < y + 2; j++) {
                    Coordinate currentCoord = new Coordinate(i, j);
                    if (j == (y - 1) || j == (y + 1)) {  // Wall positions
                        System.out.println("Checking wall position at: " + i + "," + j);
                        if (hallwayMap.containsKey(currentCoord)) {
                            String existingType = hallwayMap.get(currentCoord);
                            System.out.println("Found existing tile type: " + existingType);
                            // Only keep existing floor tiles to preserve hallway intersections
                            if (existingType.equals("floor") || existingType.equals("endMiddle")) {
                                tiles[i][j] = Tileset.FLOOR;
                                // Keep the existing designation in the map
                            } else {
                                tiles[i][j] = Tileset.WALL;
                                hallwayMap.put(currentCoord, "wall");
                            }
                        } else {
                            tiles[i][j] = Tileset.WALL;
                            hallwayMap.put(currentCoord, "wall");
                        }
                    } else {  // Floor position (middle)
                        currentCoord = new Coordinate(i, j);
                        tiles[i][j] = Tileset.FLOOR;
                        // Only update the map if the position isn't already marked
                        if (!hallwayMap.containsKey(currentCoord)) {
                            hallwayMap.put(currentCoord, "floor");
                        }
                    }
                }
            }
            
            // Handle the end of the hallway
            if (x + length < totalWidth && y + 2 < totalHeight - 4 && !isOccupied(x + length, y + 2)) {
                // Continue with a new room
                tiles[x + length][y] = Tileset.FLOOR;
                hallwayMap.put(new Coordinate(x + length, y), "floor");
                generateRooms(x + length, y - 1);
            } else {
                // End the hallway
                tiles[x + length - 1][y + 1] = Tileset.FLOWER;
                hallwayMap.put(new Coordinate(x + length - 1, y + 1), "wall");
                
                tiles[x + length - 1][y] = Tileset.AVATAR;
                hallwayMap.put(new Coordinate(x + length - 1, y), "endMiddle");
                
                tiles[x + length - 1][y - 1] = Tileset.FLOWER;
                hallwayMap.put(new Coordinate(x + length - 1, y - 1), "wall");
            }
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
            if (x >= 0 && x < totalWidth && y >= 0 && y < totalHeight) {
                return true;
            }
            return false;
        }

        private boolean isOccupied ( int x, int y){
            if (tiles[x][y] == Tileset.NOTHING) {
                return false;
            }
            return true;
        }

        private boolean isFloor ( int x, int y){
            if (tiles[x][y] == Tileset.FLOOR) {
                return true;
            }
            return false;
        }
        private boolean isLastPosition ( int x, int y){
            if (x == 0 || x == totalWidth - 1 || y == 0 || y == totalHeight - 1) {
                return true;
            }
            return false;
        }

        public TETile[][] getTiles () {
            return tiles;
        }
    }


