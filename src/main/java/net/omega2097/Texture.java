package net.omega2097;


public class Texture {
    private int width;
    private int height;
    private int id;
    private String fileName;

    public Texture(String fileName, int width, int height, int id) {
        this.fileName = fileName;
        this.width = width;
        this.height = height;
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

}
