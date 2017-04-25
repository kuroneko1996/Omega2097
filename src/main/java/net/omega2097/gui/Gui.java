package net.omega2097.gui;

import net.omega2097.GameObject;
import net.omega2097.renderers.GuiRenderer;
import net.omega2097.shaders.GuiShader;

import java.util.ArrayList;
import java.util.List;

abstract public class Gui {
    private float screenWidth;
    private float screenHeight;
    private GuiRenderer renderer;
    private GuiShader shader;
    private List<GameObject> gameObjects = new ArrayList<>();

    public Gui(float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        shader = new GuiShader();
        renderer = new GuiRenderer(screenWidth, screenHeight);
        renderer.setGuiShader(shader);
    }

    public void add(GameObject gameObject) {
        gameObjects.add(gameObject);
    }

    public void clear() {
        gameObjects.clear();
    }

    public void render() {
        renderer.setRenderEntities(gameObjects);
        renderer.render();
    }

    public void cleanup() {
        gameObjects.clear();
        shader.cleanUp();
    }

}
