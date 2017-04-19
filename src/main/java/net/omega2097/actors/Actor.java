package net.omega2097.actors;

import net.omega2097.BoundingBox;
import net.omega2097.Camera;
import net.omega2097.GameObject;
import net.omega2097.Stat;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class Actor extends GameObject {
    Stat<Float> health;
    Stat<Integer> gold = new Stat<>(0);
    Shooter shooter;

    public Actor() {
        this(100);
    }

    public Actor(float hp) {
        super();
        health = new Stat<Float>(hp);
    }

    public Stat<Float> getHealth() {
        return health;
    }

    public Stat<Integer> getGold() {
        return gold;
    }

    public Shooter getShooter() {
        return shooter;
    }

    public void setShooter(Shooter shooter) {
        this.shooter = shooter;
    }

    public Vector3f getDirection() {
        return new Vector3f(rotation);
    }

    Vector3f getDirectionInRadians() {
        Vector3f result = getDirection();
        result.set((float)Math.toRadians(result.x), (float)Math.toRadians(result.y), (float)Math.toRadians(result.z));
        return result;
    }

    void takeDamage(float dmg) {
        health.setCurrent(health.getCurrent() - dmg);

        if (health.getCurrent() <= 0) {
            System.out.println("%s died");
            destroy();
        }
    }

    @Override
    public void setPosition(float x, float y, float z) {
        super.setPosition(x, y, z);
        updateColliderPosition();
    }

    @Override
    public void update(List<GameObject> gameObjects) {
        super.update(gameObjects);

        if (shooter != null) {
            shooter.update();
        }
    }

    void updateColliderPosition() {
        if (collider == null) return;
        BoundingBox bbox = collider.getBox();
        float x = position.x - bbox.getSize().x / 2.0f;
        float y = position.y - bbox.getSize().y / 2.0f;
        float z = position.z - bbox.getSize().z / 2.0f;
        collider.setPosition(x, y, z);
    }
}
