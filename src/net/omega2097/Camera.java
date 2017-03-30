package net.omega2097;

import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    private Vector3f position = new Vector3f(0,0,0);
    private float pitch;
    private float yaw;
    private float roll;


    public void update() {
        if(KeyboardHandler.isKeyDown(GLFW_KEY_W)){
            position.z -= 0.1f;
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_S)){
            position.z += 0.1f;
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_D)){
            position.x += 0.1f;
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_A)){
            position.x -= 0.1f;
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_RIGHT)){
            yaw = (yaw + 2) % 360;
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_LEFT)){
            yaw = (yaw - 2) % 360;
        }
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
}
