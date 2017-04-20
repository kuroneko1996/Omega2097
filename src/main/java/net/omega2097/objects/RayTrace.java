package net.omega2097.objects;

import net.omega2097.GameObject;
import net.omega2097.Loader;
import net.omega2097.Model;
import net.omega2097.util.PrimitivesGenerator;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class RayTrace extends GameObject {
    private static final long TIME_TO_DESTROY = 1500;
    private long timeCreated;

    public static RayTrace create(Vector3f origin, Vector3f rot, PrimitivesGenerator primGen, Loader loader) {
        RayTrace gameObject = new RayTrace();

        // calculate size
        Model model = primGen.generateBox(0.025f,0.025f,3);
        gameObject.setPosition(origin.x, origin.y, origin.z);

        gameObject.setRotation(rot.x, rot.y, rot.z);

        gameObject.setModel(model);
        gameObject.setTextureName("red.png");
        gameObject.getModel().addTextureID(loader.loadTexture("res/" + gameObject.getTextureName()));

        gameObject.timeCreated = System.currentTimeMillis();
        return gameObject;
    }

    @Override
    public void update() {
        if (System.currentTimeMillis() > timeCreated + TIME_TO_DESTROY) {
            destroy();
        }
    }
}
