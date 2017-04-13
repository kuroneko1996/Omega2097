package net.omega2097.util;

import net.omega2097.GameObject;
import net.omega2097.Loader;
import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector3f;


public class PrimitivesGenerator {
    private Loader loader;
    public PrimitivesGenerator(Loader loader) {
        this.loader = loader;
    }
    public GameObject generateQuad(int width, int height) {
        float[] positions = {
                -0.5f, 0f,  0.5f,   0.5f, 0f,  0.5f,
                -0.5f, 0f, -0.5f,   0.5f, 0f, -0.5f
        };
        int[] indices = { 1, 2, 0,  1, 3, 2 };

        float[] textureCoordinates = {
                0, 0,               1 * width,0,
                0, 1 * height,      1 * width,1 * height
        };

        GameObject gameObject = new GameObject();
        gameObject.setModel(loader.loadToVAO(positions, textureCoordinates, indices));
        gameObject.setScale(new Vector3f(width, 1, height));
        return gameObject;
    }

    public GameObject generateNewQuad(int width, int height) {

        MeshBuilder builder = new MeshBuilder();
        builder.buildQuad(new Vector3f(0,0,0), new Vector3f(1,0,0), new Vector3f(0, 0, 1));
        Mesh mesh = builder.createMesh();

        GameObject gameObject = new GameObject();
        gameObject.setModel(loader.loadToVAO(mesh.getVerticesArray(), mesh.getUvArray(), mesh.getTriangles()));
        gameObject.setScale(new Vector3f(width, 1, height));
        return gameObject;
    }

    public GameObject generateCube() {
        MeshBuilder builder = new MeshBuilder();

        Vector3f upDir = new Vector3f(0, 1, 0);
        Vector3f rightDir = new Vector3f(1, 0, 0);
        Vector3f forwardDir = new Vector3f(0, 0, 1);

        Vector3f nearCorner = new Vector3f(0,0,0);
        Vector3f farCorner = new Vector3f();
        Vector3f.add(upDir, rightDir, farCorner);
        Vector3f.add(farCorner, forwardDir, farCorner);


        builder.buildQuad(nearCorner, forwardDir, rightDir);
        builder.buildQuad(nearCorner, rightDir, upDir);
        builder.buildQuad(nearCorner, upDir, forwardDir);

        // -rightDir -forwardDir
        builder.buildQuad(farCorner, Vector3f.sub(new Vector3f(0,0,0), rightDir, null),
                Vector3f.sub(new Vector3f(0,0,0), forwardDir, null));

        // -upDir -rightDir
        builder.buildQuad(farCorner, Vector3f.sub(new Vector3f(0,0,0), upDir, null),
                Vector3f.sub(new Vector3f(0,0,0), rightDir, null));

        // -forwardDir -upDir
        builder.buildQuad(farCorner, Vector3f.sub(new Vector3f(0,0,0), forwardDir, null),
                Vector3f.sub(new Vector3f(0,0,0), upDir, null));

        Mesh mesh = builder.createMesh();

        GameObject gameObject = new GameObject();
        gameObject.setModel(loader.loadToVAO(mesh.getVerticesArray(), mesh.getUvArray(), mesh.getTriangles()));
        gameObject.setScale(new Vector3f(1, 1, 1));
        return gameObject;
    }
}
