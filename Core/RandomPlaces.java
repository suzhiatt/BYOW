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
    HashMap<Coordinate, String> roomMap = new HashMap<>();
    int z = 0;
    boolean built;
    int hallLength;
    int rightHallLength;


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
        //checking if the coordinates are 1. valid (still within map bounds) and if so, 2. four corners aren't occupied
        if (isValidPosition(x, y + high) && isValidPosition(x + wide, y + high) && isValidPosition(x + wide, y) && isValidPosition(x, y)) {
            if (!isOccupied(x, y + high) && !isOccupied(x, y) && !isOccupied(x + wide, y) && !isOccupied(x + wide, y + high)) {
                for (int i = x; i <= (wide + x); i++) {
                    for (int j = y; j <= (high + y); j++) {
                        Coordinate coordinate = new Coordinate(i, j);
                        //(Checks if we are about to build room walls over a hallway, if so, changes the hashMap accordingly)
                        if (i == x || i == (x + wide) || j == y || j == (y + high)) {
                            //if the key coordinate is in the hallwayMap, and it is a floor, then leave it alone, if tile is endMiddle -> floor, if neither, then place wall
                            if (hallwayMap.containsKey(coordinate)) {
                                String tile = hallwayMap.get(coordinate);
                                // Don't place anything if we find a floor - just skip it
                                if (tile.equals("floor")) {
                                    continue;
                                }
                                // Convert endMiddle to floor
                                if (tile.equals("endMiddle")) {
                                    tiles[i][j] = Tileset.AVATAR;
                                    hallwayMap.put(coordinate, "floor");
                                    continue;
                                }
                            }
                            // Only place wall if we haven't found a floor or endMiddle
                            tiles[i][j] = Tileset.WALL;
                            //do we need to put it into the room map? As a wall? When do we check the roomMap?
                            roomMap.put(coordinate, "room");
                        } else {
                            //this overrides if there is already a hallway there, right? It would just enlarge the hallway.
                            tiles[i][j] = Tileset.FLOOR;
                        }
                        if (isLastPosition(i, j)) {
                            //this means: isLastPosition on the world. If so, cut anything that is there and put the wall up.
                            tiles[i][j] = Tileset.WALL;
                            roomMap.put(coordinate, "room");
                        }
                    }
                }
                int e = z-1;
                System.out.println("Room was successfully built for building " + e + " This is from generateRooms");
                built = true;
                System.out.println("Built is " + built + " for room " + e);
            }
        }
        else {
            //meaning the first and last corners of each wall was occupied. Why do I only do those? Because I have fail safes for that other stuff, supposedly
            System.out.println("Tried to generate a room, but the spaces were occupied. Building " + z);
            z++;
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
        System.out.println("We made it back to aRFTop after genRooms is called");

        if(built) {
            System.out.println("This room was built from the addRoomFromTop that called genRooms, now it will callHallways");
            //is this repetitive because callHallways is already happening? when each genRooms is called?
            callHallways(x,y);
        }
        else {
                tiles[x+2][y] = Tileset.LOCKED_DOOR;
                Coordinate coordinate = new Coordinate(x+2,y);
                hallwayMap.put(coordinate, "endMiddle");
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
            tiles[x + rightHallLength][y + 2] = Tileset.AVATAR;
            hallwayMap.put(new Coordinate(x + rightHallLength - 1, y + 1), "wall");

            tiles[x + rightHallLength][y+1] = Tileset.AVATAR;
            hallwayMap.put(new Coordinate(x + rightHallLength - 1, y), "endMiddle");

            tiles[x + rightHallLength][y] = Tileset.AVATAR;
            hallwayMap.put(new Coordinate(x + rightHallLength - 1, y - 1), "wall");
        }
    }

    private void addTopHallway(int x, int y) {
        //calculate length of hallway
        hallLength = r.nextInt(10 + 1) + 3;
        //if it is invalid, then return
        if (isValidPosition(x + 1, y + hallLength) && isValidPosition(x, y)) {
            //replaces the wall tile with floor tile from generateRoom
            tiles[x][y] = Tileset.FLOOR;
            Coordinate coordinate = new Coordinate(x, y);
            hallwayMap.put(coordinate, "floor");
            System.out.println("Building top hallway, number " + z + "bottom coord: " + x +", "+ y + "top coord length " + hallLength);
            z++;
            for (int i = x - 1; i < 2 + x; i++) {
                for (int j = y; j < y + hallLength; j++) {
//                    if (!isOccupied(i, j)) {
                        coordinate = new Coordinate(i, j);
                        if (i == (x - 1) || i == (x + 1)) {
                            if(!isOccupied(i,j)) {
                                boolean contains = hallwayMap.containsKey(coordinate);
                                if (!contains) {
                                    tiles[i][j] = Tileset.UNLOCKED_DOOR;
                                    hallwayMap.put(coordinate, "wall");
                                }
                            }
                        } else {
                            tiles[i][j] = Tileset.FLOOR;
                            hallwayMap.put(coordinate, "floor");
                        }
                }
            }
            //use this one to add the last dot on the last position...
            //this isn't working. It is checking for occupied spaces, but this fail safe isn't working. It only checks if the spaces just above the hall are occupied.
            //Or, do we fix this in the aRFT section?
            if (y + hallLength < (totalHeight - 4) && x - 2 >= 0 && !isOccupied(y + hallLength, x - 2)) {
                addRoomFromTop(x - 2, y + hallLength - 1);
            } else {
                if (!isOccupied(x, y + hallLength)) {
                    tiles[x][y + hallLength - 1] = Tileset.WALL;
                    coordinate = new Coordinate(x, y + hallLength - 1);
                    hallwayMap.put(coordinate, "endMiddle");
                }
                else{
                    tiles[x][y + hallLength - 1] = Tileset.FLOOR;
                }
            }
        }
    }

    private void addRightHallway(int x, int y) {
        int rightHallLength = r.nextInt(10 - 3 + 1) + 3;
        if (isValidPosition(x + rightHallLength - 1, y + 1) && isValidPosition(x, y)) {
            // Start with the entrance
            tiles[x][y] = Tileset.FLOOR;
            hallwayMap.put(new Coordinate(x, y), "floor");
//            System.out.println("Building right hallway, number " + z + "left coord: " +x +","+y + "right coord width " + length);
            z++;
            
            // Create the hallway
            for (int i = x + 1; i < x + rightHallLength; i++) {
                for (int j = y - 1; j < y + 2; j++) {
                    Coordinate currentCoord = new Coordinate(i, j);
                    if (j == (y - 1) || j == (y + 1)) {  // Wall positions
//                        System.out.println("Checking wall position at: " + i + "," + j);
                        if (hallwayMap.containsKey(currentCoord)) {
                            String existingType = hallwayMap.get(currentCoord);
//                            System.out.println("Found existing tile type: " + existingType);
                            // Only keep existing floor tiles to preserve hallway intersections
                            if (existingType.equals("floor") || existingType.equals("endMiddle")) {
                                tiles[i][j] = Tileset.FLOOR;
                                hallwayMap.put(currentCoord, "floor");
//                                System.out.println("Floor already existed in map.");
                                // Keep the existing designation in the map
                            } else {
                                tiles[i][j] = Tileset.WALL;
                                hallwayMap.put(currentCoord, "wall");
//                                System.out.println("Built wall");
                            }
                        } else {
                            tiles[i][j] = Tileset.WALL;
                            hallwayMap.put(currentCoord, "wall");
//                            System.out.println("Built wall");
                        }
                    } else {  // Floor position (middle)
                        currentCoord = new Coordinate(i, j);
                        tiles[i][j] = Tileset.FLOOR;
//                        System.out.println("Built floor");
                        // Only update the map if the position isn't already marked
                        if (!hallwayMap.containsKey(currentCoord)) {
                            hallwayMap.put(currentCoord, "floor");
                        }
                    }
                }
            }
            
            // Handle the end of the hallway
            if (x + rightHallLength < totalWidth && y + 2 < totalHeight - 4 && !isOccupied(x + rightHallLength, y + 2)) {
                // Continue with a new room
                tiles[x + rightHallLength][y] = Tileset.FLOOR;
                hallwayMap.put(new Coordinate(x + rightHallLength, y), "floor");
                addRoomFromRight(x + rightHallLength, y - 1);
            } else {
                // End the hallway
                tiles[x + rightHallLength - 1][y + 1] = Tileset.FLOWER;
                hallwayMap.put(new Coordinate(x + rightHallLength - 1, y + 1), "wall");
                
                tiles[x + rightHallLength - 1][y] = Tileset.AVATAR;
                hallwayMap.put(new Coordinate(x + rightHallLength - 1, y), "endMiddle");
                
                tiles[x + rightHallLength - 1][y - 1] = Tileset.FLOWER;
                hallwayMap.put(new Coordinate(x + rightHallLength - 1, y - 1), "wall");
            }
        }
    }

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


