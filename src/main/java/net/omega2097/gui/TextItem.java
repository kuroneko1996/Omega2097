package net.omega2097.gui;

import net.omega2097.Engine;
import net.omega2097.GameObject;
import net.omega2097.Loader;
import net.omega2097.Model;
import net.omega2097.util.Mesh;
import net.omega2097.util.MeshBuilder;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TextItem extends GameObject {
    private String text;
    private final int numCols;
    private final int numRows;
    private Loader loader;
    private Model model;
    private Mesh mesh;
    private MeshBuilder builder;
    private String charset;
    private float x1;
    private float y1;
    private float size;
    private int textureWidth = 256;
    private int textureHeight = 256;

    public TextItem(float x, float y, String text, String fontTexture, int numRows, int numCols, String charset, float sizePercent) {
        super();
        this.x1 = x;
        this.y1 = y;
        this.text = text;
        this.numCols = numCols;
        this.numRows = numRows;
        this.charset = charset;
        this.mesh = new Mesh();
        this.size = sizePercent;

        loader = Engine.getInstance().getLoader();
        builder = new MeshBuilder();
        model = loader.loadToVAO(buildMesh(numRows, numCols));
        setModel(model);
        setTextureName(fontTexture);
        getModel().addTextureID(loader.loadTexture("res/" + fontTexture));
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;

        buildMesh(numCols, numRows);
        loader.updateModelInVAO(model, mesh);
    }

    private Mesh buildMesh(int numRows, int numCols) {
        builder.clear();
        byte[] chars = text.getBytes(Charset.forName(charset));
        int numChars = chars.length;
        float zPos = 0.01f; // TODO options

        float tileWidth = (float)textureWidth / (float)numCols * this.size;
        float tileHeight = (float)textureHeight / (float)numRows * this.size;
        List<Vector2f> texCoordinates = new ArrayList<>();

        for(int i = 0; i < numChars; i++) {
            byte currentChar = (byte)(chars[i] - 32);
            int col = currentChar % numCols;
            int row = currentChar / numCols;
            float x = x1 + i * tileWidth;
            float y = y1;

            texCoordinates.clear();

            // left top
            texCoordinates.add(new Vector2f((float)col / (float)numCols, (float)(row + 1) / (float)numRows));
            // left bottom
            texCoordinates.add(new Vector2f((float)col / (float)numCols, (float)row / (float)numRows));
            // right bottom
            texCoordinates.add(new Vector2f((float)(col + 1)/ (float)numCols, (float)row / (float)numRows));
            // right top
            texCoordinates.add(new Vector2f((float)(col + 1)/ (float)numCols, (float)(row + 1) / (float)numRows));

            builder.buildQuad(new Vector3f(x, y, zPos), new Vector3f(tileWidth,0,zPos),
                    new Vector3f(0, tileHeight, zPos), texCoordinates);
        }
        return builder.updateMesh(mesh);
    }
}
