package net.omega2097;

import org.lwjgl.util.vector.Vector3f;

public class Collider {
    private BoundingBox box;
    public Collider(BoundingBox box) {
        this.box = box;
    }

    public Vector3f checkRectanglesOverlap(Collider other) {
        Vector3f result;
        Vector3f distance = Vector3f.sub(other.getPosition(), this.getPosition(), null);

        float thisExtent = (this.box.getMax().x - this.box.getMin().x) / 2.0f;
        float otherExtent = (other.box.getMax().x - other.box.getMin().x) / 2.0f;
        float xOverlap = thisExtent + otherExtent - Math.abs(distance.x);

        if (xOverlap > 0) {
            thisExtent = (this.box.getMax().z - this.box.getMin().z) / 2.0f;
            otherExtent = (other.box.getMax().z - other.box.getMin().z) / 2.0f;
            float zOverlap = thisExtent + otherExtent - Math.abs(distance.z);

            if (zOverlap > 0) {

                if (xOverlap < zOverlap) {
                    if (distance.x < 0) {
                        result = new Vector3f(-xOverlap, 0, 0);
                    } else {
                        result = new Vector3f(xOverlap, 0, 0);
                    }
                } else {
                    if (distance.z < 0) {
                        result = new Vector3f(0, 0, -zOverlap);
                    } else {
                        result = new Vector3f(0, 0, zOverlap);
                    }
                }
                //System.out.println("x=" + xOverlap + ", z=" + zOverlap + result.toString());
                return result;
            }
        }
        return null;
    }

    public Vector3f getPosition() {
        return box.getPosition();
    }

    public void setPosition(float x, float y, float z) {
        box.getPosition().set(x, y, z);
    }

    public BoundingBox getBox() {
        return box;
    }

    public void setBox(BoundingBox box) {
        this.box = box;
    }
}
