package net.omega2097;

import net.omega2097.util.Mesh;

public interface ILoader {
    int loadTexture(String fileName);
    Model load(float[] positions, float[] textureCoordinates, int[] indices);
    Model load(Mesh mesh);
    Model updateModel(Model model, Mesh mesh);
    void cleanUp();
}
