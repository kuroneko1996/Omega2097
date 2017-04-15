package net.omega2097;

import org.lwjgl.util.vector.Vector3f;

public class Camera {
    private Vector3f position = new Vector3f(0,0,0);
    private float pitch;
    private float yaw;
    private float roll;
    private boolean updated = false;

    public static final float MOUSE_SENSITIVITY = 0.4f;


    public boolean updateRotation(float offsetX, float offsetY, float offsetZ) {
        pitch = (pitch + offsetY) % 360;
        if (pitch > 90) {
            pitch = 90;
        } else if (pitch < -90) {
            pitch = -90;
        }
        yaw = (yaw + offsetX) % 360;
        roll = (roll + offsetZ) % 360;

        if (offsetX != 0 || offsetY != 0 || offsetZ != 0) {
            return true;
        }
        return false;
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
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

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }
}
