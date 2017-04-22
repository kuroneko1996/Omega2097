package net.omega2097;

import net.omega2097.shaders.BillboardShader;
import net.omega2097.shaders.GuiShader;
import org.lwjgl.opengl.*;
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
    private Matrix4f guiProjectionMatrix;
    private Matrix4f transformationMatrix;
    private float screenAspectRatio;
    private float screenWidth;
    private float screenHeight;

    public MeshRenderer(float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screenAspectRatio = screenWidth / screenHeight;
        createProjectionMatrix();
        createTransformationMatrix();
        createGuiProjectionMatrix();
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

    public void renderGui(GameObject gameObject, GuiShader shader, Matrix4f viewMatrix) {
        Model model = gameObject.getModel();
        Vector3f guiCenter = gameObject.getPosition();

        shader.start();
        // load uniforms
        shader.loadViewMatrix(viewMatrix);
        shader.loadProjectionMatrix(guiProjectionMatrix);
        shader.loadGuiCenter(guiCenter.x, guiCenter.y, guiCenter.z);

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

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, model.getIboID());
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        // clean up
        // unbind IBO
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER,0);

        GL20.glDisableVertexAttribArray(0);
        if (model.isTextured()) {
            GL20.glDisableVertexAttribArray(1);
        }
        GL30.glBindVertexArray(0);
    }

    private void createProjectionMatrix() {
        float yScale = (float)((1f / Math.tan(Math.toRadians(FOV / 2f))) * screenAspectRatio);
        float xScale = yScale / screenAspectRatio;
        float frustumLength = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = xScale;
        projectionMatrix.m11 = yScale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustumLength);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustumLength);
        projectionMatrix.m33 = 0;
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

    private void createTransformationMatrix() {
        transformationMatrix = Util.createTransformationMatrix(new Vector3f(0,0,0),
                new Vector3f(0,0,0), new Vector3f(1,1,1));
    }
}
