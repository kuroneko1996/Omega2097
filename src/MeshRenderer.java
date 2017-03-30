import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import shaders.StaticShader;
import util.Util;

public class MeshRenderer {
    public void prepare() {

    }

    public void render(GameObject gameObject, StaticShader shader) {
        Model model = gameObject.getModel();

        shader.start();
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
}
