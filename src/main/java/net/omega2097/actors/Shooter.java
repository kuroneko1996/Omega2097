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
    private final static int SHOOT_DELAY = 250;

    public Shooter(Actor owner) {
        this.owner = owner;
        this.isShooting = false;
        this.shootStartTime = 0;
    }

    public void shoot(List<GameObject> gameObjects) {
        if (this.isShooting) return;
        isShooting = true;
        shootStartTime = System.currentTimeMillis();


        Vector3f ownerDirInRadians = owner.getDirectionInRadians();
        Vector3f rayOrigin = new Vector3f(owner.getPosition());

        float angleY = ownerDirInRadians.y;
        float angleX = ownerDirInRadians.x;
        Vector3f tmpDir = new Vector3f(0, 0, -1);

        tmpDir = Util.rotateX(tmpDir, angleX);
        tmpDir = Util.rotateY(tmpDir, angleY);
        Vector3f rayDirection = tmpDir.normalise(null);

        System.out.println("Player is shooting from " + rayOrigin + " to " + rayDirection);
        final Vector3f hitCoord = new Vector3f(0,0,0);

        // sort by distance
        java.util.Map<Float, GameObject> objectsMap = new TreeMap<>();
        for(GameObject gameObject : gameObjects) {
            if (gameObject.getCollider() != null) {
                float distance = Vector3f.sub(gameObject.getPosition(), rayOrigin, null).length();
                objectsMap.put(distance, gameObject);
            }
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
            BulletImpact bulletImpact = BulletImpact.create(hitCoord, Engine.getInstance().getPrimGen(),
                    Engine.getInstance().getLoader());
            gameObjects.add(bulletImpact);

            if (mapEntry.get().getValue() instanceof Actor) {
                Actor actor = (Actor)mapEntry.get().getValue();
                actor.takeDamage(10);
            }

            System.out.println("GameObject has been shot at " + hitCoord);
        }
    }
    private void createVisualRay(Vector3f origin, Vector3f rot, List<GameObject> gameObjects) {
        Engine engine = Engine.getInstance();
        Loader loader = engine.getLoader();
        PrimitivesGenerator primGen = engine.getPrimGen();

        RayTrace trace = RayTrace.create(origin, rot, primGen, loader);
        gameObjects.add(trace);
    }

    void update() {
        if (isShooting) {
            if (System.currentTimeMillis() > (shootStartTime + SHOOT_DELAY) ) {
                isShooting = false;
            }
        }
    }
}
