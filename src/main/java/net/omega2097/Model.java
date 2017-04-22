package net.omega2097;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private int vaoID;
    private int iboID; // indexed buffer
    private int vertexCount;
    private int textureID;
    private boolean textured = false; // doesn't have uv coordinates
    private List<Integer> textureIDs = new ArrayList<>();
    private int currentTexture = 0;

    public int getVaoID() {
        return vaoID;
    }

    public void setVaoID(int vaoID) {
        this.vaoID = vaoID;
    }

    public int getIboID() {
        return iboID;
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

    public void addTextureID(int textureID) {
        this.textureIDs.add(textureID);
    }

    public int getTextureID() {
        return textureIDs.get(currentTexture);
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
