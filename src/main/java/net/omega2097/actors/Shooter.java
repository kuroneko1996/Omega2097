package net.omega2097.actors;

import net.omega2097.*;
import net.omega2097.objects.BulletImpact;
import net.omega2097.objects.RayTrace;
import net.omega2097.util.PrimitivesGenerator;
import net.omega2097.util.Util;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;
import java.util.Optional;
import java.util.TreeMap;


public class Shooter {
    private Actor owner;
    private boolean isShooting;
    private long shootStartTime;
    private long shootDelay = 250;

    public Shooter(Actor owner) {
        this.owner = owner;
        this.isShooting = false;
        this.shootStartTime = 0;
    }

    public long getShootDelay() {
        return shootDelay;
    }

    public void setShootDelay(long shootDelay) {
        this.shootDelay = shootDelay;
    }

    void shoot(Vector3f rayDirection) {
        if (isShooting) return;

        isShooting = true;
        shootStartTime = System.currentTimeMillis();
        List<GameObject> gameObjects = Engine.getInstance().getGameObjects();

        Vector3f rayOrigin = new Vector3f(owner.getPosition());

        final Vector3f hitCoord = new Vector3f(0,0,0);
        System.out.println(owner.getName() + " is shooting.");

        // sort by distance
        java.util.Map<Float, GameObject> objectsMap = new TreeMap<>();
        for(GameObject gameObject : gameObjects) {
            if (gameObject.getCollider() != null && gameObject != owner) {
                float distance = Vector3f.sub(gameObject.getPosition(), rayOrigin, null).length();
                objectsMap.put(distance, gameObject);
            }
        }
        if ( !(owner instanceof Player) ) {
            GameObject player = Engine.getInstance().getPlayer();
            float distance = Vector3f.sub(player.getPosition(), rayOrigin, null).length();
            objectsMap.put(distance, player);
        }

        // check intersections
        Optional<java.util.Map.Entry<Float, GameObject>> mapEntry = objectsMap.entrySet().stream().filter((entry) -> {
            if (!entry.getValue().isSolid()) return false;

            BoundingBox bbox = entry.getValue().getCollider().getBox();
            Vector3f hit = Util.hitBoundingBox(rayOrigin, rayDirection, bbox.getMin(), bbox.getMax());
            if (hit != null) {
                hitCoord.set(hit.x, hit.y, hit.z);
                return true;
            }
            return false;
        }).findFirst();

        if (mapEntry.isPresent()) {
            GameObject hitGameObject = mapEntry.get().getValue();

            System.out.println(hitGameObject.getName() + " has been shot at " + hitCoord + ", dst: " + mapEntry.get().getKey());
            if (hitGameObject instanceof Actor) {
                Actor actor = (Actor)hitGameObject;
                actor.takeDamage(10);
            }
        }
    }

    void update() {
        if (isShooting) {
            if (System.currentTimeMillis() > (shootStartTime + shootDelay) ) {
                isShooting = false;
            }
        }
    }
}
