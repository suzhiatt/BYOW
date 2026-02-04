package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class RandomPlaces {
    private final Random r;
    int totalHeight;
    int totalWidth;
    int wide;
    int high;
    TETile[][] tiles;
    public static HashMap<Coordinate, String> placesMap = new HashMap<>();
    boolean built;
    int hallLength;
    int rightHallLength;
    int firstX;
    int firstY;

    public static class Coordinate {
        final int x;
        final int y;

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

    public RandomPlaces(int width, int height, long seed) {
        placesMap.clear();
        this.r = new Random(seed);
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
        firstX = r.nextInt(4)+1;
        firstY = r.nextInt(4)+1;
        addRoomFromTopHallway(firstX,firstY);
    }

    public void generateRooms(int x, int y) {
        built = false;
        //width and height of room generated
        wide = r.nextInt(11 - 4 + 1) + 4;
        high = r.nextInt(11 - 4 + 1) + 4;

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
                            String tile = placesMap.get(coordinate);
                            if ("endMiddle".equals(tile) || "floor".equals(tile) || "roomFloor".equals(tile)) {
                                setFloor(i, j, "roomFloor");
//                                tiles[i][j] = Tileset.FLOOR;
//                                placesMap.put(coordinate, "roomFloor");
                            }
                            else {
                                // Only place wall if we haven't found a floor or endMiddle
                                setWall(i,j);
//                                tiles[i][j] = Tileset.WALL;
//                                placesMap.put(coordinate, "wall");
                                }
                        } else {
                            //this overrides if there is already a hallway there, right? It would just enlarge the hallway.
                            setFloor(i,j,"roomFloor");
//                            tiles[i][j] = Tileset.FLOOR;
//                            placesMap.put(coordinate, "roomFloor");
                        }
                        if (isLastPosition(i, j)) {
                            setWall(i,j);
//                            tiles[i][j] = Tileset.WALL;
//                            placesMap.put(coordinate, "wall");
                        }
                    }
                    built = true;
                }
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
    }

    private void addRoomFromTopHallway(int x, int y) {
        generateRooms(x,y);
        if(built) {
            callHallways(x,y);
        }
        else {
//            Coordinate coordinate = new Coordinate(x+2,y);
            String tile = placesMap.get(new Coordinate(x+2,y));
            if(!"roomFloor".equals(tile)) {
                //adding for the rare occasion that a hall has been built directly over a top hallway...
                Coordinate topCoordinate = new Coordinate(x+2,y+1);
                String topTile = placesMap.get(topCoordinate);
                if(!"floor".equals(topTile)) {
                    setWall(x+2, y);
//                    tiles[x+2][y] = Tileset.WALL;
//                    placesMap.put(new Coordinate(x+2,y), "wall");
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
            placesMap.put(new Coordinate(x + rightHallLength, y + 2), "wall");

            tiles[x + rightHallLength][y+1] = Tileset.WALL;
            placesMap.put(new Coordinate(x + rightHallLength, y+1), "endMiddle");

            tiles[x + rightHallLength][y] = Tileset.WALL;
            placesMap.put(new Coordinate(x + rightHallLength, y), "wall");
        }
    }

    private void addTopHallway(int x, int y) {
        hallLength = r.nextInt(10 + 1) + 3;
        if (isValidPosition(x, y + hallLength) && isValidPosition(x, y)) {
            //replaces the wall tile with floor tile from generateRoom
            setFloor(x, y, "floor");
            for (int i = x - 1; i < 2 + x; i++) {
                for (int j = y; j < y + hallLength; j++) {
                    String tile = placesMap.get(new Coordinate(i,j));
                    if (i == (x - 1) || i == (x + 1)) {
                        if (isAvailable(i, j)) {
                            setWall(i,j);
                        }
                        else {
                            //coordinate options: floor, roomFloor, wall, endMiddle
                            if("endMiddle".equals(tile)) {
                                if(i==(x+1)) {
                                    System.out.println("Building aTH and reach an endMiddle, placing a wall. Line 182");
                                    setWall(i,j);
                                }
                                else {
                                    setFloor(i,j,"floor");}
                            }
                        }
                    } else {
//                        System.out.println("We are checking to add a floor as part of addTopHall. What it will be replacing is..." + tile + "Tiles is at coord: " + i + ", " + j);
                        //checks to make sure that we are not replacing a roomFloor tile with a floor tile.
                        if (!"roomFloor".equals(tile)) {
                            setFloor(i,j,"floor");
//                            tiles[i][j] = Tileset.FLOOR;
//                            placesMap.put(coordinate, "floor");
                        }
                    }
                }
            }
            //use this one to add the last dot on the last position...Calls add Room From Top...or not
            if (y + hallLength < (totalHeight - 4) && x - 2 >= 0 && isAvailable(x - 2, y + hallLength)) {
                addRoomFromTopHallway(x - 2, y + hallLength - 1);
            } else {
                if (isAvailable(x, y + hallLength)) {
                    tiles[x][y + hallLength] = Tileset.WALL;
                    placesMap.put(new Coordinate(x, y + hallLength), "endMiddle");
                    tiles[x-1][y + hallLength] = Tileset.WALL;
                    placesMap.put(new Coordinate(x-1, y + hallLength), "wall");
                    tiles[x+1][y + hallLength] = Tileset.WALL;
                    placesMap.put(new Coordinate(x+1, y + hallLength), "wall");
//                    setWall(x-1, y + hallLength);
//                    setEndMiddle(x,y+hallLength);
//                    setWall(x+1, y + hallLength);
                } else {
//                    Coordinate topCoordinate = new Coordinate(x, y + hallLength);
                    String tile = placesMap.get(new Coordinate(x, y + hallLength));
                    System.out.println("*** Line 220, we were going to put the y+hallL wall but there is a " + tile);
                    if ("wall".equals(tile)) {
                        Coordinate aboveTopCoordinate = new Coordinate(x, y + hallLength + 1);
                        String topTile = placesMap.get(aboveTopCoordinate);
                        System.out.println("*** Line 224, aboveTopCoord is " + topTile);
                        if ("floor".equals(topTile) || "roomFloor".equals(topTile)) {
                            setFloor(x, y+hallLength, "floor");
                        }
                        else if ("wall".equals(topTile)) {
                            Coordinate leftTopCoord = new Coordinate(x-1, y + hallLength);
                            Coordinate rightTopCoord = new Coordinate(x+1, y + hallLength);
                            if("roomFloor".equals(placesMap.get(new Coordinate(x-1, y + hallLength))) || placesMap.get(rightTopCoord) == null) {
                                //we encountered a room wall on the right side, so we need to end the hallway in three tiles...
                                setWall(x-1,y+hallLength);
                                setWall(x,y+hallLength);
                                setWall(x+1, y+hallLength);
//                                tiles[x - 1][y + hallLength] = Tileset.WALL;
//                                placesMap.put(new Coordinate(x-1,y+hallLength), "wall");
//                                tiles[x][y + hallLength] = Tileset.WALL;
//                                placesMap.put(new Coordinate(x,y+hallLength), "wall");
//                                tiles[x + 1][y + hallLength] = Tileset.WALL;
//                                placesMap.put(new Coordinate(x+1,y+hallLength), "wall");
                            }
                        }
                    }
                    if("floor".equals(tile)) {
                        Coordinate aboveTopCoordinate = new Coordinate(x+1, y + hallLength + 1);
                        String aboveTopTile = placesMap.get(aboveTopCoordinate);
                        System.out.println("*** Line 232, aboveTopCoord is " + aboveTopTile);
                        if("null".equals(aboveTopTile)) {
                            setWall(x+1,y + hallLength + 1);
//                            tiles[x+1][y + hallLength + 1] = Tileset.MOUNTAIN;
//                            placesMap.put(aboveTopCoordinate, "wall");
                        }
                    }
                }
            }
        }
    }


    private void addRightHallway(int x, int y) {
        int rightHallLength = r.nextInt(10 - 3 + 1) + 3;
        if (isValidPosition(x + rightHallLength, y + 1) && isValidPosition(x, y)) {
            // Start with the entrance
            tiles[x][y] = Tileset.FLOOR;
            placesMap.put(new Coordinate(x, y), "floor");
            
            // Create the hallway
            for (int i = x + 1; i < x + rightHallLength; i++) {
                for (int j = y - 1; j < y + 2; j++) {
                    Coordinate coordinate = new Coordinate(i, j);
                    if (j == (y - 1) || j == (y + 1)) {
                        if(isAvailable(i, j)) {
                            tiles[i][j] = Tileset.WALL;
                            placesMap.put(coordinate, "wall");
                        }
                        else {
                            String tile = placesMap.get(coordinate);
                            //coordinate options: floor, roomFloor, wall, endMiddle
                            if("endMiddle".equals(tile) && i == (x-1)) {
                                tiles[i][j] = Tileset.FLOOR;
                                placesMap.put(coordinate, "floor");
                            }
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
                tiles[x + rightHallLength][y] = Tileset.FLOOR;
                placesMap.put(new Coordinate(x + rightHallLength, y), "floor");
                addRoomFromRight(x + rightHallLength, y - 1);
            } else {
                setWall(x + rightHallLength,y+1);
//                tiles[x + rightHallLength - 1][y + 1] = Tileset.WALL;
//                placesMap.put(new Coordinate(x + rightHallLength - 1, y + 1), "wall");
                setEndMiddle(x + rightHallLength, y);
//                tiles[x + rightHallLength - 1][y] = Tileset.WALL;
//                placesMap.put(new Coordinate(x + rightHallLength - 1, y), "endMiddle");
                setWall(x + rightHallLength, y-1);
//                tiles[x + rightHallLength - 1][y - 1] = Tileset.WALL;
//                placesMap.put(new Coordinate(x + rightHallLength - 1, y - 1), "wall");
//            }
            }
        }
    }

    public static String getValue(Coordinate key) {
        return placesMap.get(key);
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

    public int getFirstX() {
        return firstX;
    }

    public int getFirstY() {
        return firstY;
    }

    public void setFloor (int x, int y, String material ) {
        tiles[x][y] = Tileset.FLOOR;
        placesMap.put(new Coordinate(x,y), material);
    }

    public void setWall(int x, int y) {
        tiles[x][y] = Tileset.WALL;
        placesMap.put(new Coordinate(x,y), "wall");
    }

    public void setEndMiddle(int x, int y) {
        tiles[x][y] = Tileset.WALL;
        placesMap.put(new Coordinate(x,y), "endMiddle");
    }

}


