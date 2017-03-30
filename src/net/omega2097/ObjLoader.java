package net.omega2097;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/*
Loads OBJ files
 */
public class ObjLoader {
    public static Model load(String fileName, Loader loader) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(new File("res/" + fileName + ".obj"));
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't load file! " + fileName + ".obj");
            e.printStackTrace();
        }
        BufferedReader bReader = new BufferedReader(fileReader);

        String line;
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        float[] verticesArray = null;
        int[] indicesArray = null;

        try {
            while ( (line = bReader.readLine()) != null) {
                String[] currentLine = line.split("\\s+");
                if (line.startsWith("v")) { // vertices
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
                                Float.parseFloat(currentLine[2]),
                                Float.parseFloat(currentLine[3]));
                    vertices.add(vertex);
                } else if (line.startsWith("vt")) { // textures
                    Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]),
                                Float.parseFloat(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn")) { // normals
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                } else if (line.startsWith("f")) { // faces section
                    for (int i = 1; i <= 3; i++) {
                        String[] vertexData = currentLine[i].split("/");
                        processVertex(vertexData, indices);
                    }
                }
            }

            bReader.close();
        } catch(Exception e) {
            e.printStackTrace();
        }


        // convert to vertices and indices arrays
        verticesArray = new float[vertices.size() * 3];
        indicesArray = new int[indices.size()];

        int vertexIndex = 0;
        for(Vector3f vertex:vertices) {
            verticesArray[vertexIndex++] = vertex.x;
            verticesArray[vertexIndex++] = vertex.y;
            verticesArray[vertexIndex++] = vertex.z;
        }
        for(int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }

        return loader.loadToVAO(verticesArray, indicesArray);
    }

    private static void processVertex(String[] vertexData, List<Integer> indices) {
        int currentVertexPosition = Integer.parseInt(vertexData[0]) - 1; // it starts from 1
        indices.add(currentVertexPosition);
    }
}
