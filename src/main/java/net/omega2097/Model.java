package net.omega2097;

import net.omega2097.util.Mesh;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private int vaoID;
    private int vboID; // vertex buffer (positions)
    private int iboID; // indexed buffer
    private int uvboID; // uv buffer

    private Mesh mesh;
    private int vertexCount;
    private boolean textured = false; // doesn't have uv coordinates
    private List<Texture> textures = new ArrayList<>();
    private int currentTexture = 0;

    private boolean billboard = false;

    public boolean isBillboard() {
        return billboard;
    }

    public void setBillboard(boolean billboard) {
        this.billboard = billboard;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public int getVboID() {
        return vboID;
    }

    public void setVboID(int vboID) {
        this.vboID = vboID;
    }

    public int getVaoID() {
        return vaoID;
    }

    public void setVaoID(int vaoID) {
        this.vaoID = vaoID;
    }

    public int getIboID() {
        return iboID;
    }

    public void setIboID(int iboID) {
        this.iboID = iboID;
    }

    public int getUvboID() {
        return uvboID;
    }

    public void setUvboID(int uvboID) {
        this.uvboID = uvboID;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    public boolean isTextured() {
        return textured;
    }

    public void setTextured(boolean textured) {
        this.textured = textured;
    }

    public void addTexture(Texture texture) {
        this.textures.add(texture);
    }

    public int getTextureID() {
        return textures.get(currentTexture).getId();
    }

    public void setCurrentTexture(int number) {
        this.currentTexture = number;
    }

    public Model(int vaoID, int iboID, int vertexCount) {
        this.vaoID = vaoID;
        this.iboID = iboID;
        this.vertexCount = vertexCount;
    }
}
