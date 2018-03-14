package ch.santiagovollmar.gol;

import java.util.concurrent.Callable;

public class Point implements Callable<Object> {
  int x;
  int y;
  
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

  @Override
  public Object call() throws Exception {
    LogicManager.compute(this);
    return null;
  }
  
  
}
