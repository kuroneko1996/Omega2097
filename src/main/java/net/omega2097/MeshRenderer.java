package net.omega2097;

import net.omega2097.shaders.BillboardShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import net.omega2097.shaders.StaticShader;
import net.omega2097.util.Util;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL11.GL_BLEND;

public class MeshRenderer {

    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000f;
    private Matrix4f projectionMatrix;
    private Matrix4f transformationMatrix;
    private float aspectRatio;

    public MeshRenderer(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        createProjectionMatrix();
        createTransformationMatrix();
    }
    public void render(GameObject gameObject, StaticShader shader, Matrix4f viewMatrix) {
        Model model = gameObject.getModel();

        shader.start();
        shader.loadViewMatrix(viewMatrix);
        shader.loadProjectionMatrix(projectionMatrix);
        Util.updateTransformationMatrix(transformationMatrix, gameObject.getPosition(),
                gameObject.getRotation(), gameObject.getScale());
        shader.loadTransformationMatrix(transformationMatrix);

        glRenderAndCleanUp(model);

        shader.stop();
    }

    public void renderBillBoard(GameObject gameObject, BillboardShader shader, Matrix4f viewMatrix) {
        Model model = gameObject.getModel();
        Vector3f billboardCenter = new Vector3f(gameObject.getPosition());
        Vector2f billboardSize = new Vector2f(1,1);
        Vector3f cameraRight = new Vector3f(viewMatrix.m00, viewMatrix.m10, viewMatrix.m20);
        Vector3f cameraUp = new Vector3f(viewMatrix.m01, viewMatrix.m11, viewMatrix.m21);


        shader.start();
        // load uniforms
        shader.loadViewMatrix(viewMatrix);
        shader.loadProjectionMatrix(projectionMatrix);
        shader.loadCameraVectors(cameraRight, cameraUp);
        shader.loadBillboard(billboardCenter, billboardSize);

        glRenderAndCleanUp(model);

        shader.stop();
    }

    private void glRenderAndCleanUp(Model model) {
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        if (model.isTextured()) { // use uv coordinates
            GL20.glEnableVertexAttribArray(1);
        }
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTextureID());

        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        // clean up
        GL20.glDisableVertexAttribArray(0);
        if (model.isTextured()) {
            GL20.glDisableVertexAttribArray(1);
        }
        GL30.glBindVertexArray(0);
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

    private void createTransformationMatrix() {
        transformationMatrix = Util.createTransformationMatrix(new Vector3f(0,0,0),
                new Vector3f(0,0,0), new Vector3f(1,1,1));
    }
}
