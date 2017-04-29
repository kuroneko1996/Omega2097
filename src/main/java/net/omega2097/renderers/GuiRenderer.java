package net.omega2097.renderers;

import net.omega2097.GameObject;
import net.omega2097.Model;
import net.omega2097.shaders.GuiShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class GuiRenderer extends MeshRenderer {
    private GuiShader guiShader;
    private Matrix4f guiProjectionMatrix;
    private List<GameObject> renderEntities;

    public GuiRenderer(float screenWidth, float screenHeight) {
        super(screenWidth, screenHeight);
        createGuiProjectionMatrix();
    }

    public void setGuiShader(GuiShader guiShader) {
        this.guiShader = guiShader;
    }

    public void setRenderEntities(List<GameObject> renderEntities) {
        this.renderEntities = renderEntities;
    }

    @Override
    public void render() {
        if (renderEntities.isEmpty()) return;
        GuiShader shader = guiShader;

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        shader.start();
        // load uniforms
        shader.loadProjectionMatrix(guiProjectionMatrix);

        for (GameObject gameObject : renderEntities) {
            Model model = gameObject.getModel();
            int vertexCount = model.getVertexCount();

            prepare(model);

            Vector3f guiCenter = gameObject.getPosition();
            shader.loadGuiCenter(guiCenter.x, guiCenter.y, guiCenter.z);
            draw(vertexCount);

            cleanup(model);
        }

        shader.stop();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
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
