package net.omega2097.objects;

import net.omega2097.GameObject;
import net.omega2097.Loader;
import net.omega2097.Model;
import net.omega2097.util.PrimitivesGenerator;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class BulletImpact extends GameObject {
    private static final long TIME_TO_DESTROY = 2500;
    private long timeCreated;

    public static BulletImpact create(Vector3f location, PrimitivesGenerator primGen, Loader loader) {
        BulletImpact gameObject = new BulletImpact();

        // calculate size
        Model model = primGen.generateCube(0.1f);
        gameObject.setPosition(location.x, location.y, location.z);
        gameObject.setModel(model);
        gameObject.setTextureName("red.png");
        gameObject.getModel().addTextureID(loader.loadTexture("res/" + gameObject.getTextureName()));

        gameObject.timeCreated = System.currentTimeMillis();
        return gameObject;
    }

    @Override
    public void update(List<GameObject> gameObjects) {
        if (System.currentTimeMillis() > (timeCreated + TIME_TO_DESTROY)) {
            destroy();
        }
    }
}
