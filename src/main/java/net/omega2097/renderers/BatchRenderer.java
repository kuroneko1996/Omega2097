package net.omega2097.renderers;

import net.omega2097.GameObject;
import net.omega2097.Model;
import net.omega2097.util.Util;

import java.util.List;
import java.util.Map;

public class BatchRenderer extends MeshRenderer {
    private java.util.Map<Model, List<GameObject>> renderEntities;

    public BatchRenderer(float screenWidth, float screenHeight) {
        super(screenWidth, screenHeight);
    }

    public void setRenderEntities(Map<Model, List<GameObject>> renderEntities) {
        this.renderEntities = renderEntities;
    }

    @Override
    public void render() {
        if (renderEntities.isEmpty()) return;

        shader.start();

        shader.loadViewMatrix(viewMatrix);
        shader.loadProjectionMatrix(projectionMatrix);

        for (Model model : renderEntities.keySet()) {
            List<GameObject> batch = renderEntities.get(model);
            int vertexCount = model.getVertexCount();

            prepare(model);

            for (GameObject gameObject : batch) {
                Util.updateTransformationMatrix(transformationMatrix, gameObject.getPosition(),
                        gameObject.getRotation(), gameObject.getScale());
                shader.loadTransformationMatrix(transformationMatrix);
                draw(vertexCount);
            }

            cleanup(model);
        }

        shader.stop();
    }
}
