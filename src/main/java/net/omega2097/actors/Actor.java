package net.omega2097.actors;

import net.omega2097.*;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class Actor extends GameObject {
    Stat<Float> health = new Stat<>(100f);
    Stat<Integer> gold = new Stat<>(0);
    Shooter shooter;
    Ai ai;
    Vector3f direction = new Vector3f(0,0,0); // have to be a normalized vector

    public Actor() {
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

    public Ai getAi() {
        return ai;
    }

    public void setAi(Ai ai) {
        this.ai = ai;
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
    }

    @Override
    public void setPosition(float x, float y, float z) {
        super.setPosition(x, y, z);
        updateColliderPosition();
    }

    @Override
    public void setRotation(float x, float y, float z) {
        super.setRotation(x, y, z);
        //TODO update direction here
    }

    @Override
    public void update() {
        super.update();

        if (ai != null) {
            ai.update();
        }
    }

    @Override
    protected void onTriggerEnter(Collider other) {
        super.onTriggerEnter(other);

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
