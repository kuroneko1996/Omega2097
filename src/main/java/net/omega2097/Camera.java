package net.omega2097;

import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    private Vector3f position = new Vector3f(0,0,0);
    private float pitch;
    private float yaw;
    private float roll;

    private static final float MOUSE_SENSITIVITY = 0.4f;


    public boolean update(MouseInput mouseInput) {
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
        if(KeyboardHandler.isKeyDown(GLFW_KEY_A)){
            position.x -= spd * Math.cos(Math.toRadians(newAngleY));
            position.z -= spd * Math.sin(Math.toRadians(newAngleY));
            updated = true;
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_D)){
            position.x += spd * Math.cos(Math.toRadians(newAngleY));
            position.z += spd * Math.sin(Math.toRadians(newAngleY));
            updated = true;
        }


        Vector2f mouseRotation = mouseInput.getRotation();
        if (updateRotation(mouseRotation.x * MOUSE_SENSITIVITY, mouseRotation.y * MOUSE_SENSITIVITY, 0f)) {
            return true;
        }

        return updated;
    }

    public boolean updateRotation(float offsetX, float offsetY, float offsetZ) {
        pitch = (pitch + offsetY) % 360;
        yaw = (yaw + offsetX) % 360;
        roll = (roll + offsetZ) % 360;

        if (offsetX != 0 || offsetY != 0 || offsetZ != 0) {
            return true;
        }
        return false;
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
