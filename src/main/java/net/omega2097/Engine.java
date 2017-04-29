package net.omega2097;

import net.omega2097.actors.Actor;
import net.omega2097.actors.EnemyAi;
import net.omega2097.actors.Player;
import net.omega2097.gui.BitmapFont;
import net.omega2097.gui.Hud;
import net.omega2097.gui.TextItem;
import net.omega2097.map.Map;
import net.omega2097.map.MapLoader;
import net.omega2097.map.RandomRoomGenerator;
import net.omega2097.map.Tile;
import net.omega2097.renderers.BatchRenderer;
import net.omega2097.renderers.BillBoardsRenderer;
import net.omega2097.shaders.BillboardShader;
import net.omega2097.util.PrimitivesGenerator;
import net.omega2097.util.Random;
import net.omega2097.util.Util;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import net.omega2097.shaders.StaticShader;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

public class Engine {
    private static Engine instance;

    private Loader loader = new Loader();
    private BatchRenderer batchRenderer;
    private BillBoardsRenderer billBoardsRenderer;

    private Hud hud;

    private StaticShader shader;
    private BillboardShader billboardShader;
    private Player player;
    private Camera camera;
    private Matrix4f viewMatrix;
    private Map map;
    private PrimitivesGenerator primGen;
    private List<GameObject> toRemove = new ArrayList<>();

    private List<GameObject> gameObjects = new ArrayList<>();

    private double lastLoopTime;
    private double lastTime;
    private int framesNumber;
    private Window window;
    private StringBuilder windowTitle = new StringBuilder(64);
    private MouseInput mouseInput;

    private java.util.Map<Model, List<GameObject>> staticEntities = new HashMap<>();
    private java.util.Map<Model, List<GameObject>> billboardEntities = new HashMap<>();

    private Game game;

    private Engine() {}
    public static Engine getInstance() {
        if (instance == null) {
            instance = new Engine();
        }
        return instance;
    }

    private double getTime() {
        return glfwGetTime();
    }
    private float getDelta() {
        double time = getTime();
        float delta = (float)(time - lastLoopTime);
        lastLoopTime = time;
        return delta;
    }
    public void init(Window window, MouseInput mouseInput) {
        this.mouseInput = mouseInput;
        this.window = window;

        lastLoopTime = getTime();
        lastTime = getTime();

        // init shaders and renderers
        shader = new StaticShader();
        billboardShader = new BillboardShader();
        batchRenderer = new BatchRenderer((float)window.width, (float)window.height);
        billBoardsRenderer = new BillBoardsRenderer((float)window.width, (float)window.height);
        hud = new Hud((float)window.width, (float)window.height);

        camera = new Camera();
        primGen = new PrimitivesGenerator(loader);

        viewMatrix = Util.createViewMatrix(camera);

        game = new Game();
        game.setLives(3);
        game.setState(Game.GameState.INIT);

        Util.updateViewMatrix(viewMatrix, camera.getPosition(), camera.getPitch(), camera.getYaw()); // prevent black screen
    }
    private void input() {
        glfwPollEvents();
    }
    private void update(float delta) {

        switch (game.getState()) {
            case INIT:
            case RESTART_LEVEL:
                loadLevel();
                game.setState(Game.GameState.PLAY);
                break;
            case OVER:
                System.out.println("GAME OVER");
                game.setState(Game.GameState.MENU);
                break;
            case EXIT:
                break;
            case MENU:
                break;
            case PLAY:
                playGame();
                break;
        }

        mouseInput.update();
        if (window.isMouseLocked()) {
            glfwSetCursorPos(window.id, window.width / 2.0f, window.height / 2.0f);
        }
    }

    private void prepareRenderLists() {
        staticEntities.clear();
        billboardEntities.clear();

        for(GameObject gameObject: gameObjects) {
            Model model = gameObject.getModel();

            java.util.Map<Model, List<GameObject>> currentStorage;

            if (model.isBillboard()) {
                currentStorage = billboardEntities;
            } else {
                currentStorage = staticEntities;
            }

            List<GameObject> batch = currentStorage.get(model);
            if (batch == null) {
                List<GameObject> newBatch = new ArrayList<>();
                newBatch.add(gameObject);
                currentStorage.put(model, newBatch);
            } else {
                batch.add(gameObject);
            }
        }
    }

