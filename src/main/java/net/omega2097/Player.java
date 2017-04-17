package net.omega2097;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends GameObject {
    private MouseInput mouseInput;
    private Camera camera;
    private GameObject gun;

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public MouseInput getMouseInput() {
        return mouseInput;
    }

    public void setMouseInput(MouseInput mouseInput) {
        this.mouseInput = mouseInput;
    }

    public GameObject getGun() {
        return gun;
    }

    public void setGun(GameObject gun) {
        this.gun = gun;
    }

    public void update() {
        boolean updated = false;
        camera.setUpdated(false);

        float spd = 0.1f;
        float newAngleY = camera.getYaw();
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
        if (camera.updateRotation(mouseRotation.x * Camera.MOUSE_SENSITIVITY, mouseRotation.y * Camera.MOUSE_SENSITIVITY, 0f)) {
            updated = true;
        }

        updateCameraPosition();
        camera.setUpdated(updated);

        updateGunPosition();

        // move collider with player
        collider.setPosition(position.x, position.y, position.z);
    }

    @Override
    public void setPosition(float x, float y, float z) {
        super.setPosition(x, y, z);
        updateCameraPosition();
        camera.setUpdated(true);

        updateGunPosition();

        collider.setPosition(x, y, z);
    }

    public void updateCameraPosition() {
        camera.setPosition(position.x + 0.5f, position.y+0.5f, position.z + 0.5f );
    }

    public void updateGunPosition() {
    }
}
