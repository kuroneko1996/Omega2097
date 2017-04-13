package net.omega2097;

import org.lwjgl.util.vector.Vector3f;

public class GameObject {
    private Model model;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getTextureName() {
        return textureName;
    }

    public void setTextureName(String textureName) {
        this.textureName = textureName;
    }

    private String modelName;
    private String textureName;
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scale;

    public GameObject() {
        this.position = new Vector3f(0,0,0);
        this.scale = new Vector3f(1,1,1);
        this.rotation = new Vector3f(0,0,0);
    }

    public GameObject(String modelName, String textureName, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.modelName = modelName;
        this.textureName = textureName;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public GameObject(String modelName, String textureName) {
        this(modelName, textureName, new Vector3f(0f,0f,0f), new Vector3f(0f,0f,0f),
                new Vector3f(1f,1f,1f));
    }

    public GameObject(String modelName) {
        this(modelName, "");
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }
}
