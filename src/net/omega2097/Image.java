package net.omega2097;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load;

public class Image {
    private final ByteBuffer image;
    private int width;
    private int height;

    public int getChannels() {
        return channels;
    }

    private int channels;

    public ByteBuffer getImage() {
        return image;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image(String fileName) {
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer ch = BufferUtils.createIntBuffer(1);

        image = stbi_load(fileName, w, h, ch, 0);
        if (image == null) {
            throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
        }

        width = w.get(0);
        height = h.get(0);
        channels = ch.get(0);
    }
}
