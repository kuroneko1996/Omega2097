import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class Engine {
    public boolean running = false;

    Loader loader = new Loader();
    ModelRenderer renderer = new ModelRenderer();

    float[] vertices = { -0.5f, 0.5f, 0f, -0.5f, -0.5f, 0f, 0.5f, -0.5f, 0f, 0.5f, -0.5f, 0f, 0.5f, 0.5f, 0f, -0.5f, 0.5f, 0f
    };

    Model model;

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

        model = loader.loadToVAO(vertices);
    }
    private void input() {
        glfwPollEvents();
    }
    private void update(float delta) {
    }
    private void render() {
        GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear frame/depth buffer
        renderer.render(model);

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

        loader.cleanUp();
    }
}
