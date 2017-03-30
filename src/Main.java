
import net.omega2097.Engine;
import net.omega2097.KeyboardHandler;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;

public class Main {
    private long window;
    private GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
    private GLFWKeyCallback keyCallback;

    private int windowWidth = 640;
    private int windowHeight = 480;

    private void init() {
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        window = GLFW.glfwCreateWindow(windowWidth, windowHeight, "Omega 2097", 0,0);
        glfwSetKeyCallback(window, keyCallback = new KeyboardHandler());
        if (window == 0) {
            glfwTerminate();
            throw new RuntimeException("Failed to create GLFW Window");
        }

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
    }

    private void run(Engine engine) {
        float aspectRatio = (float)windowWidth / (float)windowHeight;

        try {
            init();
            engine.init(aspectRatio);
            engine.startGameLoop(window);

            glfwDestroyWindow(window);
        } finally {
            glfwTerminate();
            keyCallback.free();
            errorCallback.free();
        }
    }

    public static void main(String[] args) {
        Engine engine = new Engine();
        new Main().run(engine);
    }
}