package net.omega2097.util;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class MeshBuilder {
    private List<Vector3f> vertices = new ArrayList<>();
    private List<Vector3f> normals = new ArrayList<>();
    private List<Vector2f> uv = new ArrayList<>();
    private List<Integer> indices = new ArrayList<>();

    public void addTriangle(int index0, int index1, int index2) {
        indices.add(index0);
        indices.add(index1);
        indices.add(index2);
    }

    public Mesh createMesh() {
        Mesh mesh = new Mesh();
        mesh.setVertices(vertices.toArray(new Vector3f[vertices.size()]));
        mesh.setTriangles(indices.stream().mapToInt(i->i).toArray());

        if (uv.size() == vertices.size()) {
            mesh.setUv(uv.toArray(new Vector2f[uv.size()]));
        }

        return mesh;
    }

    public void buildQuad(Vector3f offset, Vector3f widthDir, Vector3f lengthDir, List<Vector2f> customUV) {
        MeshBuilder builder = this;

        Vector3f normal = new Vector3f();
        Vector3f.cross(lengthDir, widthDir, normal);
        normal.normalise();

        builder.vertices.add(offset);
        builder.normals.add(normal);

        builder.vertices.add(Vector3f.add(offset, lengthDir, null));
        builder.normals.add(normal);

        // offset + length + width
        Vector3f tmp = new Vector3f();
        Vector3f.add(offset, lengthDir, tmp);
        Vector3f.add(tmp, widthDir, tmp);
        builder.vertices.add(tmp);
        builder.normals.add(normal);

        builder.vertices.add(Vector3f.add(offset, widthDir, null));
        builder.normals.add(normal);

        if (customUV == null) {
            builder.uv.add(new Vector2f(0, 0));
            builder.uv.add(new Vector2f(0.0f, 1.0f));
            builder.uv.add(new Vector2f(1.0f, 1.0f));
            builder.uv.add(new Vector2f(1.0f, 0.0f));
        } else {
            builder.uv.addAll(customUV);
        }

        int baseIndex = builder.vertices.size() - 4;

        builder.addTriangle(baseIndex, baseIndex + 1, baseIndex + 2);
        builder.addTriangle(baseIndex, baseIndex + 2, baseIndex + 3);
    }

    public void buildQuad(Vector3f offset, Vector3f widthDir, Vector3f lengthDir) {
        buildQuad(offset, widthDir, lengthDir, null);
    }
}
