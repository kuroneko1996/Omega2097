package net.omega2097;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/*
Loads OBJ files
 */
public class ObjLoader {
    private List<Vector3f> vertices = new ArrayList<>();
    private List<Vector2f> textures = new ArrayList<>();
    private List<Vector3f> normals = new ArrayList<>();
    private List<Integer> indices = new ArrayList<>();

    private HashMap<Integer, Vertex> uniqueVertices = new HashMap<>();

    private int vertexNum = 0;

    public Model load(String fileName, Loader loader) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(new File(fileName + ".obj"));
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't load file! " + fileName + ".obj");
            e.printStackTrace();
        }
        BufferedReader bReader = new BufferedReader(fileReader);

        String line;

        try {
            while ( (line = bReader.readLine()) != null) {
                String[] currentLine = line.split("\\s+");
                if (line.startsWith("v ")) { // vertices
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
                                Float.parseFloat(currentLine[2]),
                                Float.parseFloat(currentLine[3]));
                    vertices.add(vertex);
                } else if (line.startsWith("vt ")) { // textures
                    Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]),
                                Float.parseFloat(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) { // normals
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                } else if (line.startsWith("f ")) { // faces section
                    for (int i = 1; i <= 3; i++) {
                        String[] vertexData = currentLine[i].split("/");
                        processVertex(vertexData);
                    }
                }
            }

            bReader.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        // convert to vertices and indices arrays
        float[] positionsArray = new float[uniqueVertices.size() * 3];
        float[] uvsArray = new float[uniqueVertices.size() * 2];

        int vertexIndex = 0;
        for(Map.Entry<Integer, Vertex> entry : uniqueVertices.entrySet()) {
            Vertex vertex = entry.getValue();
            positionsArray[vertexIndex*3] = vertex.position.x;
            positionsArray[vertexIndex*3+1] = vertex.position.y;
            positionsArray[vertexIndex*3+2] = vertex.position.z;

            if (vertex.uv != null) {
                uvsArray[vertexIndex * 2] = vertex.uv.x;
                uvsArray[vertexIndex * 2 + 1] = 1 - vertex.uv.y;
            }

            vertexIndex++;
        }


        int[] indicesArray = new int[indices.size()];

        for(int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }

        return loader.loadToVAO(positionsArray, uvsArray, indicesArray);
    }

    private void processVertex(String[] vertexData) {
        int currentVertexPosition = Integer.parseInt(vertexData[0]) - 1; // it starts from 1

        Vector2f currentTexture = null;
        int currentTexturePosition;
        if (!vertexData[1].equals("")) {
            currentTexturePosition = Integer.parseInt(vertexData[1]) - 1;
            currentTexture = textures.get(currentTexturePosition);
        }
        Vector3f currentVertex = vertices.get(currentVertexPosition);

        Vertex vertex = new Vertex(currentVertex, currentTexture);
        if (!uniqueVertices.containsValue(vertex)) {
            uniqueVertices.put(vertexNum, vertex);
            indices.add(vertexNum);
            vertexNum++;
        } else {
            for(Map.Entry<Integer, Vertex> entry: uniqueVertices.entrySet()){
                if(vertex.equals(entry.getValue())){
                    indices.add(entry.getKey());
                    break;
                }
            }
        }

    }
}
