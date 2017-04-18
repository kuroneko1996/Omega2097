package net.omega2097;

import org.lwjgl.util.vector.Vector3f;

public class Camera {
    private Vector3f position = new Vector3f(0,0,0);
    private float pitch; // x
    private float yaw; // y
    private float roll; // z
    private boolean updated = false;

    public static final float MOUSE_SENSITIVITY = 0.4f;
    public static final float MAX_PITCH = 65;


    public boolean updateRotation(float offsetX, float offsetY, float offsetZ) {
        pitch = (pitch + offsetX) % 360;
        if (pitch > MAX_PITCH) {
            pitch = MAX_PITCH;
        } else if (pitch < -MAX_PITCH) {
            pitch = -MAX_PITCH;
        }
        yaw = (yaw + offsetY) % 360;
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

    public Vector3f getRotation() {
        return new Vector3f(pitch, yaw, roll);
    }
}
