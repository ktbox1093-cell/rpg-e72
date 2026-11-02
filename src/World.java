import javax.microedition.lcdui.Graphics;
import java.util.Random;

public class World {
    public static final byte TILE_GRASS = 0;
    public static final byte TILE_FOREST = 1;
    public static final byte TILE_DESERT = 2;
    public static final byte TILE_DUNGEON = 3;
    public static final byte TILE_WATER = 4;

    public static final int MAP_WIDTH = 64;
    public static final int MAP_HEIGHT = 64;
    public static final int TILE_SIZE = 16;

    private byte[][] mapGrid;

    public World() {
        this.mapGrid = new byte[MAP_WIDTH][MAP_HEIGHT];
        generateWorld();
    }

    private void generateWorld() {
        Random rand = new Random(1337);
        for (int x = 0; x < MAP_WIDTH; x++) {
            for (int y = 0; y < MAP_HEIGHT; y++) {
                if (x < 22 && y < 22) mapGrid[x][y] = TILE_GRASS;
                else if (x >= 22 && x < 45 && y < 45) mapGrid[x][y] = TILE_FOREST;
                else if (x >= 45) mapGrid[x][y] = TILE_DESERT;
                else if (y >= 45) mapGrid[x][y] = TILE_DUNGEON;
                else {
                    if (rand.nextInt(12) == 0) mapGrid[x][y] = TILE_WATER;
                    else mapGrid[x][y] = TILE_GRASS;
                }
            }
        }
    }

    public boolean isWalkable(int tileX, int tileY) {
        if (tileX < 0 || tileX >= MAP_WIDTH || tileY < 0 || tileY >= MAP_HEIGHT) return false;
        return mapGrid[tileX][tileY] != TILE_WATER;
    }

    public void render(Graphics g, int playerTileX, int playerTileY, int screenW, int screenH) {
        int tilesVisibleX = (screenW / TILE_SIZE) + 2;
        int tilesVisibleY = (screenH / TILE_SIZE) + 2;

        // Posición absoluta de la cámara centrada en el jugador
        int cameraX = playerTileX * TILE_SIZE - (screenW / 2) + (TILE_SIZE / 2);
        int cameraY = playerTileY * TILE_SIZE - (screenH / 2) + (TILE_SIZE / 2);

        int startCol = cameraX / TILE_SIZE;
        int startRow = cameraY / TILE_SIZE;

        for (int col = startCol - 1; col <= startCol + tilesVisibleX; col++) {
            for (int row = startRow - 1; row <= startRow + tilesVisibleY; row++) {
                if (col >= 0 && col < MAP_WIDTH && row >= 0 && row < MAP_HEIGHT) {
                    byte tileType = mapGrid[col][row];
                    int drawX = (col * TILE_SIZE) - cameraX;
                    int drawY = (row * TILE_SIZE) - cameraY;
                    drawTile(g, tileType, drawX, drawY);
                }
            }
        }
    }

    private void drawTile(Graphics g, byte tileType, int x, int y) {
        switch (tileType) {
            case TILE_GRASS: g.setColor(34, 139, 34); break;
            case TILE_FOREST: g.setColor(0, 100, 0); break;
            case TILE_DESERT: g.setColor(218, 165, 32); break;
            case TILE_DUNGEON: g.setColor(80, 80, 80); break;
            case TILE_WATER: g.setColor(30, 144, 255); break;
            default: g.setColor(0, 0, 0); break;
        }
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        g.setColor(20, 20, 20);
        g.drawRect(x, y, TILE_SIZE, TILE_SIZE);
    }
}
