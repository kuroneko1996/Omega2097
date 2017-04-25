package net.omega2097.renderers;


abstract class Renderer {
    float screenAspectRatio;
    float screenWidth;
    float screenHeight;

    Renderer(float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screenAspectRatio = screenWidth / screenHeight;
    }

    abstract void render();
}
