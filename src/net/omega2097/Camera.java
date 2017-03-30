package net.omega2097;

import net.omega2097.util.Util;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    private Vector3f position = new Vector3f(0,0,0);
    private float pitch;
    private float yaw;
    private float roll;


    public boolean update() {
        boolean updated = false;
        float spd = 0.1f;
        float newAngleY = yaw;
        float dx = (float) (spd * Math.sin(Math.toRadians(newAngleY)));
        float dz = (float) (spd * Math.cos(Math.toRadians(newAngleY)));

        if(KeyboardHandler.isKeyDown(GLFW_KEY_W)){
            position.x += dx;
            position.z -= dz;
            updated = true;
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_S)){
            position.x -= dx;
            position.z += dz;
            updated = true;
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_D)){
            newAngleY = (yaw + 2) % 360;
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_A)){
            newAngleY = (yaw - 2) % 360;
        }

        if (newAngleY != yaw) {
            yaw = newAngleY;
            updated = true;
        }

        return updated;
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
