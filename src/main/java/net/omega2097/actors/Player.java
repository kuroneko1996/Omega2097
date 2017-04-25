package net.omega2097.actors;

import net.omega2097.*;
import net.omega2097.util.Util;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Actor {
    private MouseInput mouseInput;
    private Camera camera;
    private GameObject gun;

    public Player() {
        super();
        this.shooter = new Shooter(this);
        setSolid(true);
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
    public void update() {
        super.update();
        if (getHealth().getCurrent() <= 0) {
            System.out.println("You have been killed");
            Game game =  Engine.getInstance().getGame();
            game.setLives(game.getLives() - 1);
            if (game.getLives() > 0) {
                game.setState(Game.GameState.RESTART_LEVEL);
            } else {
                game.setState(Game.GameState.OVER);
            }
            return;
        }

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
        if (KeyboardHandler.isKeyDown(GLFW_KEY_Z)
                || (mouseInput.isButtonDown(GLFW_MOUSE_BUTTON_1) && mouseInput.isMouseClickedInWindow())) {

            Vector3f dirInRadians = getDirectionInRadians();
            float angleY = dirInRadians.y;
            float angleX = dirInRadians.x;
            Vector3f tmpDir = new Vector3f(0, 0, -1);

            tmpDir = Util.rotateX(tmpDir, angleX);
            tmpDir = Util.rotateY(tmpDir, angleY);
            Vector3f rayDirection = tmpDir.normalise(null);

            shooter.shoot(rayDirection);
        }
        if (KeyboardHandler.isKeyDown(GLFW_KEY_X)) {
            System.out.println("pos: " + position + ", dir: " + getDirection() + ", bbox: " + getCollider().getBox().getPosition() + ", camRot: " + camera.getRotation());
        }

        Vector2f mouseRotation = mouseInput.getRotation();
        if (camera.updateRotation( mouseRotation.y * Camera.MOUSE_SENSITIVITY, mouseRotation.x * Camera.MOUSE_SENSITIVITY, 0f)) {
            updated = true;
        }

        updateCameraPosition();
        camera.setUpdated(updated);
        updateColliderPosition();
        shooter.update();
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
        float rotX = 180 - cameraRot.x;
        float rotZ = 0;

        return new Vector3f(rotX, rotY, rotZ);
    }

    private void updateCameraPosition() {
        camera.setPosition( position.x, position.y, position.z );
    }
}
