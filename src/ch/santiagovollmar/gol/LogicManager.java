package ch.santiagovollmar.gol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.sun.glass.ui.TouchInputSupport;

public class LogicManager {
  private static boolean isPaused = false;
  
  public static synchronized void renderNext() {
    if (!isPaused) {
      GridManager.consume();
      GridManager.update();
    }
  }
  
  private static Set<Point> checkedNeighbors = Collections.synchronizedSet(new HashSet<Point>());
  public static void clearCheckedCache() {
    checkedNeighbors.clear();
  }
  
  
  public static void compute(Point point) {
    // get amount of neighbors
    int neighborAmount = 0;
    ArrayList<Point> neighbors = GridManager.getNeighborCoordinates(point);
    for (Point neighbor : neighbors) {
      neighborAmount += GridManager.isAlive(neighbor) ? 1 : 0;
    }
    
    // check if cell survives
    if (neighborAmount < 2 || neighborAmount > 3) { // cell dies
      GridManager.clear(point, true);
    }
    
    // check if any near, dead cells get born
    for (Point neighbor : neighbors) {
      if (checkedNeighbors.contains(neighbor)) {
        continue;
      }
      checkedNeighbors.add(neighbor);
      
      // get amount of neighbors
      int subNeighborAmount = 0;
      ArrayList<Point> subNeighbors = GridManager.getNeighborCoordinates(neighbor);
      for (Point subNeighbor : subNeighbors) {
        subNeighborAmount += GridManager.isAlive(subNeighbor) ? 1 : 0;
      }
      
      if (subNeighborAmount == 3) { // cell gets born
        GridManager.fill(neighbor, true);
      }
    }
  }
  
  public static synchronized void setPaused(boolean paused) {
    isPaused = paused;
    MenuBar.getCurrentInstance().setPaused(paused);
  }
  
  public static synchronized boolean isPaused() {
    return isPaused;
  }
}
