package net.omega2097.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class GuiShader extends ShaderProgram {
    private static final String VERTEX_FILE = "src/main/java/net/omega2097/shaders/gui/vertexShader.txt";
    private static final String FRAGMENT_FILE = "src/main/java/net/omega2097/shaders/fragmentShader.txt";

    private int locationOfProjectionMatrix;
    private int locationOfViewMatrix;

    public GuiShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        loadMatrix(locationOfProjectionMatrix, matrix);
    }

    public void loadViewMatrix(Matrix4f matrix) {
        loadMatrix(locationOfViewMatrix, matrix);
    }

    public void loadGuiCenter(Vector3f center) {
        loadVector3f(getUniformLocation("vertexPosition_worldspace"), center);
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");
        bindAttribute(1, "texcoords");
    }

    @Override
    protected void getAllUniformLocations() {
        locationOfProjectionMatrix = getUniformLocation("projectionMatrix");
        locationOfViewMatrix = getUniformLocation("viewMatrix");
    }
}
