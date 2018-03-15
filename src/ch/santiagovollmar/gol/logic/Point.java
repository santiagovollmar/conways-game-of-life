package ch.santiagovollmar.gol.logic;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class Point implements Callable<Object>, Serializable {
  private static final long serialVersionUID = 741323992140637769L;
  
  public int x;
  public int y;
  
  public Point(int x, int y) throws IllegalArgumentException {
    if (x > 90000 || y > 90000) {
      throw new IllegalArgumentException("Coordinates must not exceed 90000");
    }
    
    this.x = x;
    this.y = y;
  }
  
  @Override
  public int hashCode() {
    return x * 90000 + y;
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof Point) {
      if (((Point)other).x == x && ((Point)other).y == y)
        return true;
    }
    return false;
  }
  
  @Override
  public String toString() {
    return "Point[x=" + x + ", y=" + y + "];";
  }

  public byte[] getBytes() {
    byte[] bytes = new byte[8];
    
    for (int i = 0; i < 4; i++) {
      bytes[i] = (byte) ((x >>> (i * 8)) & 0xFF);
    }
    
    for (int i = 0; i < 4; i++) {
      bytes[i + 4] = (byte) ((y >>> (i * 8)) & 0xFF);
    }
    
    return bytes;
  }
  
  @Override
  public Object call() throws Exception {
    LogicManager.compute(this);
    return null;
  }
  
  
}
