package net.omega2097.map;

import net.omega2097.GameObject;
import net.omega2097.actors.Actor;
import net.omega2097.actors.EnemyAi;
import net.omega2097.actors.Shooter;
import net.omega2097.objects.Ammo;
import net.omega2097.objects.Medkit;
import net.omega2097.objects.Pickable;
import net.omega2097.objects.Treasure;
import net.omega2097.util.IRandom;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Map implements IMap {
    private int width;
    private int height;
    private Tile tiles[];
    private IRandom random;
    private List<Actor> guards = new ArrayList<>();
    private List<Actor> dogs = new ArrayList<>();
    private List<GameObject> walls = new ArrayList<>();
    private List<GameObject> doors = new ArrayList<>();
    private List<Pickable> pickables = new ArrayList<>();
    private Vector2f playerSpawn = new Vector2f();
    private List<Vector2f> exits = new ArrayList<>();
    private List<GameObject> objects = new ArrayList<>();

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
        System.out.println(roomNumber);
        if (roomNumber == 0) {
            setPlayerSpawn(room);
        } else {
            placeEnemies(room);
            placeMedkits(room);
            placeTreasures(room);
        }
    }

    public Vector2f getPlayerSpawn() {
        return playerSpawn;
    }

    public void setPlayerSpawn(int x, int y) {
        playerSpawn = new Vector2f(x, y);
    }

    public void addExit(int x, int y) {
        exits.add(new Vector2f(x, y));
    }

    public void addWall(int x, int y, String textureName, int textureIndex) {
        Tile tile = getTileAt(x, y);
        tile.setWalkable(false);
        tile.setTransparent(false);
        GameObject gameObject = new GameObject();
        gameObject.setPosition(x, 0.5f, -y);
        gameObject.setSolid(true);
        gameObject.setTextureName(textureName);
        gameObject.setTextureIndex(textureIndex);
        gameObject.setName("Wall x=" + x + ", y=" + y);
        walls.add(gameObject);
    }

    public void addGuard(int x, int y) {
        Tile tile = getTileAt(x, y);
        // TODO set orientation
        Actor gameObject = new Actor();
        gameObject.getHealth().setMax(25f).setCurrent(25f);
        gameObject.setPosition(x, 0.5f, -y);
        gameObject.setSolid(true);
        EnemyAi enemyAi = new EnemyAi(gameObject);
        gameObject.setAi(enemyAi);

        enemyAi.setChasingStopDistance(3f);
        Shooter shooter = new Shooter(gameObject);
        gameObject.setShooter(shooter);
        guards.add(gameObject);

        tile.setObject(true);
    }

    public void addDog(int x, int y) {
        Tile tile = getTileAt(x, y);
        // TODO set orientation
        Actor gameObject = new Actor();
        gameObject.getHealth().setMax(25f).setCurrent(25f);
        gameObject.setPosition(x, 0.5f, -y);
        gameObject.setSolid(true);
        EnemyAi enemyAi = new EnemyAi(gameObject);
        gameObject.setAi(enemyAi);

        enemyAi.setChasingStopDistance(1f);
        dogs.add(gameObject);

        tile.setObject(true);
    }

    public void addMedkit(int x, int y, String textureName, int textureIndex, int value, String name) {
        Medkit medkit = new Medkit();
        medkit.setValue(value);
        addPickable(medkit, x, y, textureName, textureIndex, name);
    }

    public void addTreasure(int x, int y, String textureName, int textureIndex, int value, String name) {
        Treasure treasure = new Treasure();
        treasure.setValue(value);
        addPickable(treasure, x, y, textureName, textureIndex, name);
    }

    public void addAmmo(int x, int y, String textureName, int textureIndex, int value, String name) {
        Ammo ammo = new Ammo();
        ammo.setValue(value);
        addPickable(ammo, x, y, textureName, textureIndex, name);
    }

    void addPickable(Pickable gameObject, int x, int y, String textureName, int textureIndex, String name) {
        Tile tile = getTileAt(x, y);

        gameObject.setPosition(x, 0.5f, -y);
        gameObject.setSolid(false);
        gameObject.setTrigger(true);
        gameObject.setTextureName(textureName);
        gameObject.setTextureIndex(textureIndex);
        gameObject.setName(name);

        pickables.add(gameObject);
        tile.setObject(true);
    }

    public void addObject(int x, int y, String textureName, int textureIndex, boolean solid, String name) {
        Tile tile = getTileAt(x, y);
        tile.setWalkable(true);
        tile.setTransparent(true);
        GameObject gameObject = new GameObject();
        gameObject.setPosition(x, 0.5f, -y);
        gameObject.setSolid(solid);
        gameObject.setTextureName(textureName);
        gameObject.setTextureIndex(textureIndex);
        gameObject.setName(name);
        objects.add(gameObject);
    }

    private void setPlayerSpawn(Rectangle room) {
        int x = random.next(room.getX(), room.getRight());
        int y = random.next(room.getY(), room.getBottom());
        setPlayerSpawn(x, y);
    }

    private void placeEnemies(Rectangle room) {
        int number = random.next(0, 3);

        while(number > 0) {
            int x = random.next(room.getX(), room.getRight());
            int y = random.next(room.getY(), room.getBottom());

            int enemyType = random.next(0, 3); // guard or dog

            Tile tile = getTileAt(x, y);
            if (tile.isWalkable() && !tile.isObject()) {
                if (enemyType == 0) { // GUARD
                    addGuard(x, y);
                } else { // DOG
                    addDog(x, y);
                }
                number--;
            }
        }
    }

    private void placeMedkits(Rectangle room) {
        int number = random.next(0, 1);

        while(number > 0) {
            int x = random.next(room.getX(), room.getRight());
            int y = random.next(room.getY(), room.getBottom());
            addMedkit(x, y, "textures/objects/food.png", 26, 10, "Food");
            number--;
        }
    }

    private void placeTreasures(Rectangle room) {
        int number = random.next(0, 2);

        while(number > 0) {
            int x = random.next(room.getX(), room.getRight());
            int y = random.next(room.getY(), room.getBottom());
            Tile tile = getTileAt(x, y);
            if (tile.isWalkable() && !tile.isObject()) {
                addTreasure(x, y, "textures/objects/objects.png", 47, 100, "Chalice");
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

    public List<Actor> getGuards() {
        return guards;
    }

    public List<Actor> getDogs() {
        return dogs;
    }

    public List<GameObject> getWalls() {
        return walls;
    }

    public List<Pickable> getPickables() {
        return pickables;
    }

    public List<GameObject> getObjects() {
        return objects;
    }

    public List<GameObject> getDoors() {
        return doors;
    }

}
