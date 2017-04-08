package net.omega2097;

public class Window {
    public long id;
    public int width;
    public int height;
    private boolean mouseLocked = false;

    public Window(long id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public boolean isMouseLocked() {
        return mouseLocked;
    }

    public void setMouseLocked(boolean mouseLocked) {
        this.mouseLocked = mouseLocked;
    }
}
