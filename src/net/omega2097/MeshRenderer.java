package net.omega2097;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import net.omega2097.shaders.StaticShader;
import net.omega2097.util.Util;

public class MeshRenderer {

    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000f;
    private Matrix4f projectionMatrix;
    private float aspectRatio;

    public MeshRenderer(float aspectRatio, StaticShader shader) {
        this.aspectRatio = aspectRatio;
        createProjectionMatrix();
        loadProjectionMatrixIntoShader(shader);
    }

    public void loadProjectionMatrixIntoShader(StaticShader shader) {
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(GameObject gameObject, StaticShader shader, Matrix4f viewMatrix) {
        Model model = gameObject.getModel();

        shader.start();
        shader.loadViewMatrix(viewMatrix);

        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        Matrix4f transformationMatrix = Util.createTransformationMatrix(gameObject.getPosition(),
                gameObject.getRotation(), gameObject.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    private void createProjectionMatrix() {
        float yScale = (float)((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float xScale = yScale / aspectRatio;
        float frustumLength = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = xScale;
        projectionMatrix.m11 = yScale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustumLength);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustumLength);
        projectionMatrix.m33 = 0;
    }
}
