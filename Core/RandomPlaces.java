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
    HashMap<Coordinate, String> placesMap = new HashMap<>();
    HashMap<Coordinate, String> roomMap = new HashMap<>();
    int z = 0;
    boolean built;
    int hallLength;
    int rightHallLength;


    static private class Coordinate {
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
        //do I need this "callHallways" if it can only be called once a room is generated? So it must be done from generateRooms?
        callHallways(x,y);
    }

    public void generateRooms(int x, int y) {
        built = false;
        //width and height of room generated
        wide = random.nextInt(11 - 4 + 1) + 4;
        high = random.nextInt(11 - 4 + 1) + 4;
        System.out.println("Room coordinate: x: " + x + " wide: " + wide + " y: " + y + " maxY: " + high + " building: " + z + " built value is " + built);
        z++;
        if (isValidPosition(x, y + high) && isValidPosition(x + wide, y + high) && isValidPosition(x + wide, y) && isValidPosition(x, y)) {
            if (!isAvailable(x, y + high) && !isAvailable(x, y) && !isAvailable(x + wide, y) && !isAvailable(x + wide, y + high)) {
                for (int i = x; i <= (wide + x); i++) {
                    for (int j = y; j <= (high + y); j++) {
                        Coordinate coordinate = new Coordinate(i, j);
                        //(Checks if we are about to build room walls over a hallway, if so, changes the hashMap accordingly)
                        if (i == x || i == (x + wide) || j == y || j == (y + high)) {
                            //if the key coordinate is in the placesMap, and it is a floor, then leave it alone, if tile is endMiddle -> floor, if neither, then place wall
//                            if (placesMap.containsKey(coordinate)) {
                            String tile = placesMap.get(coordinate);
                            // Don't place anything if we find a floor - just skip it
                            if ("floor".equals(tile)) {
                                continue;
                            }
                            // Convert endMiddle to floor
                            if ("endMiddle".equals(tile)) {
                                tiles[i][j] = Tileset.FLOOR;
                                placesMap.put(coordinate, "roomFloor");
                                continue;
                            }
                            // Only place wall if we haven't found a floor or endMiddle
                            tiles[i][j] = Tileset.WALL;
                            placesMap.put(coordinate, "wall");
                        } else {
                            //this overrides if there is already a hallway there, right? It would just enlarge the hallway.
                            tiles[i][j] = Tileset.FLOOR;
                            placesMap.put(coordinate, "roomFloor");
                        }
                        if (isLastPosition(i, j)) {
                            //this means: isLastPosition on the world. If so, cut anything that is there and put the wall up.
                            tiles[i][j] = Tileset.WALL;
                            placesMap.put(coordinate, "wall");
                        }
                    }
                    int e = z - 1;
                    built = true;
                    System.out.println("Built is " + built + " for room " + e);
                }
            } else {
                //meaning the first and last corners of each wall was occupied. Why do I only do those? Because I have fail safes for that other stuff, supposedly
                System.out.println("Tried to generate a room, but the spaces were occupied. Building " + z);
                z++;
            }
        }
    }

    private void callHallways(int x, int y) {
        int max = x + wide - 1;
        int min = x + 1;
        int yMax = y + high - 1;
        int yMin = y + 1;
        //top hallway: y+high, max = x+wide, min = x
        addTopHallway((r.nextInt(max - min + 1) + min), y + high);
        //right wall: x+wide, newY is between y and (y+high)
        addRightHallway(max + 1, (r.nextInt(yMax - yMin + 1) + yMin));
        System.out.println("Hallways successfully called after building " + z);
    }

    private void addRoomFromTop(int x, int y) {
        generateRooms(x,y);
        if(built) {
            System.out.println("This room was built from the addRoomFromTop that called genRooms, now it will callHallways");
            callHallways(x,y);
        }
        else {
                tiles[x+2][y] = Tileset.LOCKED_DOOR;
                Coordinate coordinate = new Coordinate(x+2,y);
                placesMap.put(coordinate, "endMiddle");
                System.out.println("Couldn't generate room from addRoomFromTop, so not adding anything else to it, and trying ends here.");
        }
    }

    private void addRoomFromRight(int x, int y) {
        generateRooms(x,y);
        System.out.println("We made it back to aRFRight after calling genRooms from aRFR");
        if(built) {
            callHallways(x,y);
        }
        else {
            tiles[x + rightHallLength][y + 2] = Tileset.WALL;
            placesMap.put(new Coordinate(x + rightHallLength - 1, y + 1), "wall");

            tiles[x + rightHallLength][y+1] = Tileset.WALL;
            placesMap.put(new Coordinate(x + rightHallLength - 1, y), "endMiddle");

            tiles[x + rightHallLength][y] = Tileset.WALL;
            placesMap.put(new Coordinate(x + rightHallLength - 1, y - 1), "wall");
        }
    }

    private void addTopHallway(int x, int y) {
        hallLength = r.nextInt(10 + 1) + 3;
        if (isValidPosition(x + 1, y + hallLength) && isValidPosition(x, y)) {
            //replaces the wall tile with floor tile from generateRoom
            tiles[x][y] = Tileset.FLOOR;
            Coordinate coordinate = new Coordinate(x, y);
            placesMap.put(coordinate, "floor");
            System.out.println("Building top hallway, number " + z + " bottom coord: " + x + ", "+ y + " top coord length: " + hallLength);
            z++;
            for (int i = x - 1; i < 2 + x; i++) {
                for (int j = y; j < y + hallLength; j++) {
                        coordinate = new Coordinate(i, j);
                        if (i == (x - 1) || i == (x + 1)) {
                            if(!isAvailable(i,j)) {
                            String tile = placesMap.get(coordinate);
                                if (!"floor".equals(tile) || !"endMiddle".equals(tile)) {
                                    tiles[i][j] = Tileset.WALL;
                                    placesMap.put(coordinate, "wall");
                                }
                           }
                        } else {
                            tiles[i][j] = Tileset.FLOOR;
                            placesMap.put(coordinate, "floor");
                        }
                }
            }
            //use this one to add the last dot on the last position...Calls add Room From Top...or not
            if (y + hallLength < (totalHeight - 4) && x - 2 >= 0 && !isAvailable(y + hallLength, x - 2)) {
                addRoomFromTop(x - 2, y + hallLength - 1);
            } else {
                if (!isAvailable(x, y + hallLength)) {
                    tiles[x][y + hallLength] = Tileset.WALL;
                    placesMap.put(new Coordinate(x, y + hallLength), "wall");
                    tiles[x-1][y + hallLength] = Tileset.WALL;
                    placesMap.put(new Coordinate(x-1, y + hallLength), "wall");
                    tiles[x+1][y + hallLength] = Tileset.WALL;
                    placesMap.put(new Coordinate(x+1, y + hallLength), "wall");
                    System.out.print("Checking if we ever make it to line 207 aka the end of addTopHallway, we do!");
                }
                else {
                    tiles[x][y + hallLength - 1] = Tileset.WALL;
                    coordinate = new Coordinate(x, y + hallLength - 1);
                    placesMap.put(coordinate, "endMiddle");
                }
            }
        }
    }

    private void addRightHallway(int x, int y) {
        int rightHallLength = r.nextInt(10 - 3 + 1) + 3;
        if (isValidPosition(x + rightHallLength - 1, y + 1) && isValidPosition(x, y)) {
            // Start with the entrance
            tiles[x][y] = Tileset.FLOOR;
            placesMap.put(new Coordinate(x, y), "floor");
            System.out.println("Building right hallway, number " + z + "left coord: " +x + "," + y + "right coord width " + rightHallLength);
            z++;
            
            // Create the hallway
            for (int i = x + 1; i < x + rightHallLength; i++) {
                for (int j = y - 1; j < y + 2; j++) {
                    Coordinate coordinate = new Coordinate(i, j);
                    if (j == (y - 1) || j == (y + 1)) {
                        if(!placesMap.containsKey(coordinate)) {
                            tiles[i][j] = Tileset.WALL;
                             placesMap.put(coordinate, "wall");
                        }
                    }
//                    if (j == (y - 1) || j == (y + 1)) {
//                        if (placesMap.containsKey(coordinate)) {
//                            String existingType = placesMap.get(coordinate);
//                            // Only keep existing floor tiles to preserve hallway intersections
//                            if (existingType.equals("floor") || existingType.equals("endMiddle")) {
//                                tiles[i][j] = Tileset.FLOOR;
//                                placesMap.put(coordinate, "floor");
//
//                            } else {
//                                tiles[i][j] = Tileset.WALL;
//                                placesMap.put(coordinate, "wall");
//                            }
//                        } else {
//                            tiles[i][j] = Tileset.WALL;
//                            placesMap.put(coordinate, "wall");
//                        }
                    else {
//                        coordinate = new Coordinate(i, j);
                        tiles[i][j] = Tileset.FLOOR;
                            placesMap.put(coordinate, "floor");
                    }
                }
            }
            
            // Handle the end of the hallway
            if (x + rightHallLength < totalWidth && y + 2 < totalHeight - 4 && !isAvailable(x + rightHallLength, y + 2)) {
                // Continue with a new room
                tiles[x + rightHallLength][y] = Tileset.FLOOR;
                placesMap.put(new Coordinate(x + rightHallLength, y), "floor");
                addRoomFromRight(x + rightHallLength, y - 1);
            } else {
                // End the hallway
                tiles[x + rightHallLength - 1][y + 1] = Tileset.WALL;
                placesMap.put(new Coordinate(x + rightHallLength - 1, y + 1), "wall");
                
                tiles[x + rightHallLength - 1][y] = Tileset.WALL;
                placesMap.put(new Coordinate(x + rightHallLength - 1, y), "endMiddle");
                
                tiles[x + rightHallLength - 1][y - 1] = Tileset.WALL;
                placesMap.put(new Coordinate(x + rightHallLength - 1, y - 1), "wall");
            }
        }
    }

    private boolean isValidPosition ( int x, int y){
            return x >= 0 && x < totalWidth && y >= 0 && y < totalHeight;
        }

    private boolean isAvailable(int x, int y){
        return tiles[x][y] != Tileset.NOTHING;
        }

    private boolean isLastPosition ( int x, int y){
            return x == 0 || x == totalWidth - 1 || y == 0 || y == totalHeight - 1;
        }

    public TETile[][] getTiles () {
            return tiles;
        }
    }


