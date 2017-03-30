import org.lwjgl.util.vector.Vector3f;

public class GameObject {
    private Model model;
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scale;

    public GameObject(Model model, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.model = model;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public GameObject(Model model) {
        this(model, new Vector3f(0f,0f,0f), new Vector3f(0f,0f,0f),
                new Vector3f(1f,1f,1f));
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
