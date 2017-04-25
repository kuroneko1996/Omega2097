package net.omega2097.renderers;

import net.omega2097.GameObject;
import net.omega2097.Model;
import net.omega2097.shaders.GuiShader;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;
import java.util.Map;

public class GuiRenderer extends MeshRenderer {
    private GuiShader guiShader;
    private java.util.Map<Model, List<GameObject>> renderEntities;
    private Matrix4f guiProjectionMatrix;

    public GuiRenderer(float screenWidth, float screenHeight) {
        super(screenWidth, screenHeight);
        createGuiProjectionMatrix();
    }

    public void setGuiShader(GuiShader guiShader) {
        this.guiShader = guiShader;
    }

    public void setRenderEntities(Map<Model, List<GameObject>> renderEntities) {
        this.renderEntities = renderEntities;
    }

    @Override
    public void render() {
        if (renderEntities.isEmpty()) return;
        GuiShader shader = guiShader;

        shader.start();
        // load uniforms
        shader.loadProjectionMatrix(guiProjectionMatrix);

        for (Model model : renderEntities.keySet()) {
            List<GameObject> batch = renderEntities.get(model);
            int vertexCount = model.getVertexCount();

            prepare(model);

            for (GameObject gameObject : batch) {
                Vector3f guiCenter = gameObject.getPosition();
                shader.loadGuiCenter(guiCenter.x, guiCenter.y, guiCenter.z);

                draw(vertexCount);
            }

            cleanup(model);
        }

        shader.stop();
    }

    private void createGuiProjectionMatrix() {
        float left = 0f;
        float right = screenWidth;
        float bottom = 0;
        float top = screenHeight;

        float zNear = -1f;
        float zFar = 1f;

        guiProjectionMatrix = new Matrix4f();
        guiProjectionMatrix.setIdentity();

        guiProjectionMatrix.m00 = 2.0f / (right - left);
        guiProjectionMatrix.m11 = 2.0f / (top - bottom);
        guiProjectionMatrix.m22 = - 2.0f / (zFar - zNear);
        guiProjectionMatrix.m33 = 1.0f;

        guiProjectionMatrix.m30 = - (right + left) / (right - left);
        guiProjectionMatrix.m31 = - (top + bottom) / (top - bottom);
        guiProjectionMatrix.m32 = - (zFar + zNear) / (zFar - zNear);
    }
}