    private void render() {
        //GL11.glFrontFace(GL11.GL_CW);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        //GL11.glEnable(GL11.GL_CULL_FACE);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glClearColor(0,0,0,1);
        GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear frame/depth buffer

        // render 'static' objects
        batchRenderer.setViewMatrix(viewMatrix);
        batchRenderer.setShader(shader);
        batchRenderer.setRenderEntities(staticEntities);
        batchRenderer.render();

        renderBillboards();

        // render gui
        hud.render();

        glfwSwapBuffers(window.id);
    }

    private void renderBillboards() {
        // sort and render billboards
        java.util.TreeMap<Float, GameObject> sortedMap = new TreeMap<>(Comparator.reverseOrder());

        Vector3f cameraPosition = camera.getPosition();
        // calc distance to camera

        for (Model model : billboardEntities.keySet()) {
            List<GameObject> batch = billboardEntities.get(model);
            for (GameObject gameObject : batch) {
                Vector3f position = gameObject.getPosition();
                float distance = (float) Math.sqrt((cameraPosition.x - position.x) * (cameraPosition.x - position.x)
                        + (cameraPosition.z - position.z) * (cameraPosition.z - position.z)
                );
                sortedMap.put(distance, gameObject);
            }
        }

        billBoardsRenderer.setViewMatrix(viewMatrix);
        billBoardsRenderer.setBillboardShader(billboardShader);
        billBoardsRenderer.setBatch(sortedMap);
        billBoardsRenderer.render();
    }


    private void loadLevel() {
        toRemove.clear();
        gameObjects.clear();
        hud.clear();
        player = null;

        loader.cleanUp();

        camera = new Camera();

        Random random = new Random(998);
        RandomRoomGenerator<Map> roomGenerator = new RandomRoomGenerator<>(32,32, 10, 6,
                9, random);

        MapLoader mapLoader = new MapLoader(random);
        map = mapLoader.load("res/maps/level1.png");

        addFloorAndCeil();
        addWalls();
        addDoors();
        addObjects();
        addGuards(map, primGen);
        addDogs(map, primGen);
        addPickables(map, primGen);
        addPlayer();
        addHud();
        System.out.println("Total " + gameObjects.size() + " game objects have been created");
        printMap(map);

        Util.updateViewMatrix(viewMatrix, camera.getPosition(), camera.getPitch(), camera.getYaw()); // prevent black screen

        prepareRenderLists();
    }

    private void setupGameObject(GameObject gameObject, java.util.Map<String, Model> textures) {
        String textureName = gameObject.getTextureName();
        String textureKey = textureName + gameObject.getTextureIndex();
        Model model = textures.get(textureKey);
        if (model == null) {
            Texture texture = loader.loadTexture("res/" + textureName);
            float[] uv = Util.calcUVinAtlas(texture.getWidth(), 8, gameObject.getTextureIndex());
            model = primGen.generateVerticalQuad(1, 1, 0, uv[0], uv[1], uv[2], uv[3]);
            model.setBillboard(true);
            model.addTexture(texture);
            textures.put(textureKey, model);
        }
        gameObject.setModel(model);

        if (gameObject.isTrigger()) {
            Vector3f bboxSize = new Vector3f(1, 1, 1);
            Vector3f bboxCoord = new Vector3f(gameObject.getPosition().x - 0.5f,
                    gameObject.getPosition().y - 0.5f, gameObject.getPosition().z - 0.5f);
            BoundingBox bbox = new BoundingBox(bboxCoord, bboxSize);
            gameObject.setCollider(new Collider(bbox));
        }
    }

    private void addObjects() {
        java.util.Map<String, Model> textures = new HashMap<>();
        for (int i = 0; i < map.getObjects().size(); i++) {
            GameObject gameObject = map.getObjects().get(i);
            setupGameObject(gameObject, textures);
            gameObjects.add(gameObject);
        }
    }

    private void addWalls() {
        java.util.Map<String, Model> textures = new HashMap<>();

        for (int i = 0; i < map.getWalls().size(); i++) {
            GameObject wall = map.getWalls().get(i);

            String textureName = wall.getTextureName();
            String textureKey = textureName + wall.getTextureIndex();
            Model model = textures.get(textureKey);
            if (model == null) {
                Texture texture = loader.loadTexture("res/" + textureName);
                float[] uv = Util.calcUVinAtlas(texture.getWidth(), 2, wall.getTextureIndex());
                model = primGen.generateCube(1, uv[0], uv[1], uv[2], uv[3]);
                model.addTexture(texture);
                textures.put(textureKey, model);
            }
            wall.setModel(model);

            Vector3f bboxSize = new Vector3f(1,1,1);
            Vector3f bboxCoord = new Vector3f(wall.getPosition().x - 0.5f,
                    wall.getPosition().y - 0.5f,wall.getPosition().z - 0.5f);
            BoundingBox bbox = new BoundingBox(bboxCoord, bboxSize);
            wall.setCollider(new Collider(bbox));

            gameObjects.add(wall);
        }
    }

