import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import shaders.StaticShader;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

public class Engine {
    public boolean running = false;

    Loader loader = new Loader();
    MeshRenderer renderer = new MeshRenderer();
    StaticShader shader;

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
    void init() {
        lastLoopTime = getTime();

        Model carModel = ObjLoader.load("car", loader);
        GameObject car1 = new GameObject(carModel);
        car1.setRotation(new Vector3f(90,0,0));
        gameObjects.add(car1);

        shader = new StaticShader();
    }
    private void input() {
        glfwPollEvents();
    }
    private void update(float delta) {
    }
    private void render() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClearColor(0,0,0,1);
        GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear frame/depth buffer

        for(GameObject gameObject: gameObjects) {
            renderer.render(gameObject, shader);
        }

        glfwSwapBuffers(window);

    }

    void startGameLoop(long window) {
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
