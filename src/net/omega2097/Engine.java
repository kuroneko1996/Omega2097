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

    List<GameObject> gameObjects = new ArrayList<>();

    private double lastLoopTime;
    private long window;

    private double getTime() {
        return glfwGetTime();
    }
    private float getDelta() {
        double time = getTime();
        float delta = (float)(time - lastLoopTime);
        lastLoopTime = time;
        return delta;
    }
    public void init(float aspectRatio) {
        lastLoopTime = getTime();

        shader = new StaticShader();
        renderer = new MeshRenderer(aspectRatio, shader);
        camera = new Camera();
        camera.setPosition(new Vector3f(0,0.5f,0));

        Model carModel = ObjLoader.load("car", loader);
        GameObject car1 = new GameObject(carModel);
        car1.setPosition(new Vector3f(0,0,-5));
        gameObjects.add(car1);
    }
    private void input() {
        glfwPollEvents();
    }
    private void update(float delta) {
        camera.update();
    }
    private void render() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClearColor(0,0,0,1);
        GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear frame/depth buffer

        Matrix4f viewMatrix = Util.createViewMatrix(camera);

        for(GameObject gameObject: gameObjects) {
            renderer.render(gameObject, shader, viewMatrix);
        }

        glfwSwapBuffers(window);

    }

    public void startGameLoop(long window) {
        this.window = window;
        //long targetTime = 1000 / 60; // 60 fps

        while (!glfwWindowShouldClose(window)) {
            float delta = getDelta();

            input();
            update(delta);
            render();
        }

        shader.cleanUp();
        loader.cleanUp();
    }
}
