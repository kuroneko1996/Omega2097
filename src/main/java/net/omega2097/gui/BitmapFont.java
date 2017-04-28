package net.omega2097.gui;

import net.omega2097.ILoader;

public class BitmapFont {
    private String fileName;
    private int numRows;
    private int numCols;
    private ILoader loader;

    private int textureWidth = 256;
    private int textureHeight = 256;
    private int textureID;

    public BitmapFont(String fileName, int numRows, int numCols, ILoader loader) {
        this.fileName = fileName;
        this.numRows = numRows;
        this.numCols = numCols;

        this.loader = loader;
        this.textureID = loader.loadTexture(fileName);
    }


    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getTextureWidth() {
        return textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }

    public String getFileName() {
        return fileName;
    }

    public int getTextureID() {
        return textureID;
    }
}
