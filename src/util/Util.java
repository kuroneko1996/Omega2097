package util;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Util {

    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        matrix.translate(translation);

        matrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1,0,0));
        matrix.rotate((float)Math.toRadians(rotation.y), new Vector3f(0,1,0));
        matrix.rotate((float)Math.toRadians(rotation.y), new Vector3f(0,0,1));

        matrix.scale(scale);
        return matrix;
    }
}
