package net.omega2097.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class BillboardShader extends ShaderProgram {
    private static final String VERTEX_FILE = "src/main/java/net/omega2097/shaders/billboard/vertexShader.txt";
    private static final String FRAGMENT_FILE = "src/main/java/net/omega2097/shaders/fragmentShader.txt";

    private int locationOfProjectionMatrix;
    private int locationOfViewMatrix;

    public BillboardShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadCameraVectors(Vector3f right, Vector3f up) {
        loadVector3f(getUniformLocation("CameraRight_worldspace"), right);
        loadVector3f(getUniformLocation("CameraUp_worldspace"), up);
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        loadMatrix(locationOfProjectionMatrix, matrix);
    }

    public void loadViewMatrix(Matrix4f matrix) {
        loadMatrix(locationOfViewMatrix, matrix);
    }

    public void loadBillboard(Vector3f center, Vector2f size) {
        loadVector3f(getUniformLocation("BillboardPos"), center);
        loadVector2f(getUniformLocation("BillboardSize"), size);
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
