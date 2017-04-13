package net.omega2097.map;

import java.util.ArrayList;
import java.util.List;

public class Map implements IMap {
    private int width;
    private int height;
    private Tile tiles[];

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void init(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width * height];

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                this.tiles[x + y*width] = new Tile(x, y);
            }
        }
    }

    @Override
    public void setProperties(int x, int y, boolean isTransparent, boolean isWalkable) {
        Tile tile = getTileAt(x, y);
        tile.setTransparent(isTransparent);
        tile.setWalkable(isWalkable);
    }

    public Tile getTileAt(int x, int y) {
        if (x >= width || x < 0 || y >= height || y < 0) {
            throw new RuntimeException("Bad tile coordinates: " + x + ", " + y);
        }
        return tiles[x + y * width];
    }

    public Tile getRandomClearTile() {
        Tile tile = new Tile(0, 0);
        for(int i = 0; i < tiles.length; i++) {
            if (tiles[i].isWalkable() && tiles[i].isTransparent()) {
                tile = tiles[i];
                break;
            }
        }
        return tile;
    }
}
