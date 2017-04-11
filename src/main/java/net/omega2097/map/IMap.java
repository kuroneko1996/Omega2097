package net.omega2097.map;

public interface IMap {
   public int getWidth();
   public int getHeight();

   public void init(int width, int height);
   public void setProperties(int x, int y, boolean isTransparent, boolean isWalkable);
}