    private void addDoors() {
        java.util.Map<String, Model> textures = new HashMap<>();

        for (int i = 0; i < map.getDoors().size(); i++) {
            GameObject door = map.getDoors().get(i);

            String textureName = door.getTextureName();
            Model model = textures.get(textureName);
            if (model == null) {
                model = primGen.generateCube(1);
                model.addTexture(loader.loadTexture("res/" + textureName));
                textures.put(textureName, model);
            }
            door.setModel(model);

            Vector3f bboxSize = new Vector3f(1,1,1);
            Vector3f bboxCoord = new Vector3f(door.getPosition().x - 0.5f,
                    door.getPosition().y - 0.5f,door.getPosition().z - 0.5f);
            BoundingBox bbox = new BoundingBox(bboxCoord, bboxSize);
            door.setCollider(new Collider(bbox));

            gameObjects.add(door);
        }
    }

    private void addFloorAndCeil() {
        // Make floor and ceil
        GameObject floor = new GameObject();
        floor.setModel(primGen.generateHorizontalQuad(map.getWidth(), map.getHeight()));
        floor.setPosition(0,0,-map.getHeight());
        floor.setScale(map.getWidth(), 1, map.getHeight());
        floor.setTextureName("w_floor1.png");
        floor.getModel().addTexture(loader.loadTexture("res/" + floor.getTextureName()));

        GameObject ceil = new GameObject();
        ceil.setModel(primGen.generateHorizontalQuad(map.getWidth(), map.getHeight()));
        ceil.setPosition(0, 1,-map.getHeight());
        ceil.setScale(map.getWidth(), 1, map.getHeight());
        ceil.setTextureName("w_ceil1.png");
        ceil.getModel().addTexture(loader.loadTexture("res/" + ceil.getTextureName()));

        gameObjects.add(floor);
        gameObjects.add(ceil);
    }

    private void addPlayer() {
        player = new Player();
        player.setName("Player");
        player.setMouseInput(mouseInput);
        player.getHealth().setMax(100f).setCurrent(100f);
        player.getAmmo().setMax(999).setCurrent(10);

        Vector3f bboxSize = new Vector3f(0.5f,0.8f,0.5f);
        Vector3f bboxCoord = new Vector3f(-0.5f, -0.5f, -0.5f);
        BoundingBox pbox = new BoundingBox(bboxCoord, bboxSize);
        player.setCollider(new Collider(pbox));
        player.setCamera(camera);

        Vector2f spawnPos = map.getPlayerSpawn();
        System.out.println("Start at " + spawnPos.getX() + ", " + spawnPos.getY());
        player.setPosition(spawnPos.getX() + 0.5f, 0.5f, -spawnPos.getY() + 0.5f);

    }

