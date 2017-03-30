package net.omega2097;

import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardHandler extends GLFWKeyCallback {

    private static boolean[] pressed = new boolean[512];

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            pressed[key] = true;
        } else if (action == GLFW_RELEASE) {
            pressed[key] = false;
        }
    }

    public static boolean isKeyDown(int key) {
        // TODO check for length
        return pressed[key];
    }
}
