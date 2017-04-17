package net.omega2097.map;

import net.omega2097.GameObject;
import net.omega2097.util.IRandom;

import java.util.ArrayList;
import java.util.List;

public class Map implements IMap {
    private int width;
    private int height;
    private Tile tiles[];
    private IRandom random;
    List<GameObject> enemies = new ArrayList<>();

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
                this.tiles[x * width + y] = new Tile(x, y);
            }
        }
    }

    @Override
    public void setProperties(int x, int y, boolean isTransparent, boolean isWalkable) {
        Tile tile = getTileAt(x, y);
        tile.setTransparent(isTransparent);
        tile.setWalkable(isWalkable);
    }

    @Override
    public void placeObjects(Rectangle room, int roomNumber) {
        int number = random.next(0, 3);
        number = 3;

        while(number > 0) {
            int x = random.next(room.getX(), room.getRight());
            int y = random.next(room.getY(), room.getBottom());
            Tile tile = getTileAt(x, y);
            if (tile.isWalkable() && !tile.isObject()) {
                GameObject gameObject = new GameObject();
                gameObject.setPosition(x + 0.5f, 0.5f, y + 0.5f);
                enemies.add(gameObject);
                tile.setObject(true);
                number--;
            }
        }
    }

    public Map(IRandom random) {
        this.random = random;
    }

    public Tile getTileAt(int x, int y) {
        if (x >= width || x < 0 || y >= height || y < 0) {
            throw new RuntimeException("Bad tile coordinates: " + x + ", " + y);
        }
        return tiles[x * width + y];
    }

    public List<GameObject> getEnemies() {
        return enemies;
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