    private void addHud() {
        float screenCenterX = window.width / 2f;
        float screenCenterY = window.height / 2f;
        float xScale = (float)window.height / (float)window.width;
        float yScale = (float)window.width / (float)window.height;

        float gunTextureSize = 64;
        float gunTextureSizeX = gunTextureSize * 2 * xScale;
        float gunTextureSizeY = gunTextureSize * 2 * yScale;
        float gunX1 = screenCenterX - gunTextureSizeX;
        float gunX2 = screenCenterX + gunTextureSizeX;
        float gunY1 = 80;
        float gunY2 = 80 + gunTextureSizeY;

        GameObject gun = new GameObject();
        gun.setModel(primGen.generateRectangle(gunX1, gunY1,
                gunX2, gunY2, 0));
        gun.setTextureName("textures/gui/weapons/pistol.png");
        gun.getModel().addTexture(loader.loadTexture("res/" + gun.getTextureName()));

        hud.add(gun);
        player.setGun(gun);

        GameObject hudPanel = new GameObject();
        hudPanel.setModel(primGen.generateRectangle(0,0, window.width, 80, 0, 0, 280f/320f, 1, 1));
        hudPanel.setTextureName("textures/gui/hud.png");
        hudPanel.getModel().addTexture(loader.loadTexture("res/" + hudPanel.getTextureName()));
        hud.add(hudPanel);


        float levelTextX = 35;
        float levelTextY = 20;
        float textSize = 1f;
        BitmapFont font = new BitmapFont("res/textures/gui/fonts/ExportedFont.png", 16, 16, loader);

        TextItem levelText = new TextItem(levelTextX, levelTextY, String.format("%02d", game.getLevel()),
                font, "ISO-8859-1", textSize, loader);
        hud.add(levelText);

        TextItem scoreText = new TextItem(levelTextX + 60, levelTextY, String.format("%06d",0),
                font, "ISO-8859-1", textSize, loader);
        hud.add(scoreText);

        TextItem livesText = new TextItem(levelTextX + 180, levelTextY, String.format("%02d",game.getLives()),
                font, "ISO-8859-1", textSize, loader);
        hud.add(livesText);

        TextItem healthText = new TextItem(levelTextX + 300, levelTextY, "000",
                font, "ISO-8859-1", textSize, loader);
        hud.add(healthText);
        hud.setPlayerHealth(healthText);

        TextItem ammoText = new TextItem(levelTextX + 385, levelTextY, "000",
                font, "ISO-8859-1", textSize, loader);
        hud.add(ammoText);
        hud.setPlayerAmmo(ammoText);
        hud.setUpdated(true);
    }

    private void addDogs(Map map, PrimitivesGenerator primGen) {
        String[] textureNames = {
                "idle.png", "run_0.png", "run_1.png", "run_2.png",
                "attack_0.png", "attack_1.png", "attack_2.png",
                "dying_0.png", "dying_1.png", "dying_2.png",
                "dead.png"
        };
        Texture[] textures = new Texture[textureNames.length];
        for (int i = 0; i < textureNames.length; i++) {
            textures[i] = loader.loadTexture("res/" + "textures/dog/" + textureNames[i] );
        }
        Animation[] animations = new Animation[]{
                new Animation(new int[]{0}, true, 250), // idle
                new Animation(new int[]{1, 2, 0, 3}, true, 250), // chasing
                new Animation(new int[]{4, 5, 6}, true, 500), // attacking
                new Animation(new int[]{7, 8, 9}, false, 250), // dying
                new Animation(new int[]{10}, false, 250) // dead
        };

        for (int i = 0; i < map.getDogs().size(); i++) {
            Actor enemy = map.getDogs().get(i);
            enemy.setName("Dog " + i);
            enemy.setModel(primGen.generateVerticalQuad(1,1));
            enemy.getModel().setBillboard(true);
            enemy.setTextureName("textures/dog/idle.png");
            EnemyAi enemyAi = (EnemyAi)enemy.getAi();
            enemyAi.setAnimations(animations);

            for(Texture texture : textures) {
                enemy.getModel().addTexture(texture);
            }

            Vector3f bSize = new Vector3f(0.4f, 0.8f, 0.4f);
            Vector3f bCenter = new Vector3f(enemy.getPosition().x - 0.2f, enemy.getPosition().y - 0.4f,
                    enemy.getPosition().z - 0.2f);
            BoundingBox bbox = new BoundingBox(bCenter, bSize);
            enemy.setCollider(new Collider(bbox));
            gameObjects.add(enemy);
        }
    }

    private void addGuards(Map map, PrimitivesGenerator primGen) {
        String[] textureNames = {
                "idle.png", "walk_0.png", "walk_1.png", "walk_2.png", "walk_3.png",
                "attack_0.png", "attack_1.png",
                "dying_0.png", "dying_1.png", "dying_2.png","dying_3.png",
                "dead.png"
        };
        Texture[] textures = new Texture[textureNames.length];
        for (int i = 0; i < textureNames.length; i++) {
            textures[i] = loader.loadTexture("res/" + "textures/guard/" + textureNames[i] );
        }

        Animation[] animations = new Animation[]{
                new Animation(new int[]{0}, true, 250), // idle
                new Animation(new int[]{1, 2, 3, 4}, true, 250), // chasing
                new Animation(new int[]{5, 6}, true, 500), // attacking
                new Animation(new int[]{7, 8, 9, 10}, false, 250), // dying
                new Animation(new int[]{11}, false, 250) // dead
        };

        for (int i = 0; i < map.getGuards().size(); i++) {
            Actor enemy = map.getGuards().get(i);
            enemy.setName("Guard " + i);
            enemy.setModel(primGen.generateVerticalQuad(1,1));
            enemy.getModel().setBillboard(true);
            enemy.setTextureName("textures/guard/walk_0.png");
            EnemyAi enemyAi = (EnemyAi)enemy.getAi();
            enemyAi.setAnimations(animations);
            enemy.getShooter().setShootDelay(animations[2].getFrameDuration() * animations[2].getTotalFrames());

            for(Texture texture : textures) {
                enemy.getModel().addTexture(texture);
            }

            Vector3f bSize = new Vector3f(0.4f, 0.8f, 0.4f);
            Vector3f bCenter = new Vector3f(enemy.getPosition().x - 0.2f, enemy.getPosition().y - 0.4f,
                    enemy.getPosition().z - 0.2f);
            BoundingBox bbox = new BoundingBox(bCenter, bSize);
            enemy.setCollider(new Collider(bbox));
            gameObjects.add(enemy);
        }
    }

