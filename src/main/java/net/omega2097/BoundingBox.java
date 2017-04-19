package net.omega2097;

import org.lwjgl.util.vector.Vector3f;

public class BoundingBox {
    private Vector3f position = new Vector3f(0,0,0);
    private Vector3f size;
    private Vector3f max = new Vector3f(0,0,0);

    public BoundingBox(Vector3f position, Vector3f size) {
        this.position = position;
        this.size = size;
        this.max = getMax();
    }

    public Vector3f getSize() {
        return size;
    }

    public void setSize(Vector3f size) {
        this.size = size;
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getMin() {
        return position;
    }

    public Vector3f getCenter() {
        return new Vector3f(position.x + size.x / 2.0f, position.y + size.y / 2.0f, position.z + size.z / 2.0f);
    }

    public Vector3f getMax() {
        Vector3f.add(this.position, this.size, max);
        return max;
    }
}
