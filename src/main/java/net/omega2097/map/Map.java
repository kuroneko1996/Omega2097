package net.omega2097.map;

import net.omega2097.actors.Actor;
import net.omega2097.actors.EnemyAi;
import net.omega2097.objects.Medkit;
import net.omega2097.objects.Treasure;
import net.omega2097.util.IRandom;

import java.util.ArrayList;
import java.util.List;

public class Map implements IMap {
    private int width;
    private int height;
    private Tile tiles[];
    private IRandom random;
    private List<Actor> enemies = new ArrayList<>();
    private List<Medkit> medkits = new ArrayList<>();
    private List<Treasure> treasures = new ArrayList<>();

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
        placeEnemies(room);
        placeMedkits(room);
        placeTreasures(room);
    }

    private void placeEnemies(Rectangle room) {
        int number = random.next(0, 3);

        while(number > 0) {
            int x = random.next(room.getX(), room.getRight());
            int y = random.next(room.getY(), room.getBottom());
            Tile tile = getTileAt(x, y);
            if (tile.isWalkable() && !tile.isObject()) {
                Actor gameObject = new Actor();
                gameObject.getHealth().setMax(25f).setCurrent(25f);
                gameObject.setPosition(x, 0.5f, y);
                gameObject.setSolid(true);
                gameObject.setAi(new EnemyAi(gameObject));
                // TODO set orientation
                enemies.add(gameObject);
                tile.setObject(true);
                number--;
            }
        }
    }

    private void placeMedkits(Rectangle room) {
        int number = random.next(0, 1);

        while(number > 0) {
            int x = random.next(room.getX(), room.getRight());
            int y = random.next(room.getY(), room.getBottom());
            Tile tile = getTileAt(x, y);
            if (tile.isWalkable() && !tile.isObject()) {
                Medkit gameObject = new Medkit();
                gameObject.setPosition(x, 0.5f, y);
                gameObject.setSolid(false);
                medkits.add(gameObject);
                tile.setObject(true);
                number--;
            }
        }
    }

    private void placeTreasures(Rectangle room) {
        int number = random.next(0, 2);

        while(number > 0) {
            int x = random.next(room.getX(), room.getRight());
            int y = random.next(room.getY(), room.getBottom());
            Tile tile = getTileAt(x, y);
            if (tile.isWalkable() && !tile.isObject()) {
                Treasure gameObject = new Treasure();
                gameObject.setPosition(x, 0.5f, y);
                gameObject.setSolid(false);
                treasures.add(gameObject);
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

    public List<Actor> getEnemies() {
        return enemies;
    }

    public List<Medkit> getMedkits() {
        return medkits;
    }

    public List<Treasure> getTreasures() {
        return treasures;
    }

    public Tile getRandomClearTile() {
        Tile tile = new Tile(0, 0);
        for(int i = 0; i < tiles.length; i++) {
            if (tiles[i].isWalkable() && tiles[i].isTransparent() && !tiles[i].isObject()) {
                tile = tiles[i];
                break;
            }
        }
        return tile;
    }
}
