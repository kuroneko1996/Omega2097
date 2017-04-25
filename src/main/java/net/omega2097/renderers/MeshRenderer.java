package net.omega2097.renderers;

import net.omega2097.GameObject;
import net.omega2097.Model;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import net.omega2097.shaders.StaticShader;
import net.omega2097.util.Util;
import org.lwjgl.util.vector.Vector3f;

public class MeshRenderer extends Renderer {
    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000f;
    Matrix4f projectionMatrix;
    Matrix4f transformationMatrix;
    Matrix4f viewMatrix;
    StaticShader shader;
    private GameObject gameObject;


    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public void setViewMatrix(Matrix4f viewMatrix) {
        this.viewMatrix = viewMatrix;
    }

    public void setShader(StaticShader shader) {
        this.shader = shader;
    }

    public MeshRenderer(float screenWidth, float screenHeight) {
        super(screenWidth, screenHeight);
        createProjectionMatrix();
        createTransformationMatrix();
    }

    @Override
    public void render() {
        Model model = gameObject.getModel();

        shader.start();
        shader.loadViewMatrix(viewMatrix);
        shader.loadProjectionMatrix(projectionMatrix);
        Util.updateTransformationMatrix(transformationMatrix, gameObject.getPosition(),
                gameObject.getRotation(), gameObject.getScale());
        shader.loadTransformationMatrix(transformationMatrix);

        prepare(model);
        draw(model.getVertexCount());
        cleanup(model);

        shader.stop();
    }

    void prepare(Model model) {
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        if (model.isTextured()) { // use uv coordinates
            GL20.glEnableVertexAttribArray(1);
        }
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTextureID());

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, model.getIboID());
    }

    void cleanup(Model model) {
        // clean up
        // unbind IBO
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER,0);

        GL20.glDisableVertexAttribArray(0);
        if (model.isTextured()) {
            GL20.glDisableVertexAttribArray(1);
        }
        GL30.glBindVertexArray(0);
    }

    void draw(int vertexCount) {
        GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);
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

    private void createTransformationMatrix() {
        transformationMatrix = Util.createTransformationMatrix(new Vector3f(0,0,0),
                new Vector3f(0,0,0), new Vector3f(1,1,1));
    }
}
