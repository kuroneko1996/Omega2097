package net.omega2097;

import net.omega2097.map.Map;
import net.omega2097.map.RandomRoomGenerator;
import net.omega2097.map.Tile;
import net.omega2097.util.Random;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import net.omega2097.shaders.StaticShader;
import net.omega2097.util.Util;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

public class Engine {
    public boolean running = false;

    Loader loader = new Loader();
    MeshRenderer renderer;
    StaticShader shader;
    Camera camera;
    Matrix4f viewMatrix;


    List<GameObject> gameObjects = new ArrayList<>();

    private double lastLoopTime;
    private double lastTime;
    private int framesNumber;
    private Window window;
    private StringBuilder windowTitle = new StringBuilder(64);
    private MouseInput mouseInput;

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
        renderer = new MeshRenderer(aspectRatio, shader);
        camera = new Camera();
        camera.setPosition(new Vector3f(0,0.5f,0));
        viewMatrix = Util.createViewMatrix(camera);


        //MapLoader mapLoader = new MapLoader();
        //gameObjects = mapLoader.load("res/test_map.json");
        Random random = new Random(998);
        RandomRoomGenerator<Map> roomGenerator = new RandomRoomGenerator<>(32,32, 10, 6,
                9, random);
        Map map = new Map();
        roomGenerator.createMap(map);
        // generate game objects
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Tile tile = map.getTileAt(x, y);
                if (!tile.isWalkable() && !tile.isTransparent()) {
                    GameObject gameObject = new GameObject("cube", "wall.png");
                    gameObject.setScale(new Vector3f(0.5f, 0.5f, 0.5f)); // TODO fix the model
                    gameObject.setPosition(new Vector3f(y, -1, x));
                    gameObjects.add(gameObject);
                    System.out.print("#");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.print("\n");
        }

        for(GameObject gameObject : gameObjects) {
            Model model = (new ObjLoader()).load("res/" + gameObject.getModelName(), loader);
            model.setTextureID(loader.loadTexture("res/" + gameObject.getTextureName()));
            gameObject.setModel(model);
        }
        System.out.println("Total " + gameObjects.size() + " game objects created");
    }
    private void input() {
        glfwPollEvents();
    }
    private void update(float delta) {
        mouseInput.update();
        if (window.isMouseLocked()) {
            glfwSetCursorPos(window.id, window.width / 2.0f, window.height / 2.0f);
        }

        if (camera.update(mouseInput)) {
            Util.updateViewMatrix(viewMatrix, camera.getPosition(), camera.getPitch(), camera.getYaw());
        }
    }
    private void render() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClearColor(0,0,0,1);
        GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear frame/depth buffer

        for(GameObject gameObject: gameObjects) {
            renderer.render(gameObject, shader, viewMatrix);
        }

        glfwSwapBuffers(window.id);
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
