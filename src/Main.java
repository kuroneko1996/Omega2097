
import net.omega2097.Engine;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;

public class Main {
    private long window;
    private GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);

    private float windowWidth = 640;
    private float windowHeight = 480;

    private void init() {
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        window = GLFW.glfwCreateWindow(640, 480, "Omega 2097", 0,0);
        if (window == 0) {
            glfwTerminate();
            throw new RuntimeException("Failed to create GLFW Window");
        }

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
    }

    private void run(Engine engine) {
        float aspectRatio = windowWidth / windowHeight;

        try {
            init();
            engine.init(aspectRatio);
            engine.startGameLoop(window);

            glfwDestroyWindow(window);
        } finally {
            glfwTerminate();
            errorCallback.free();
        }
    }

    public static void main(String[] args) {
        Engine engine = new Engine();
        new Main().run(engine);
    }
}