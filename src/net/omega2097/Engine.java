package net.omega2097;

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

        Model levelModel = (new ObjLoader()).load("cube", loader);
        levelModel.setTextureID(loader.loadTexture("res/gray.png"));

        GameObject levelObject = new GameObject(levelModel);
        levelObject.setPosition(new Vector3f(0,0,0));
        gameObjects.add(levelObject);
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
