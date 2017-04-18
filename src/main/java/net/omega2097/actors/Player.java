package net.omega2097.actors;

import net.omega2097.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Actor {
    private MouseInput mouseInput;
    private Camera camera;
    private GameObject gun;

    public Player() {
        super();
        this.shooter = new Shooter(this);
    }

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

    @Override
    public void update(List<GameObject> gameObjects) {
        super.update(gameObjects);

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
        if (KeyboardHandler.isKeyDown(GLFW_KEY_Z)) {
            shooter.shoot(gameObjects);
        }
        if (KeyboardHandler.isKeyDown(GLFW_KEY_X)) {
            System.out.println("pos: " + position + ", dir: " + getDirection());
        }

        Vector2f mouseRotation = mouseInput.getRotation();
        if (camera.updateRotation( mouseRotation.y * Camera.MOUSE_SENSITIVITY, mouseRotation.x * Camera.MOUSE_SENSITIVITY, 0f)) {
            updated = true;
        }

        updateCameraPosition();
        camera.setUpdated(updated);
        updateColliderPosition();
    }

    @Override
    public void setPosition(float x, float y, float z) {
        super.setPosition(x, y, z);
        updateCameraPosition();
        camera.setUpdated(true);
    }

    @Override
    public Vector3f getDirection() {
        Vector3f cameraRot = camera.getRotation();
        float rotY = 180 - cameraRot.y;
        if (rotY > 360) {
            rotY = rotY - 360;
        }
        float rotX = 0;
        float rotZ = 0;

        return new Vector3f(rotX, rotY, rotZ);
    }

    private void updateCameraPosition() {
        camera.setPosition(position.x, position.y, position.z );
    }
}
