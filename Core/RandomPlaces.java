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
    int z = 0;
    boolean built;
    int hallLength;
    int rightHallLength;


    static private class Coordinate {
        private final int x, y;

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
        //calls a random x, y to being the room. The bound is between 1 and 5 so it begins in the bottom left corner of world.
        //I noticed that with the world beginning at x or y = 0, then it only adds one hallway, and can't generate a Room after that.
        int x = random.nextInt(4)+1;
        int y = random.nextInt(4)+1;
        addRoomFromTopHallway(x,y);
    }

    public void generateRooms(int x, int y) {
        built = false;
        //width and height of room generated
        wide = random.nextInt(11 - 4 + 1) + 4;
        high = random.nextInt(11 - 4 + 1) + 4;
        System.out.println("Room coordinate: x: " + x + " wide: " + wide + " y: " + y + " maxY: " + high + " building: " + z + " built value is " + built);
        z++;

        boolean isValid = (isValidPosition(x, y + high) && isValidPosition(x + wide, y + high) && isValidPosition(x + wide, y) && isValidPosition(x, y));

        if (isValid) {
            boolean isAvailableSpace = isAvailable(x, y + high) && isAvailable(x, y) && isAvailable(x + wide, y) && isAvailable(x + wide, y + high);

            if (isAvailableSpace) {
                for (int i = x; i <= (wide + x); i++) {
                    for (int j = y; j <= (high + y); j++) {
                        Coordinate coordinate = new Coordinate(i, j);
                        //(Checks if we are about to build room walls over a hallway, if so, changes the hashMap accordingly)
                        if (i == x || i == (x + wide) || j == y || j == (y + high)) {
                            //if the key coordinate is in the placesMap, and it is a floor, then leave it alone, if tile is endMiddle -> floor, if neither, then place wall
//                            if (placesMap.containsKey(coordinate)) {
                            String tile = placesMap.get(coordinate);
                            System.out.println("About to build a genRooms wall. This is the tile that is already there..." + tile);
                            if ("endMiddle".equals(tile)) {
                                tiles[i][j] = Tileset.LOCKED_DOOR;
                                placesMap.put(coordinate, "roomFloor");
                                System.out.println("I found an endMiddle piece");
                            }
                            if("floor".equals(tile)) {
                                tiles[i][j] = Tileset.FLOOR;
                                placesMap.put(coordinate, "roomFloor");
                                System.out.println("I found a floor piece, so i put down a room floor");
                            }
                            else {
                                if(!"roomFloor".equals(tile)) {
                                    System.out.println("There was no endMiddle or floor, so i put down a wall piece");
                                    // Only place wall if we haven't found a floor or endMiddle
                                    tiles[i][j] = Tileset.WALL;
                                    placesMap.put(coordinate, "wall");}
                                }
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
                    built = true;
                }
            } else {
                //meaning the first and last corners of each wall was occupied. Why do I only do those? Because I have fail safes for that other stuff, supposedly
                System.out.println("Tried to generate a room, but the spaces were occupied. Building " + z);
                z++;
            }
        }
    }

    private void callHallways(int x, int y) {
        //uses class "wide" and "high" that was previously chosen in generateRooms to choose a point to begin the hallway from
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

    private void addRoomFromTopHallway(int x, int y) {
        generateRooms(x,y);
        if(built) {
            callHallways(x,y);
        }
        else {
            Coordinate coordinate = new Coordinate(x+2,y);
            String tile = placesMap.get(coordinate);
            System.out.println("Line 139 has a tile that is ... if this is ever NOT floor, do a dance. Otherwise, we should delete this?" + tile);
            if(!"roomFloor".equals(tile)) {
                //adding for the rare occasion that a hall has been built directly over a top hallway...
                Coordinate topCoordinate = new Coordinate(x+2,y+1);
                String topTile = placesMap.get(topCoordinate);
                if(!"floor".equals(topTile)) {
                    tiles[x+2][y] = Tileset.WALL;
                    placesMap.put(coordinate, "wall");
                }
            }
        }
    }

    private void addRoomFromRight(int x, int y) {
        generateRooms(x,y);
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
            System.out.println("Building top hallway, number " + z + " bottom coord: " + x + ", " + y + " top coord length: " + hallLength);
            z++;
            for (int i = x - 1; i < 2 + x; i++) {
                for (int j = y; j < y + hallLength; j++) {
                    coordinate = new Coordinate(i, j);
                    if (i == (x - 1) || i == (x + 1)) {
                        if (isAvailable(i, j)) {
                            tiles[i][j] = Tileset.WALL;
                            placesMap.put(coordinate, "wall");
                        }
                    } else {
                        String tile = placesMap.get(coordinate);
                        //checks to make sure that we are not replacing a roomFloor tile with a floor tile.
                        if (!"roomFloor".equals(tile)) {
                            tiles[i][j] = Tileset.FLOOR;
                            placesMap.put(coordinate, "floor");
                        }
                    }
                }
            }
            //use this one to add the last dot on the last position...Calls add Room From Top...or not
            if (y + hallLength < (totalHeight - 4) && x - 2 >= 0 && isAvailable(x - 2, y + hallLength)) {
                addRoomFromTopHallway(x - 2, y + hallLength - 1);
            } else {
                if (isAvailable(x, y + hallLength)) {
                    tiles[x][y + hallLength - 1] = Tileset.WALL;
                    placesMap.put(new Coordinate(x, y + hallLength - 1), "endMiddle");
                } else {
                    Coordinate topCoordinate = new Coordinate(x, y + hallLength);
                    String tile = placesMap.get(topCoordinate);
                    System.out.println("*** Line 205, topCoord is " + tile);
                    if ("wall".equals(tile)) {
                        Coordinate aboveTopCoordinate = new Coordinate(x, y + hallLength + 1);
                        String topTile = placesMap.get(aboveTopCoordinate);
                        System.out.println("*** Line 209, aboveTopCoord is " + topTile);
                        if ("floor".equals(topTile) || "roomFloor".equals(topTile)) {
                            tiles[x][y + hallLength] = Tileset.FLOOR;
                            placesMap.put(topCoordinate, "floor");
                        } else if ("endMiddle".equals(topTile) || "wall".equals(topTile)) {
                            tiles[x + 1][y + hallLength] = Tileset.MOUNTAIN;
                            placesMap.put(new Coordinate(x+1,y+hallLength), "wall");
                        }
                    }
                    if("floor".equals(tile)) {
                        Coordinate aboveTopCoordinate = new Coordinate(x+1, y + hallLength + 1);
                        String aboveTopTile = placesMap.get(aboveTopCoordinate);
                        System.out.println("*** Line 232, aboveTopCoord is " + aboveTopTile);
                        if("null".equals(aboveTopTile)) {
                            tiles[x+1][y + hallLength + 1] = Tileset.WALL;
                            placesMap.put(aboveTopCoordinate, "wall");
                        }
                    }
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
                    else {
                        tiles[i][j] = Tileset.FLOOR;
                            placesMap.put(coordinate, "floor");
                    }
                }
            }
            
            // Handle the end of the hallway
            if (x + rightHallLength < totalWidth && y + 2 < totalHeight - 4 && isAvailable(x + rightHallLength, y + 2)) {
                // Continue with a new room
                tiles[x + rightHallLength][y] = Tileset.LOCKED_DOOR;
                placesMap.put(new Coordinate(x + rightHallLength, y), "floor");
                addRoomFromRight(x + rightHallLength, y - 1);
            } else {
                // End the hallway
                tiles[x + rightHallLength - 1][y + 1] = Tileset.AVATAR;
                placesMap.put(new Coordinate(x + rightHallLength - 1, y + 1), "wall");
                
                tiles[x + rightHallLength - 1][y] = Tileset.AVATAR;
                placesMap.put(new Coordinate(x + rightHallLength - 1, y), "endMiddle");
                
                tiles[x + rightHallLength - 1][y - 1] = Tileset.AVATAR;
                placesMap.put(new Coordinate(x + rightHallLength - 1, y - 1), "wall");
            }
        }
    }

    private boolean isValidPosition ( int x, int y){
            return x >= 0 && x < totalWidth && y >= 0 && y < totalHeight;
        }

    private boolean isAvailable(int x, int y){
        return tiles[x][y] == Tileset.NOTHING;
        }

    private boolean isLastPosition ( int x, int y){
            return x == 0 || x == totalWidth - 1 || y == 0 || y == totalHeight - 1;
        }

    public TETile[][] getTiles () {
            return tiles;
        }
    }


