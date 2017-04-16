package net.omega2097.shaders;

import org.lwjgl.util.vector.Matrix4f;

public class StaticShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/main/java/net/omega2097/shaders/vertexShader.txt";
    private static final String FRAGMENT_FILE = "src/main/java/net/omega2097/shaders/fragmentShader.txt";

    private int locationOfTransformationMatrix;
    private int locationOfProjectionMatrix;
    private int locationOfViewMatrix;
    private int textureSampler;

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        loadMatrix(locationOfTransformationMatrix, matrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        loadMatrix(locationOfProjectionMatrix, matrix);
    }

    public void loadViewMatrix(Matrix4f matrix) {
        loadMatrix(locationOfViewMatrix, matrix);
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");
        bindAttribute(1, "texcoords");
    }

    @Override
    protected void getAllUniformLocations() {
        locationOfTransformationMatrix = super.getUniformLocation("transformationMatrix");
        locationOfProjectionMatrix = super.getUniformLocation("projectionMatrix");
        locationOfViewMatrix = super.getUniformLocation("viewMatrix");
        textureSampler = super.getUniformLocation("textureSampler");
    }
}
