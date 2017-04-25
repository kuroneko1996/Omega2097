package net.omega2097;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Vertex {
    public Vector3f position;
    public Vector2f uv;
    public Vector3f norm;

    public Vertex(Vector3f position, Vector2f uv, Vector3f norm) {
        this.position = position;
        this.uv = uv;
        this.norm = norm;
    }

    public Vertex(Vector3f position, Vector2f uv) {
        this.position = position;
        this.uv = uv;
    }

    @Override
    public boolean equals(Object o){
        if (o == null)
            return false;
        if (!(o instanceof Vertex))
            return false;

        Vertex other = (Vertex) o;
        if (!this.position.equals(other.position))
            return false;

        if (this.uv == null && other.uv != null) {
            return false;
        }
        if (this.uv != null && other.uv == null) {
            return false;
        }
        if (this.uv == null && other.uv == null) {
            return true;
        }

        if (!this.uv.equals(other.uv))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int uvHashcode = (uv == null ? 1 : uv.hashCode());
        return position.hashCode() * uvHashcode;
    }
}
