package net.omega2097;

import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardHandler extends GLFWKeyCallback {
    private static final int MAX_KEYS = 512;
    private static boolean[] pressed = new boolean[MAX_KEYS];

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (key < 0 || key >= MAX_KEYS) {
            return;
        }
        if (action == GLFW_PRESS) {
            pressed[key] = true;
        } else if (action == GLFW_RELEASE) {
            pressed[key] = false;
        }
    }

    public static boolean isKeyDown(int key) {
        if (key < 0 || key >= MAX_KEYS) {
            return false;
        }
        return pressed[key];
    }
}