    private void addPickables(Map map, PrimitivesGenerator primGen) {
        java.util.Map<String, Model> textures = new HashMap<>();
        for (int i = 0; i < map.getPickables().size(); i++) {
            GameObject gameObject = map.getPickables().get(i);
            setupGameObject(gameObject, textures);
            gameObjects.add(gameObject);
        }
    }

    private void printMap(Map map) {
        for (int x = map.getWidth() - 1; x >= 0; x--) {
            for (int y = map.getHeight() - 1; y >=0; y--) {
                Tile tile = map.getTileAt(x, y);
                if (tile.isObject()) {
                    System.out.print("o");
                } else if (!tile.isWalkable()) {
                    System.out.print("#");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.print("\n");
        }
    }

    private void removeDestroyedObjects() {
        for(GameObject gameObject : gameObjects) {
            if (gameObject.isDestroyed()) {
                toRemove.add(gameObject);
                Model model = gameObject.getModel();
                if (model == null) continue;

                // remove it from render listss
                if (model.isBillboard()) {
                    if (billboardEntities.get(model) != null) {
                        billboardEntities.get(model).remove(gameObject);
                    }
                }
            }
        }
        gameObjects.removeAll(toRemove);
        toRemove.clear();
    }

    private void updateCollisions() {
        resolveCollisions(player);

        for (GameObject gameObject : gameObjects) {
            if (!gameObject.isSolid()) continue;

            if (gameObject instanceof Actor) {
                resolveCollisions((Actor) gameObject);
            }
        }
    }

    private void resolveCollisions(Actor actor) {
        for(GameObject gameObject : gameObjects) {
            if (actor == gameObject) continue;

            Collider collider = gameObject.getCollider();
            if (collider == null) {
                continue;
            }

            Vector3f shiftVector = collider.checkRectanglesOverlap(actor.getCollider());
            if (shiftVector != null) {
                if (gameObject.isSolid()) {
                    Vector3f newPosition = Vector3f.add(actor.getPosition(), shiftVector, null);
                    actor.setPosition(newPosition.x, newPosition.y, newPosition.z);
                }
                gameObject.onTriggerEnter(actor.getCollider());
            }
        }
    }

    private void playGame() {
        removeDestroyedObjects();
        player.update();
        updateCollisions();

        int totalGameObjects = gameObjects.size();
        for (int i = 0; i < totalGameObjects; i++) {
            if (gameObjects.get(i).isDestroyed()) continue;
            gameObjects.get(i).update();
        }

        if (camera.isUpdated()) {
            Util.updateViewMatrix(viewMatrix, camera.getPosition(), camera.getPitch(), camera.getYaw());
        }

        hud.update();
    }

    public void startGameLoop() {
        //long targetTime = 1000 / 60; // 60 fps

        while (!glfwWindowShouldClose(window.id)) {
            float delta = getDelta();

            input();
            update(delta);
            render();

            // calculates fps and ms
            framesNumber++;
            if (getTime() - lastTime >= 1.0) {
                windowTitle.delete(0, windowTitle.length());
                windowTitle.append("FPS: ").append(framesNumber).append("  ").append(1.0f/framesNumber)
                           .append(" ms");
                glfwSetWindowTitle(window.id, windowTitle.toString());
                lastTime += 1.0;
                framesNumber = 0;
            }
        }

        billboardShader.cleanUp();
        shader.cleanUp();
        hud.cleanup();
        loader.cleanUp();
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public void addGameObject(GameObject gameObject) {
        gameObjects.add(gameObject);
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public Hud getHud() { return hud; }
}
