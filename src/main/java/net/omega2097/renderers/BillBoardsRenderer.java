package net.omega2097.renderers;

import net.omega2097.GameObject;
import net.omega2097.Model;
import net.omega2097.shaders.BillboardShader;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.TreeMap;

public class BillBoardsRenderer extends MeshRenderer {
    private java.util.TreeMap<Float, GameObject> batch;
    private BillboardShader billboardShader;

    public BillBoardsRenderer(float screenWidth, float screenHeight) {
        super(screenWidth, screenHeight);
    }

    public void setBillboardShader(BillboardShader billboardShader) {
        this.billboardShader = billboardShader;
    }

    public void setBatch(TreeMap<Float, GameObject> batch) {
        this.batch = batch;
    }

    @Override
    public void render() {
        if (batch.size() == 0) return;
        BillboardShader shader = billboardShader;

        Vector3f cameraRight = new Vector3f(viewMatrix.m00, viewMatrix.m10, viewMatrix.m20);
        Vector3f cameraUp = new Vector3f(viewMatrix.m01, viewMatrix.m11, viewMatrix.m21);

        shader.start();
        // load uniforms
        shader.loadViewMatrix(viewMatrix);
        shader.loadProjectionMatrix(projectionMatrix);
        shader.loadCameraVectors(cameraRight, cameraUp);

        batch.forEach((k, gameObject) -> {
            Model model = gameObject.getModel();
            int vertexCount = model.getVertexCount();
            prepare(model);

            Vector3f billboardCenter = new Vector3f(gameObject.getPosition());
            Vector2f billboardSize = new Vector2f(1,1);
            shader.loadBillboard(billboardCenter, billboardSize);

            draw(vertexCount);

            cleanup(model);
        });
        shader.stop();
    }
}
