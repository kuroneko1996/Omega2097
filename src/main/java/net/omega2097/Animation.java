package net.omega2097;

public class Animation {
    int[] frames;
    private int currentFrame;
    boolean loop = false;
    long lastFrameTime;
    int frameDuration;

    public Animation(int[] frames, boolean loop, int frameDuration) {
        this.frames = frames;
        this.loop = loop;
        this.frameDuration = frameDuration;
    }

    public int getCurrentTexture() {
        return frames[getCurrentFrame()];
    }

    public void reset() {
        lastFrameTime = System.currentTimeMillis();
        currentFrame = 0;
    }

    public int getTotalFrames() { return frames.length; }

    public int getFrameDuration() { return frameDuration; }

    private int getCurrentFrame() {
        if (lastFrameTime == 0) {
            lastFrameTime = System.currentTimeMillis();
        }

        if ((System.currentTimeMillis() - lastFrameTime) > frameDuration) {
            lastFrameTime = System.currentTimeMillis();
            currentFrame += 1;
            if ( currentFrame == frames.length) {
                if (loop) {
                    currentFrame = 0;
                } else {
                    currentFrame -= 1;
                }
            }
        }
        return currentFrame;
    }

}
