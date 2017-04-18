package net.omega2097;

import net.omega2097.actors.Player;
import net.omega2097.map.Map;
import net.omega2097.map.RandomRoomGenerator;
import net.omega2097.map.Tile;
import net.omega2097.shaders.BillboardShader;
import net.omega2097.shaders.GuiShader;
import net.omega2097.util.PrimitivesGenerator;
import net.omega2097.util.Random;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import net.omega2097.shaders.StaticShader;
import net.omega2097.util.Util;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

public class Engine {
    private static Engine instance;

    private Loader loader = new Loader();
    private MeshRenderer renderer;
    private StaticShader shader;
    private BillboardShader billboardShader;
    private GuiShader guiShader;
    private Player player;
    private Camera camera;
    private Matrix4f viewMatrix;
    private Map map;
    private PrimitivesGenerator primGen;
    private List<GameObject> toRemove = new ArrayList<>();

    private int soldierWalkAnimationMaxFrame = 4;
    private HashMap<GameObject, Float> soldierAnimationCurrentFrame;


    private List<GameObject> gameObjects = new ArrayList<>();

    private double lastLoopTime;
    private double lastTime;
    private int framesNumber;
    private Window window;
    private StringBuilder windowTitle = new StringBuilder(64);
    private MouseInput mouseInput;
    private List<GameObject> billboardObjectsToRender = new ArrayList<>();


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

        float aspectRatio = (float)window.width / (float)window.height;

        lastLoopTime = getTime();
        lastTime = getTime();

        shader = new StaticShader();
        billboardShader = new BillboardShader();
        guiShader = new GuiShader();
        renderer = new MeshRenderer(aspectRatio);
        camera = new Camera();
        primGen = new PrimitivesGenerator(loader);

        viewMatrix = Util.createViewMatrix(camera);

        Random random = new Random(998);
        RandomRoomGenerator<Map> roomGenerator = new RandomRoomGenerator<>(32,32, 10, 6,
                9, random);
        map = new Map(random);
        roomGenerator.createMap(map);

        addFloorAndCeil();
        addWalls();
        addEnemies(map, primGen);
        addPlayer();
        System.out.println("Total " + gameObjects.size() + " game objects created");
        printMap(map);

