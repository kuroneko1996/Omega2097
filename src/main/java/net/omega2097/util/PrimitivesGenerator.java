package net.omega2097.util;

import net.omega2097.GameObject;
import net.omega2097.Loader;
import net.omega2097.Model;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;


public class PrimitivesGenerator {
    private Loader loader;
    public PrimitivesGenerator(Loader loader) {
        this.loader = loader;
    }

    public Model generateVerticalQuad(int width, int height, int z) {
        MeshBuilder builder = new MeshBuilder();
        int texelSizeX = width;
        int texelSizeY = height;

        List<Vector2f> texCoordinates = new ArrayList<>();
        texCoordinates.add(new Vector2f(0, texelSizeY));
        texCoordinates.add(new Vector2f(0, 0));
        texCoordinates.add(new Vector2f(texelSizeX, 0));
        texCoordinates.add(new Vector2f(texelSizeX, texelSizeY));

        Vector3f nearCorner = new Vector3f(-0.5f, -0.5f,z);
        Vector3f upDir = new Vector3f(0, 1f, 0);
        Vector3f rightDir = new Vector3f(1f, 0, 0);

        builder.buildQuad(nearCorner, rightDir, upDir, texCoordinates);

        Mesh mesh = builder.createMesh();

        return loader.loadToVAO(mesh);
    }

    public Model generateVerticalQuad(int width, int height) {
        return generateVerticalQuad(width, height, 0);
    }

    public Model generateRectangle(float x1, float y1, float x2, float y2, float z, float uvStartX, float uvStartY,
                                   float uvEndX, float uvEndY) {
        MeshBuilder builder = new MeshBuilder();
        float width = x2-x1;
        float height = y2-y1;

        List<Vector2f> texCoordinates = new ArrayList<>();
        texCoordinates.add(new Vector2f(uvStartX, uvEndY));
        texCoordinates.add(new Vector2f(uvStartX, uvStartY));
        texCoordinates.add(new Vector2f(uvEndX, uvStartY));
        texCoordinates.add(new Vector2f(uvEndX, uvEndY));

        Vector3f nearCorner = new Vector3f(x1, y1, z);
        Vector3f upDir = new Vector3f(0, height, 0);
        Vector3f rightDir = new Vector3f(width, 0, 0);

        builder.buildQuad(nearCorner, rightDir, upDir, texCoordinates);

        Mesh mesh = builder.createMesh();

        return loader.loadToVAO(mesh);
    }

    public Model generateRectangle(float x1, float y1, float x2, float y2, float z) {
        return generateRectangle(x1, y1, x2, y2, z, 0f,0f,1f,1f);
    }

    public Model generateHorizontalQuad(int width, int height) {
        MeshBuilder builder = new MeshBuilder();
        int texelSizeX = width;
        int texelSizeY = height;

        List<Vector2f> texCoordinates = new ArrayList<>();
        texCoordinates.add(new Vector2f(0, 0));
        texCoordinates.add(new Vector2f(0, texelSizeY));
        texCoordinates.add(new Vector2f(texelSizeX, texelSizeY));
        texCoordinates.add(new Vector2f(texelSizeX, 0));

        builder.buildQuad(new Vector3f(0,0,0), new Vector3f(1,0,0), new Vector3f(0, 0, 1),
                texCoordinates);

        Mesh mesh = builder.createMesh();

        return loader.loadToVAO(mesh);
    }

    public Model generateBox(float width, float height, float depth) {
        MeshBuilder builder = new MeshBuilder();

        Vector3f upDir = new Vector3f(0, height, 0);
        Vector3f rightDir = new Vector3f(width, 0, 0);
        Vector3f forwardDir = new Vector3f(0, 0, depth);

        Vector3f nearCorner = new Vector3f(-width / 2.0f,-height / 2.0f,-depth / 2.0f);
        Vector3f farCorner = new Vector3f(width / 2.0f, height / 2.0f, depth / 2.0f);


        // uv coordinates holder
        List<Vector2f> texCoordinates = new ArrayList<>();

        // bottom side
        builder.buildQuad(nearCorner, forwardDir, rightDir);
        // front side
        texCoordinates.clear();
        texCoordinates.add(new Vector2f(1, 1));
        texCoordinates.add(new Vector2f(1, 0));
        texCoordinates.add(new Vector2f(0, 0));
        texCoordinates.add(new Vector2f(0, 1));
        builder.buildQuad(nearCorner, rightDir, upDir, texCoordinates);
        // left side
        texCoordinates.clear();
        texCoordinates.add(new Vector2f(1, 1));
        texCoordinates.add(new Vector2f(0, 1));
        texCoordinates.add(new Vector2f(0, 0));
        texCoordinates.add(new Vector2f(1, 0));
        builder.buildQuad(nearCorner, upDir, forwardDir, texCoordinates);

        // top side
        // -rightDir -forwardDir
        builder.buildQuad(farCorner, Vector3f.sub(new Vector3f(0,0,0), rightDir, null),
                Vector3f.sub(new Vector3f(0,0,0), forwardDir, null));

        // back side
        // -upDir -rightDir
        texCoordinates.clear();
        texCoordinates.add(new Vector2f(1, 0));
        texCoordinates.add(new Vector2f(0, 0));
        texCoordinates.add(new Vector2f(0, 1));
        texCoordinates.add(new Vector2f(1, 1));
        builder.buildQuad(farCorner, Vector3f.sub(new Vector3f(0,0,0), upDir, null),
                Vector3f.sub(new Vector3f(0,0,0), rightDir, null), texCoordinates);

        // right side
        // -forwardDir -upDir
        builder.buildQuad(farCorner, Vector3f.sub(new Vector3f(0,0,0), forwardDir, null),
                Vector3f.sub(new Vector3f(0,0,0), upDir, null));

        Mesh mesh = builder.createMesh();
        return loader.loadToVAO(mesh);
    }

    public Model generateCube(float size) {
        return generateBox(size, size, size);
    }
}
