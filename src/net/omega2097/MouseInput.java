package net.omega2097;

import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {
    private double prevX;
    private double prevY;
    private double x;
    private double y;
    private boolean inWindow;
    private Vector2f rotation;
    private Window window;

    private GLFWCursorPosCallback cursorPosCallback;
    private GLFWCursorEnterCallback cursorEnterCallback;
    private GLFWMouseButtonCallback mouseButtonCallback;

    public Vector2f getRotation() {
        return rotation;
    }

    public MouseInput() {
        prevX = 0;
        prevY = 0;

        inWindow = true;
        rotation = new Vector2f(0,0);
    }

    public void init(Window window) {
        this.window = window;
        glfwSetCursorPosCallback(window.id, cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                x = xpos;
                y = ypos;
            }
        });
        glfwSetCursorEnterCallback(window.id, cursorEnterCallback = new GLFWCursorEnterCallback() {
            @Override
            public void invoke(long window, boolean entered) {
                inWindow = true;
            }
        });
        glfwSetMouseButtonCallback(window.id, mouseButtonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long windowId, int button, int action, int mods) {
                if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                    window.setMouseLocked(true);
                }
                if (button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS) {
                    window.setMouseLocked(false);
                }
            }
        });
    }

    public void update() {
        if (prevX > 0 && prevY > 0 && inWindow) {
            double deltaX;
            double deltaY;
            if (window.isMouseLocked()) {
                deltaX = x - window.width / 2.0f;
                deltaY = y - window.height / 2.0f;
            } else {
                deltaX = x - prevX;
                deltaY = y - prevY;
            }

            rotation.x = (float) deltaX;
            rotation.y = (float) deltaY;
        }
        prevX = x;
        prevY = y;
    }

    public void cleanUp() {
        cursorPosCallback.free();
        cursorEnterCallback.free();
        mouseButtonCallback.free();
    }
}