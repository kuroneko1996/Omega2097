package net.omega2097.util;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Mesh {
    private Vector3f[] vertices;
    private Vector2f[] uv;
    private Vector3f[] normals;
    private int[] triangles;

    public Vector3f[] getVertices() {
        return vertices;
    }

    public void setVertices(Vector3f[] vertices) {
        this.vertices = vertices;
    }

    public Vector2f[] getUv() {
        return uv;
    }

    public void setUv(Vector2f[] uv) {
        this.uv = uv;
    }

    public Vector3f[] getNormals() {
        return normals;
    }

    public void setNormals(Vector3f[] normals) {
        this.normals = normals;
    }

    public int[] getTriangles() {
        return triangles;
    }

    public void setTriangles(int[] triangles) {
        this.triangles = triangles;
    }

    public Mesh() {

    }

    public float[] getVerticesArray() {
        float[] verticesArray = new float[vertices.length * 3];

        for(int i = 0; i < vertices.length; i++) {
            verticesArray[i * 3] = vertices[i].x;
            verticesArray[i * 3 + 1] = vertices[i].y;
            verticesArray[i * 3 + 2] = vertices[i].z;
        }
        return verticesArray;
    }

    public float[] getUvArray() {
        float[] uvArray = new float[uv.length * 2];

        for(int i = 0; i < uv.length; i++) {
            uvArray[i * 2] = uv[i].x;
            uvArray[i * 2 + 1] = uv[i].y;
        }
        return uvArray;
    }

    public void clear() {
        vertices = null;
        uv = null;
        normals = null;
        triangles = null;
    }
}
