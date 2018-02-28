package ch.santiagovollmar.gol;

public class Point {
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
  
  
}
