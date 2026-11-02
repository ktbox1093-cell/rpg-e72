import javax.microedition.lcdui.Graphics;
import java.util.Random;

public class World {
    // Constantes de Biomas / Tipos de Terreno
    public static final byte TILE_GRASS = 0;
    public static final byte TILE_FOREST = 1;
    public static final byte TILE_DESERT = 2;
    public static final byte TILE_DUNGEON = 3;
    public static final byte TILE_WATER = 4;

    // Dimensiones del mundo abierto
    public static final int MAP_WIDTH = 64;
    public static final int MAP_HEIGHT = 64;
    public static final int TILE_SIZE = 16;

    private byte[][] mapGrid;
    private int screenWidth;
    private int screenHeight;

    public World(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.mapGrid = new byte[MAP_WIDTH][MAP_HEIGHT];
        generateWorld();
    }

    private void generateWorld() {
        Random rand = new Random(1337);
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                if (x < 22 && y < 22) {
                    mapGrid[x][y] = TILE_GRASS;
                } else if (x >= 22 && x < 45 && y < 45) {
                    mapGrid[x][y] = TILE_FOREST;
                } else if (x >= 45) {
                    mapGrid[x][y] = TILE_DESERT;
                } else if (y >= 45) {
                    mapGrid[x][y] = TILE_DUNGEON;
                } else {
                    int val = rand.nextInt(12);
                    if (val == 0) {
                        mapGrid[x][y] = TILE_WATER;
                    } else {
                        mapGrid[x][y] = TILE_GRASS;
                    }
                }
            }
        }
    }

    public boolean isWalkable(int tileX, int tileY) {
        if (tileX < 0 || tileX >= MAP_WIDTH || tileY < 0 || tileY >= MAP_HEIGHT) {
            return false;
        }
        byte tile = mapGrid[tileX][tileY];
        return tile != TILE_WATER;
    }

    public byte getTile(int tileX, int tileY) {
        if (tileX < 0 || tileX >= MAP_WIDTH || tileY < 0 || tileY >= MAP_HEIGHT) {
            return TILE_WATER;
        }
        return mapGrid[tileX][tileY];
    }

    public void render(Graphics g, int playerTileX, int playerTileY, int offsetX, int offsetY) {
        int tilesVisibleX = screenWidth / TILE_SIZE + 2;
        int tilesVisibleY = screenHeight / TILE_SIZE + 2;

        int startX = playerTileX - (tilesVisibleX / 2);
        int startY = playerTileY - (tilesVisibleY / 2);

        if (startX < 0) startX = 0;
        if (startY < 0) startY = 0;
        if (startX > MAP_WIDTH - tilesVisibleX) startX = Math.max(0, MAP_WIDTH - tilesVisibleX);
        if (startY > MAP_HEIGHT - tilesVisibleY) startY = Math.max(0, MAP_HEIGHT - tilesVisibleY);

        for (int x = 0; x < tilesVisibleX; x++) {
            for (int y = 0; y < tilesVisibleY; y++) {
                int mapX = startX + x;
                int mapY = startY + y;

                if (mapX < MAP_WIDTH && mapY < MAP_HEIGHT) {
                    byte tileType = mapGrid[mapX][mapY];
                    
                    int drawX = (x * TILE_SIZE) - (playerTileX * TILE_SIZE - (screenWidth / 2)) + offsetX;
                    int drawY = (y * TILE_SIZE) - (playerTileY * TILE_SIZE - (screenHeight / 2)) + offsetY;

                    drawTile(g, tileType, drawX, drawY);
                }
            }
        }
    }

    private void drawTile(Graphics g, byte tileType, int x, int y) {
        switch (tileType) {
            case TILE_GRASS:
                g.setColor(34, 139, 34);
                break;
            case TILE_FOREST:
                g.setColor(0, 100, 0);
                break;
            case TILE_DESERT:
                g.setColor(218, 165, 32);
                break;
            case TILE_DUNGEON:
                g.setColor(80, 80, 80);
                break;
            case TILE_WATER:
                g.setColor(30, 144, 255);
                break;
            default:
                g.setColor(0, 0, 0);
                break;
        }
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        g.setColor(20, 20, 20);
        g.drawRect(x, y, TILE_SIZE, TILE_SIZE);
    }
}
