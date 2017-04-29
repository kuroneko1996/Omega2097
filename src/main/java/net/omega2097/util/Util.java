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

    /**
     * Outputs a hit coordinate to the last param - hitCoord
     * @param rayOrigin
     * @param rayDir
     * @param bboxMin
     * @param bboxMax
     */
    public static Vector3f hitBoundingBox(Vector3f rayOrigin, Vector3f rayDir, Vector3f bboxMin, Vector3f bboxMax) {
        final byte RIGHT = 0;
        final byte LEFT = 1;
        final byte MIDDLE = 2;

        boolean inside = true;
        byte numDim = 3;
        byte quadrants[] = new byte[numDim];
        byte whichPlane;
        double tMax[] = new double[numDim];
        double candidatePlanes[] = new double[numDim];
        double coord[] = new double[numDim];

        double origin[] = {rayOrigin.x, rayOrigin.y, rayOrigin.z};
        double dir[] = {rayDir.x, rayDir.y, rayDir.z};
        double bMin[] = {bboxMin.x, bboxMin.y, bboxMin.z};
        double bMax[] = {bboxMax.x, bboxMax.y, bboxMax.z};

        // Find candidate planes
        for (byte i = 0; i < numDim; i++) {
            if (origin[i] < bMin[i]) {
                quadrants[i] = LEFT;
                candidatePlanes[i] = bMin[i];
                inside = false;
            } else if (origin[i] > bMax[i]) {
                quadrants[i] = RIGHT;
                candidatePlanes[i] = bMax[i];
                inside = false;
            } else {
                quadrants[i] = MIDDLE;
            }
        }

        // Ray origin inside bounding box
        if (inside) {
            coord = origin;
            return new Vector3f((float)coord[0], (float)coord[1], (float)coord[2]);
        }

        // Calculate t distances to candidate planes
        for (byte i = 0; i < numDim; i++) {
            if (quadrants[i] != MIDDLE && dir[i] != 0)
                tMax[i] = (candidatePlanes[i] - origin[i]) / dir[i];
            else
                tMax[i] = -1;
        }

        // Get largest of tMaxes
        whichPlane = 0;
        for (byte i = 0; i < numDim; i++) {
            if (tMax[whichPlane] < tMax[i]) {
                whichPlane = i;
            }
        }
        // check if final candidate is inside box
        if (tMax[whichPlane] < 0) return null;
        for (byte i = 0; i < numDim; i++) {
            if (whichPlane != i) {
                coord[i] = origin[i] + tMax[whichPlane] * dir[i]; // line equation
                if (coord[i] < bMin[i] || coord[i] > bMax[i]) // out of bounding box
                    return null;
            } else {
                coord[i] = candidatePlanes[i];
            }
        }

        return new Vector3f((float)coord[0], (float)coord[1], (float)coord[2]);
    }

    public static Vector3f rotateY(Vector3f vec, float angle) {
        float cosAngle = (float)Math.cos(angle);
        float sinAngle = (float)Math.sin(angle);
        return new Vector3f(
                -vec.x * cosAngle + vec.z * sinAngle,
                vec.y,
                vec.x * sinAngle + vec.z * cosAngle
        );
    }

    public static Vector3f rotateX(Vector3f vec, float angle) {
        float cosAngle = (float)Math.cos(angle);
        float sinAngle = (float)Math.sin(angle);
        return new Vector3f(
                vec.x,
                vec.z * sinAngle + vec.y * cosAngle,
                vec.z* cosAngle - vec.y * sinAngle

        );
    }
}
