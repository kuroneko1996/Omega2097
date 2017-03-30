package net.omega2097.util;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import net.omega2097.Camera;

public class Util {

    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
        Matrix4f matrix = new Matrix4f();
        updateTransformationMatrix(matrix, translation, rotation, scale);
        return matrix;
    }

    public static void updateTransformationMatrix(Matrix4f matrix,
                                                  Vector3f translation, Vector3f rotation, Vector3f scale)
    {
        matrix.setIdentity();
        matrix.translate(translation);

        matrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1,0,0));
        matrix.rotate((float)Math.toRadians(rotation.y), new Vector3f(0,1,0));
        matrix.rotate((float)Math.toRadians(rotation.z), new Vector3f(0,0,1));

        matrix.scale(scale);
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setIdentity();
        updateViewMatrix(viewMatrix, camera.getPosition(), camera.getPitch(), camera.getYaw());
        return viewMatrix;
    }

    public static void updateViewMatrix(Matrix4f viewMatrix, Vector3f cameraPos, float pitch, float yaw) {
        viewMatrix.setIdentity();
        viewMatrix.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0));
        viewMatrix.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0));

        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        viewMatrix.translate(negativeCameraPos);
    }
}
