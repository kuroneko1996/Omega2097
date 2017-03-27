import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class Engine {
    public boolean running = false;


    double lastLoopTime;
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
    }
    private void input() {
        glfwPollEvents();
    }
    private void update(float delta) {
    }
    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear frame/depth buffer
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
    }
}
