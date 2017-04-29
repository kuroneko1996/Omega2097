package net.omega2097.gui;

import net.omega2097.*;
import net.omega2097.util.Mesh;
import net.omega2097.util.MeshBuilder;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TextItem extends GameObject {
    private String text;
    private BitmapFont font;
    private ILoader loader;
    private Model model;
    private Mesh mesh;
    private MeshBuilder builder;
    private String charset;
    private float x1;
    private float y1;
    private float size;

    public TextItem(float x, float y, String text, BitmapFont font, String charset, float size, ILoader loader) {
        super();
        this.x1 = x;
        this.y1 = y;
        this.text = text;
        this.charset = charset;
        this.mesh = new Mesh();
        this.size = size;
        this.font = font;
        this.loader = loader;
        builder = new MeshBuilder();
        model = loader.load(buildMesh(font.getNumRows(), font.getNumCols()));
        setModel(model);
        setTextureName(font.getFileName());
        getModel().addTexture(font.getTexture());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;

        buildMesh(font.getNumRows(), font.getNumCols());
        loader.updateModel(model, mesh);
    }

    private Mesh buildMesh(int numRows, int numCols) {
        builder.clear();
        byte[] chars = text.getBytes(Charset.forName(charset));
        int numChars = chars.length;
        float zPos = 0.01f; // TODO options

        float tileWidth = (float)font.getTexture().getWidth() / (float)numCols * this.size;
        float tileHeight = (float)font.getTexture().getHeight() / (float)numRows * this.size;
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
