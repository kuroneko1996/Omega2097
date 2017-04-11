package net.omega2097.map;

public class Rectangle {
    private int width;

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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getRight() {
        return this.getX() + this.getWidth();
    }
    public int getBottom() {
        return this.getY() + this.getHeight();
    }

    private int height;
    private int x;
    private int y;

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean intersects( Rectangle value )
    {
        boolean result = false;
        if ( value == null )
        {
            return(false);
        }

        result = value.getX() < getRight() && getX() < value.getRight()
                && value.getY() < getBottom() && getY() < value.getBottom();
        return result;
    }
    public int getCenterX() {
        return(getX() + ( getWidth() / 2 ));
    }
    public int getCenterY() {
        return(getY() + ( getHeight() / 2 ));
    }
}