        Util.updateViewMatrix(viewMatrix, camera.getPosition(), camera.getPitch(), camera.getYaw()); // prevent black screen
    }
    private void input() {
        glfwPollEvents();
    }
    private void update(float delta) {
        removeDestroyedObjects();

        mouseInput.update();
        if (window.isMouseLocked()) {
            glfwSetCursorPos(window.id, window.width / 2.0f, window.height / 2.0f);
        }

        player.update(gameObjects);

        for(GameObject gameObject : gameObjects) {
            Collider collider = gameObject.getCollider();
            if (collider == null) {
                continue;
            }

            Vector3f shiftVector = collider.checkRectanglesOverlap(player.getCollider());
            if (shiftVector != null) {
                Vector3f newPosition = Vector3f.add(player.getPosition(), shiftVector, null);
                player.setPosition(newPosition.x, newPosition.y, newPosition.z);
            }
        }

        for(GameObject gameObject : gameObjects) {
            gameObject.update(gameObjects);

            // update animations
            Float frame = soldierAnimationCurrentFrame.get(gameObject);
            if (frame != null) {
                frame = frame + 0.05f;
                if (frame > soldierWalkAnimationMaxFrame) {
                    frame = 0f;
                }
                soldierAnimationCurrentFrame.put(gameObject, frame);
                gameObject.getModel().setCurrentTexture((int)frame.floatValue());
            }
        }

        if (camera.isUpdated()) {
            Util.updateViewMatrix(viewMatrix, camera.getPosition(), camera.getPitch(), camera.getYaw());
        }
    }
    private void render() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        //GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glClearColor(0,0,0,1);
        GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear frame/depth buffer

        billboardObjectsToRender.clear();
        for(GameObject gameObject: gameObjects) {
            if (gameObject.isBillboard()) {
                billboardObjectsToRender.add(gameObject);
            } else if (!gameObject.isGui()) {
                renderer.render(gameObject, shader, viewMatrix);
            }
        }

        renderBillboards();
        renderGui();

        glfwSwapBuffers(window.id);
    }

    private void renderBillboards() {
        // sort and render billboards
        java.util.Map<Float, GameObject> sortedMap = new TreeMap<>(Comparator.reverseOrder());

        Vector3f cameraPosition = camera.getPosition();
        // calc distance to camera
        for(GameObject gameObject: billboardObjectsToRender) {
            Vector3f position = gameObject.getPosition();
            float distance = (float)Math.sqrt( (cameraPosition.x - position.x) * (cameraPosition.x - position.x)
                    + (cameraPosition.z - position.z) * (cameraPosition.z - position.z)
            );
            sortedMap.put(distance, gameObject);
        }

        sortedMap.forEach((k, gameObject) -> {
            renderer.renderBillBoard(gameObject, billboardShader, viewMatrix);
        });
    }

    private void renderGui() {
        for(GameObject gameObject: gameObjects) {
            if (gameObject.isGui()) {
                renderer.renderGui(gameObject, guiShader, viewMatrix);
            }
        }
    }

    private void addWalls() {
        // generate game objects
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Tile tile = map.getTileAt(x, y);
                if (!tile.isWalkable() && !tile.isTransparent()) {
                    GameObject gameObject = new GameObject();
                    gameObject.setModel(primGen.generateCube(1));
                    gameObject.setPosition(x, 0.5f, y);
                    gameObject.setTextureName("w_wall1.png");
                    gameObject.getModel().addTextureID(loader.loadTexture("res/" + gameObject.getTextureName()));

                    Vector3f bboxSize = new Vector3f(1,1,1);
                    Vector3f bboxCoord = new Vector3f(gameObject.getPosition().x - 0.5f,
                            gameObject.getPosition().y - 0.5f,gameObject.getPosition().z - 0.5f);
                    BoundingBox bbox = new BoundingBox(bboxCoord, bboxSize);
                    gameObject.setCollider(new Collider(bbox));

                    gameObjects.add(gameObject);
                }
            }
        }
    }

    private void addFloorAndCeil() {
        // Make floor and ceil
        GameObject floor = new GameObject();
        floor.setModel(primGen.generateHorizontalQuad(32, 32));
        floor.setPosition(0,0,0);
        floor.setScale(new Vector3f(32, 1, 32));
        floor.setTextureName("w_floor1.png");
        floor.getModel().addTextureID(loader.loadTexture("res/" + floor.getTextureName()));

        GameObject ceil = new GameObject();
        ceil.setModel(primGen.generateHorizontalQuad(32, 32));
        ceil.setPosition(0, 1,0);
        ceil.setScale(new Vector3f(32, 1, 32));
        ceil.setTextureName("w_ceil1.png");
        ceil.getModel().addTextureID(loader.loadTexture("res/" + ceil.getTextureName()));

        gameObjects.add(floor);
        gameObjects.add(ceil);
    }

    private void addPlayer() {
        player = new Player();
        player.setMouseInput(mouseInput);
        Vector3f bboxSize = new Vector3f(0.6f,0.8f,0.6f);
        Vector3f bboxCoord = new Vector3f(-0.5f, -0.5f, -0.5f);
        BoundingBox pbox = new BoundingBox(bboxCoord, bboxSize);
        player.setCollider(new Collider(pbox));
        player.setCamera(camera);

        Tile startTile = map.getRandomClearTile();
        System.out.println("Start at " + startTile.getX() + ", " + startTile.getY());
        player.setPosition(startTile.getX() + 0.5f, 0.5f, startTile.getY() + 0.5f);

        GameObject gun = new GameObject();
        gun.setModel(primGen.generateVerticalQuad(1,1));
        gun.setGui(true);
        gun.setTextureName("textures/gui/weapons/pistol.png");
        gun.getModel().addTextureID(loader.loadTexture("res/" + gun.getTextureName()));
        gameObjects.add(gun);
        player.setGun(gun);
    }

    private void addEnemies(Map map, PrimitivesGenerator primGen) {
        soldierAnimationCurrentFrame = new HashMap<>();
        List<Integer> soldierWalkAnimationTextures = new ArrayList<>();

        for (int ti = 0; ti < (soldierWalkAnimationMaxFrame + 1); ti++) {
            soldierWalkAnimationTextures.add(loader.loadTexture("res/" + "textures/soldier/" + "walk_" + ti + ".png" ));
        }

        for (int i = 0; i < map.getEnemies().size(); i++) {
            GameObject enemy = map.getEnemies().get(i);
            enemy.setModel(primGen.generateVerticalQuad(1,1));
            enemy.setBillboard(true);
            enemy.setTextureName("textures/soldier/walk_0.png");

            for (int ti = 0; ti < (soldierWalkAnimationMaxFrame + 1); ti++) {
                enemy.getModel().addTextureID(soldierWalkAnimationTextures.get(ti));
            }

            Vector3f bSize = new Vector3f(0.4f, 0.8f, 0.4f);
            Vector3f bCenter = new Vector3f(enemy.getPosition().x - 0.5f, enemy.getPosition().y,
                    enemy.getPosition().z - 0.5f);
            BoundingBox bbox = new BoundingBox(bCenter, bSize);
            enemy.setCollider(new Collider(bbox));
            gameObjects.add(enemy);
            soldierAnimationCurrentFrame.put(enemy, 0f);
        }
    }

    private void printMap(Map map) {
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
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
            }
        }
        gameObjects.removeAll(toRemove);
        toRemove.clear();
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

        shader.cleanUp();
        loader.cleanUp();
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public Loader getLoader() {
        return loader;
    }

    public PrimitivesGenerator getPrimGen() {
        return primGen;
    }
}
