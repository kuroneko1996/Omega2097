package net.omega2097;

import org.lwjgl.util.vector.Vector3f;

public class GameObject {
    private String name = "GameObject";
    private Model model;
    private boolean destroyed = false;
    private boolean solid = false;

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

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String modelName;
    private String textureName;
    protected Vector3f position;
    protected Vector3f rotation = new Vector3f(0,0,0);
    protected Vector3f scale = new Vector3f(1,1,1);

    public GameObject() {
        this.position = new Vector3f(0,0,0);
        this.scale = new Vector3f(1,1,1);
        this.rotation = new Vector3f(0,0,0);
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

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.set(x, y, z);
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(float x, float y, float z) {
        this.scale.set(x, y, z);
    }

    public void update() {
        if (isDestroyed()) throw new RuntimeException("Can't update object because its destroyed!");
    }

    public void destroy() {
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    protected void onTriggerEnter(Collider other) {

    }

    protected Collider collider;
    public Collider getCollider() {
        return collider;
    }
    public void setCollider(Collider collider) {
        this.collider = collider;
        collider.setOwner(this);
    }
}
