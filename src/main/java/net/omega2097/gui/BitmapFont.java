package net.omega2097.gui;

import net.omega2097.ILoader;
import net.omega2097.Texture;

public class BitmapFont {
    private String fileName;
    private int numRows;
    private int numCols;
    private ILoader loader;

    private Texture texture;

    public BitmapFont(String fileName, int numRows, int numCols, ILoader loader) {
        this.fileName = fileName;
        this.numRows = numRows;
        this.numCols = numCols;

        this.loader = loader;
        this.texture = loader.loadTexture(fileName);
    }


    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public String getFileName() {
        return fileName;
    }

    public Texture getTexture() {
        return texture;
    }
}
