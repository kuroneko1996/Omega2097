
import net.omega2097.Engine;
import net.omega2097.KeyboardHandler;
import net.omega2097.MouseInput;
import net.omega2097.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;

public class Main {
    private long windowId;
    private Window window;
    private GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
    private GLFWKeyCallback keyCallback;
    private MouseInput mouseInput;

    private int windowWidth = 640;
    private int windowHeight = 480;

    private void init() {
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        windowId = GLFW.glfwCreateWindow(windowWidth, windowHeight, "Omega 2097", 0,0);
        if (windowId == 0) {
            glfwTerminate();
            throw new RuntimeException("Failed to create GLFW Window");
        }
        window = new Window(windowId, windowWidth, windowHeight);

        glfwSetKeyCallback(window.id, keyCallback = new KeyboardHandler());
        mouseInput = new MouseInput();
        mouseInput.init(window);

        GLFW.glfwMakeContextCurrent(window.id);
        GL.createCapabilities();
    }

    private void run(Engine engine) {
        try {
            init();
            engine.init(window, mouseInput);
            engine.startGameLoop();

            glfwDestroyWindow(window.id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            glfwTerminate();
            keyCallback.free();
            mouseInput.cleanUp();
            errorCallback.free();
        }
    }

    public static void main(String[] args) {
        System.out.println("Game Started");
        Engine engine = Engine.getInstance();
        new Main().run(engine);
        System.out.println("Game Quit");
    }
}