package net.omega2097;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Loader {
    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    public Model loadToVAO(float[] positions, float[] textureCoordinates, int[] indices) {
        int vaoID = createVAO();

        Model model = new Model(vaoID, indices.length);
        bindIndicesBuffer(indices);
        storeDataInAttributes(0, 3, positions);
        if (textureCoordinates.length > 0) {
            storeDataInAttributes(1, 2, textureCoordinates);
            model.setTextured(true);
        }

        unbindVAO();
        return model;
    }

    public int loadTexture(String fileName) {
        int textureID;

        textureID = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        float pixels[] = {
                0.4f, 0.4f, 0.4f,   0.4f, 0.4f, 0.4f,
                0.4f, 0.4f, 0.4f,   0.4f, 0.4f, 0.4f,
        };
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, 2, 2, 0, GL11.GL_RGB, GL11.GL_FLOAT, pixels);

        textures.add(textureID);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        return textureID;
    }

    public void cleanUp() {
        for (int vao: vaos) {
            GL30.glDeleteVertexArrays(vao);
        }
        for (int vbo: vbos) {
            GL15.glDeleteBuffers(vbo);
        }
        for (int texture: textures) {
            GL11.glDeleteTextures(texture);
        }
    }

    private int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    private void storeDataInAttributes(int attributeNumber, int size, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

        FloatBuffer buffer = toFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, size, GL11.GL_FLOAT, false, 0, 0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = toIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

    }

    private IntBuffer toIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer toFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
}
