package net.omega2097;

import net.omega2097.map.Map;
import net.omega2097.map.RandomRoomGenerator;
import net.omega2097.map.Tile;
import net.omega2097.shaders.BillboardShader;
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
    public boolean running = false;

    Loader loader = new Loader();
    MeshRenderer renderer;
    StaticShader shader;
    BillboardShader billboardShader;
    Player player;
    Camera camera;
    Matrix4f viewMatrix;

    private int soldierWalkAnimationMaxFrame = 4;
    private HashMap<GameObject, Float> soldierAnimationCurrentFrame;


    List<GameObject> gameObjects = new ArrayList<>();

    private double lastLoopTime;
    private double lastTime;
    private int framesNumber;
    private Window window;
    private StringBuilder windowTitle = new StringBuilder(64);
    private MouseInput mouseInput;
    private List<GameObject> billboardObjectsToRender = new ArrayList<>();

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
        renderer = new MeshRenderer(aspectRatio);

        camera = new Camera();

        PrimitivesGenerator primGen = new PrimitivesGenerator(loader);

        player = new Player();
        player.setMouseInput(mouseInput);
        BoundingBox pbox = new BoundingBox(new Vector3f(0,0,0), new Vector3f(0.5f,0.7f,0.5f));
        player.setCollider(new Collider(pbox));
        player.setCamera(camera);

        viewMatrix = Util.createViewMatrix(camera);

        Random random = new Random(998);
        RandomRoomGenerator<Map> roomGenerator = new RandomRoomGenerator<>(32,32, 10, 6,
                9, random);
        Map map = new Map(random);
        roomGenerator.createMap(map);
        // generate game objects
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Tile tile = map.getTileAt(x, y);
                if (!tile.isWalkable() && !tile.isTransparent()) {
                    GameObject gameObject = new GameObject();
                    gameObject.setModel(primGen.generateCube());
                    gameObject.setPosition(x, 0, y);
                    gameObject.setTextureName("w_wall1.png");
                    gameObject.getModel().addTextureID(loader.loadTexture("res/" + gameObject.getTextureName()));

                    BoundingBox bbox = new BoundingBox(new Vector3f(gameObject.getPosition()), new Vector3f(1,1,1));
                    gameObject.setCollider(new Collider(bbox));

                    gameObjects.add(gameObject);
                }
            }
        }

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

        addEnemies(map, primGen);

        System.out.println("Total " + gameObjects.size() + " game objects created");

        printMap(map);
        Tile startTile = map.getRandomClearTile();
        System.out.println("Start at " + startTile.getX() + ", " + startTile.getY());
        player.setPosition(startTile.getX(), 0f, startTile.getY());
    }
    private void input() {
        glfwPollEvents();
    }
    private void update(float delta) {
        mouseInput.update();
        if (window.isMouseLocked()) {
            glfwSetCursorPos(window.id, window.width / 2.0f, window.height / 2.0f);
        }

        player.update();

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
            gameObject.update();

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
        //GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glClearColor(0,0,0,1);
        GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear frame/depth buffer

        billboardObjectsToRender.clear();
        for(GameObject gameObject: gameObjects) {
            if (gameObject.isBillboard()) {
                billboardObjectsToRender.add(gameObject);
            } else {
                renderer.render(gameObject, shader, viewMatrix);
            }
        }

        renderBillboards();

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
}
